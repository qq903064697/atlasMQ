package cn.atlas.atlasmq.nameserver.common;

import cn.atlas.atlasmq.common.dto.NodeAckDTO;
import cn.atlas.atlasmq.common.dto.SlaveAckDTO;
import cn.atlas.atlasmq.nameserver.core.PropertiesLoader;
import cn.atlas.atlasmq.nameserver.replication.ReplicationTask;
import cn.atlas.atlasmq.nameserver.store.ReplicationMsgQueueManager;
import cn.atlas.atlasmq.nameserver.store.ReplicationChannelManager;
import cn.atlas.atlasmq.nameserver.store.ServiceInstanceManager;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午3:12
 * @Version 1.0
 */
public class CommonCache {

    private static PropertiesLoader propertiesLoader = new PropertiesLoader();
    private static ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
    private static NameserverProperties nameserverProperties = new NameserverProperties();
    private static ReplicationChannelManager replicationChannelManager = new ReplicationChannelManager();
    private static ReplicationTask replicationTask;
    private static Channel connectNodeChannel = null;
    private static Channel preNodeChannel = null;

    private static ReplicationMsgQueueManager replicationMsgQueueManager = new ReplicationMsgQueueManager();

    private static Map<String, NodeAckDTO> nodeAckMap = new ConcurrentHashMap<>();
    private static Map<String, SlaveAckDTO> ackMap = new ConcurrentHashMap<>();

    public static Map<String, NodeAckDTO> getNodeAckMap() {
        return nodeAckMap;
    }

    public static void setNodeAckMap(Map<String, NodeAckDTO> nodeAckMap) {
        CommonCache.nodeAckMap = nodeAckMap;
    }

    public static Channel getPreNodeChannel() {
        return preNodeChannel;
    }

    public static void setPreNodeChannel(Channel preNodeChannel) {
        CommonCache.preNodeChannel = preNodeChannel;
    }

    public static Map<String, SlaveAckDTO> getAckMap() {
        return ackMap;
    }

    public static void setAckMap(Map<String, SlaveAckDTO> ackMap) {
        CommonCache.ackMap = ackMap;
    }

    public static ReplicationMsgQueueManager getReplicationMsgQueueManager() {
        return replicationMsgQueueManager;
    }

    public static void setReplicationMsgQueueManager(ReplicationMsgQueueManager replicationMsgQueueManager) {
        CommonCache.replicationMsgQueueManager = replicationMsgQueueManager;
    }

    public static ReplicationTask getReplicationTask() {
        return replicationTask;
    }

    public static void setReplicationTask(ReplicationTask replicationTask) {
        CommonCache.replicationTask = replicationTask;
    }

    public static Channel getConnectNodeChannel() {
        return connectNodeChannel;
    }

    public static void setConnectNodeChannel(Channel connectNodeChannel) {
        CommonCache.connectNodeChannel = connectNodeChannel;
    }

    public static ReplicationChannelManager getReplicationChannelManager() {
        return replicationChannelManager;
    }

    public static void setReplicationChannelManager(ReplicationChannelManager replicationChannelManager) {
        CommonCache.replicationChannelManager = replicationChannelManager;
    }

    public static NameserverProperties getNameserverProperties() {
        return nameserverProperties;
    }

    public static void setNameserverProperties(NameserverProperties nameserverProperties) {
        CommonCache.nameserverProperties = nameserverProperties;
    }

    public static ServiceInstanceManager getServiceInstanceManager() {
        return serviceInstanceManager;
    }

    public static void setServiceInstanceManager(ServiceInstanceManager serviceInstanceManager) {
        CommonCache.serviceInstanceManager = serviceInstanceManager;
    }

    public static PropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }

    public static void setPropertiesLoader(PropertiesLoader propertiesLoader) {
        CommonCache.propertiesLoader = propertiesLoader;
    }
}
