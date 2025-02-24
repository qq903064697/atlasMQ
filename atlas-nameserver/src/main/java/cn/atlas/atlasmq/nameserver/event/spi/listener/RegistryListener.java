package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.enums.ReplicationMsgTypeEnum;
import cn.atlas.atlasmq.nameserver.event.model.RegistryEvent;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * @Author xiaoxin
 * @Description
 */
public class RegistryListener implements Listener<RegistryEvent> {

    @Override
    public void onReceive(RegistryEvent event) throws IllegalAccessException {
        // 安全认证
        String rightUser = CommonCache.getPropertiesLoader().getProperty("nameserver.user");
        String rightPassword = CommonCache.getPropertiesLoader().getProperty("nameserver.password");
        ChannelHandlerContext channelHandlerContext = event.getChannelHandlerContext();
        if (!rightUser.equals(event.getUser()) || !rightPassword.equals(event.getPassword())) {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode(), NameServerResponseCode.ERROR_USER_OR_PASSWORD.getDesc().getBytes());
            channelHandlerContext.writeAndFlush(tcpMsg);
            channelHandlerContext.close();
            throw new IllegalAccessException("error account to connected!");
        }
        System.out.println("注册事件接收：" + event);
        channelHandlerContext.attr(AttributeKey.valueOf("reqId")).set(event.getBrokerIp() + ":" + event.getBrokerPort());
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setBrokerIp(event.getBrokerIp());
        serviceInstance.setBrokerPort(event.getBrokerPort());
        serviceInstance.setFirstRegistryTime(System.currentTimeMillis());
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        // 如果当前是主从复制模式，当前角色是主节点，那么就往队列里面放元素
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
        replicationMsgEvent.setChannelHandlerContext(event.getChannelHandlerContext());
        replicationMsgEvent.setType(ReplicationMsgTypeEnum.REGISTRY.getCode());
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
        TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.REGISTRY_SUCCESS.getCode(), NameServerResponseCode.REGISTRY_SUCCESS.getDesc().getBytes());
        channelHandlerContext.writeAndFlush(tcpMsg);
        //同步给到从节点，比较严谨的同步，binlog类型，对于数据的顺序性要求很高
        //可能是无顺序的状态
        //把同步的数据塞入一条队列当中，专门有一条线程从队列当中提取数据，同步给各个从节点

    }

//    public static void main(String[] args) throws IOException {
////        String eagleMqHome = System.getenv(BrokerConstants.ATLAS_MQ_HOME);
//        String atlasMqHome = "D:/Sync/Study/项目/atlasMQ/代码/atlasmq";
//        Properties properties = new Properties();
//        properties.load(new FileInputStream(new File(atlasMqHome+"/broker/config/nameserver.properties")));
//        String user = properties.getProperty("nameserver.user");
//        String password = properties.getProperty("nameserver.password");
//        System.out.println(user);
//        System.out.println(password);
//    }
}
