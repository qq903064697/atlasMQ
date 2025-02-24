package cn.atlas.atlasmq.nameserver.handler;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.EventBus;
import cn.atlas.atlasmq.nameserver.event.model.*;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 当前节点接受外界写入数据处理器
 */
@ChannelHandler.Sharable
public class NodeWriteMsgReplicationServerHandler extends SimpleChannelInboundHandler {
    private EventBus eventBus;

    public NodeWriteMsgReplicationServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.init();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        //从节点发起链接，在master端通过密码验证，建立链接
        Event event = null;
        if (NameServerEventCode.NODE_REPLICATION_MSG.getCode() == code) {
            event = JSON.parseObject(body, NodeReplicationMsgEvent.class);

        } else if (NameServerEventCode.SLAVE_HEART_BEAT.getCode() == code) {
            event = new SlaveHeartBeatEvent();
        } else if (NameServerEventCode.SLAVE_REPLICATION_ACK_MSG.getCode() == code) {
            event = JSON.parseObject(body, SlaveReplicationMsgAckEvent.class);
        }
        event.setChannelHandlerContext(channelHandlerContext);
        CommonCache.setPreNodeChannel(channelHandlerContext.channel());
        eventBus.publish(event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
