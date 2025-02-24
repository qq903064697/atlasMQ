package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.HeartBeatEvent;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @Author xiaoxin
 * @Description
 */
public class HeartBeatListener implements Listener<HeartBeatEvent> {

    @Override
    public void onReceive(HeartBeatEvent event) throws IllegalAccessException {
        // 把存在的实例保存下载
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        // 之前做过认证
        Object reqId = channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get();
        if (reqId == null) {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(), NameServerResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        System.out.println("接收到心跳数据：" + event);
        // 心跳，客户端每隔3秒发送一次
        String brokerIdentifyStr = (String) reqId;
        String[] split = brokerIdentifyStr.split(":");
        long currentTimestamp = System.currentTimeMillis();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerIp(split[0]);
        serviceInstance.setBrokerPort(Integer.valueOf(split[1]));
        serviceInstance.setLastHeartBeatTime(currentTimestamp);
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);

        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);

    }
}
