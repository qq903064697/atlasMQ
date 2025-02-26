package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.HeartBeatDTO;
import cn.atlas.atlasmq.common.dto.ServiceRegistryRespDTO;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.common.event.Listener;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.enums.ReplicationMsgTypeEnum;
import cn.atlas.atlasmq.nameserver.event.model.HeartBeatEvent;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Author xiaoxin
 * @Description
 */
public class HeartBeatListener implements Listener<HeartBeatEvent> {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);


    @Override
    public void onReceive(HeartBeatEvent event) throws IllegalAccessException {
        // 把存在的实例保存下载
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        // 之前做过认证
        Object reqId = channelHandlerContext.attr(AttributeKey.valueOf("reqId")).get();
        if (reqId == null) {
            ServiceRegistryRespDTO serviceRegistryRespDTO = new ServiceRegistryRespDTO();
            serviceRegistryRespDTO.setMsgId(event.getMsgId());
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(),
                    JSON.toJSONBytes(serviceRegistryRespDTO));
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        logger.info("接收到心跳数据：{}",JSON.toJSONString(event));
        // 心跳，客户端每隔3秒发送一次
        String IdentifyStr = (String) reqId;
        String[] split = IdentifyStr.split(":");
        long currentTimestamp = System.currentTimeMillis();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setIp(split[0]);
        serviceInstance.setPort(Integer.valueOf(split[1]));
        serviceInstance.setLastHeartBeatTime(currentTimestamp);
        System.out.println(JSON.toJSONString(serviceInstance));
        HeartBeatDTO heartBeatDTO = new HeartBeatDTO();
        heartBeatDTO.setMsgId(event.getMsgId());
        CommonCache.getServiceInstanceManager().putIfExist(serviceInstance);
        channelHandlerContext.writeAndFlush(new TcpMsg(NameServerResponseCode.HEART_BEAT_SUCCESS.getCode(), JSON.toJSONBytes(heartBeatDTO)));

        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
        replicationMsgEvent.setChannelHandlerContext(event.getChannelHandlerContext());
        replicationMsgEvent.setType(ReplicationMsgTypeEnum.HEART_BEAT.getCode());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);

    }
}
