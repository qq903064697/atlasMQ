package cn.atlas.atlasmq.nameserver.event.spi.listener;


import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.UnRegistryEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.internal.StringUtil;

/**
 * @Author xiaoxin
 * @Description
 */
public class UnRegistryListener implements Listener<UnRegistryEvent>{

    @Override
    public void onReceive(UnRegistryEvent event) throws IllegalAccessException {
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        Object reqId = channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get();
        if (reqId == null) {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(),
                    NameServerResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        System.out.println("连接断开处理");
        String brokerIdentifyStr = (String) reqId;
        boolean removeStatus = CommonCache.getServiceInstanceManager().remove(brokerIdentifyStr) != null;
        if (removeStatus) {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.UN_REGISTRY_SERVICE.getCode(),
                    NameServerResponseCode.UN_REGISTRY_SERVICE.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
        }
    }
}
