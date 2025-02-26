package cn.atlas.atlasmq.broker.netty.broker;

import cn.atlas.atlasmq.broker.event.model.ConsumeMsgAckEvent;
import cn.atlas.atlasmq.broker.event.model.ConsumerMsgEvent;
import cn.atlas.atlasmq.broker.event.model.PushMsgEvent;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.ConsumeMsgAckReqDTO;
import cn.atlas.atlasmq.common.dto.ConsumerMsgReqDTO;
import cn.atlas.atlasmq.common.dto.MessageDTO;
import cn.atlas.atlasmq.common.enums.BrokerEventCode;
import cn.atlas.atlasmq.common.event.model.Event;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cn.atlas.atlasmq.common.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Description
 */
@ChannelHandler.Sharable
public class BrokerServerHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(BrokerServerHandler.class);

    private EventBus eventBus;

    public BrokerServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event = null;
        if (BrokerEventCode.PUSH_MSG.getCode() == code) {
            MessageDTO messageDTO = com.alibaba.fastjson2.JSON.parseObject(body, MessageDTO.class);
            PushMsgEvent pushMsgEvent = new PushMsgEvent();
            pushMsgEvent.setMessageDTO(messageDTO);
            logger.info("收到消息推送内容:{},message is {}", new String(messageDTO.getBody()), JSON.toJSONString(messageDTO));
            event = pushMsgEvent;
        } else if (BrokerEventCode.CONSUME_MSG.getCode() == code) {
            ConsumerMsgReqDTO consumerMsgReqDTO = JSON.parseObject(body, ConsumerMsgReqDTO.class);
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
//            registryEvent.setIp(inetSocketAddress.getHostString());
            consumerMsgReqDTO.setIp("127.0.0.1");
            consumerMsgReqDTO.setPort(inetSocketAddress.getPort());
            ConsumerMsgEvent consumerMsgEvent = new ConsumerMsgEvent();
            consumerMsgEvent.setConsumerMsgReqDTO(consumerMsgReqDTO);
            consumerMsgEvent.setMsgId(consumerMsgReqDTO.getMsgId());
            event = consumerMsgEvent;
        } else if (BrokerEventCode.CONSUME_SUCCESS_MSG.getCode() == code) {
            ConsumeMsgAckReqDTO consumeMsgAckReqDTO = JSON.parseObject(body, ConsumeMsgAckReqDTO.class);
            ConsumeMsgAckEvent consumeMsgAckEvent = new ConsumeMsgAckEvent();
            consumeMsgAckEvent.setConsumeMsgAckReqDTO(consumeMsgAckReqDTO);
            consumeMsgAckEvent.setMsgId(consumeMsgAckReqDTO.getMsgId());
            event = consumeMsgAckEvent;
        }
        event.setChannelHandlerContext(channelHandlerContext);
        eventBus.publish(event);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("new connection build");
    }
}
