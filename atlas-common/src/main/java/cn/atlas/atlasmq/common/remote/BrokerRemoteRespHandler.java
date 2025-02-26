package cn.atlas.atlasmq.common.remote;

import cn.atlas.atlasmq.common.cache.BrokerServerSyncFutureManager;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.ConsumeMsgAckRespDTO;
import cn.atlas.atlasmq.common.dto.ConsumerMsgBaseRespDTO;
import cn.atlas.atlasmq.common.dto.SendMessageToBrokerResponseDTO;
import cn.atlas.atlasmq.common.enums.BrokerResponseCode;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xiaoxin
 * @Description
 */
@ChannelHandler.Sharable
public class BrokerRemoteRespHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (BrokerResponseCode.SEND_MSG_RESP.getCode() == code) {
            SendMessageToBrokerResponseDTO sendMessageToBrokerResponseDTO = JSON.parseObject(body, SendMessageToBrokerResponseDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(sendMessageToBrokerResponseDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        }else if (BrokerResponseCode.CONSUME_MSG_RESP.getCode() == code) {
            ConsumerMsgBaseRespDTO consumerMsgBaseRespDTO = JSON.parseObject(body, ConsumerMsgBaseRespDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumerMsgBaseRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        }else if (BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode() == code) {
            ConsumeMsgAckRespDTO consumeMsgAckRespDTO = JSON.parseObject(body, ConsumeMsgAckRespDTO.class);
            SyncFuture syncFuture = BrokerServerSyncFutureManager.get(consumeMsgAckRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        }
    }
}
