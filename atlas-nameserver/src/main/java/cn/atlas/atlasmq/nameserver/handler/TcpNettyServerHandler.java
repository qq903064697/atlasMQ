package cn.atlas.atlasmq.nameserver.handler;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.event.EventBus;
import cn.atlas.atlasmq.nameserver.event.model.Event;
import cn.atlas.atlasmq.nameserver.event.model.HeartBeatEvent;
import cn.atlas.atlasmq.nameserver.event.model.RegistryEvent;
import cn.atlas.atlasmq.nameserver.event.model.UnRegistryEvent;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description:
 */
@ChannelHandler.Sharable
public class TcpNettyServerHandler extends SimpleChannelInboundHandler {

    private EventBus eventBus;

    public TcpNettyServerHandler() {
    }
    public TcpNettyServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.init();
    }

    // 1.网络请求的接受（netty完成）
    // 2.事件发布器的实现（EventBuf -> event）
    // 3.事件处理器的实现 （Listener -> 处理event）
    // 4.数据存储 （基于Map本地内存的方式存储）
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        // 解析成特定的事件，然后发送事件消息出去
        System.out.println("resp:" +  tcpMsg);
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        Event event = null;
        if (NameServerEventCode.REGISTRY.getCode() == code) {
            // 注册事件
            event = JSON.parseObject(body, RegistryEvent.class);
        }  else if (NameServerEventCode.HEART_BEAT.getCode() == code) {
            // 心跳事件
            event = new HeartBeatEvent();
        }
        event.setChannelHandlerContext(channelHandlerContext);
        eventBus.publish(event);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 如果依赖任务剔除节点，会有三个心跳周期的延迟，需要做到连接断开立马剔除的效果
        UnRegistryEvent unRegistryEvent = new UnRegistryEvent();
        unRegistryEvent.setChannelHandlerContext(ctx);
        eventBus.publish(unRegistryEvent);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
