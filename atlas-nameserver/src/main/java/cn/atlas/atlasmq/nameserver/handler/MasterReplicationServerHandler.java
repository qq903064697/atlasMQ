package cn.atlas.atlasmq.nameserver.handler;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.event.EventBus;
import cn.atlas.atlasmq.common.event.model.Event;
import cn.atlas.atlasmq.nameserver.event.model.SlaveHeartBeatEvent;
import cn.atlas.atlasmq.nameserver.event.model.SlaveReplicationMsgAckEvent;
import cn.atlas.atlasmq.nameserver.event.model.StartReplicationEvent;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @Author xiaoxin
 * @Description 主从架构下的复制handler
 */
@ChannelHandler.Sharable
public class MasterReplicationServerHandler extends SimpleChannelInboundHandler {

    private EventBus eventBus;

    public MasterReplicationServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.init();
    }

    //1.网络请求的接收(netty完成)
    //2.事件发布器的实现（EventBus-》event）Spring的事件，Google Guaua
    //3.事件处理器的实现（Listener-》处理event）
    //4.数据存储（基于Map本地内存的方式存储）
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        //从节点发起链接，在master端通过密码验证，建立链接
        Event event = null;
        if (NameServerEventCode.START_REPLICATION.getCode() == code) {
            event = JSON.parseObject(body, StartReplicationEvent.class);
        } else if (NameServerEventCode.SLAVE_HEART_BEAT.getCode() == code) {
            event = new SlaveHeartBeatEvent();
        } else if (NameServerEventCode.SLAVE_REPLICATION_ACK_MSG.getCode() == code) {
            event = JSON.parseObject(body, SlaveReplicationMsgAckEvent.class);
        }
        //channelHandlerContext -》 map
        //链接建立完成后，master收到的数据，同步发送给slave节点
        //channelHandlerContext.writeAndFlush();
        event.setChannelHandlerContext(channelHandlerContext);
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
