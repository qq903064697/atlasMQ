package cn.atlas.atlasmq.nameserver.common;

import cn.atlas.atlasmq.common.dto.SlaveAckDTO;
import cn.atlas.atlasmq.nameserver.core.PropertiesLoader;
import cn.atlas.atlasmq.nameserver.replication.ReplicationTask;
import cn.atlas.atlasmq.nameserver.store.MasterReplicationQueueManager;
import cn.atlas.atlasmq.nameserver.store.ReplicationChannelManager;
import cn.atlas.atlasmq.nameserver.store.ServiceInstanceManager;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static Channel masterConnection = null;
    private static MasterReplicationQueueManager masterReplicationQueueManager = new MasterReplicationQueueManager();
    private static Map<String, SlaveAckDTO> ackMap = new ConcurrentHashMap<>();

    public static Map<String, SlaveAckDTO> getAckMap() {
        return ackMap;
    }

    public static void setAckMap(Map<String, SlaveAckDTO> ackMap) {
        CommonCache.ackMap = ackMap;
    }

    public static MasterReplicationQueueManager getMasterReplicationQueueManager() {
        return masterReplicationQueueManager;
    }

    public static void setMasterReplicationQueueManager(MasterReplicationQueueManager masterReplicationQueueManager) {
        CommonCache.masterReplicationQueueManager = masterReplicationQueueManager;
    }

    public static ReplicationTask getReplicationTask() {
        return replicationTask;
    }

    public static void setReplicationTask(ReplicationTask replicationTask) {
        CommonCache.replicationTask = replicationTask;
    }

    public static Channel getMasterConnection() {
        return masterConnection;
    }

    public static void setMasterConnection(Channel masterConnection) {
        CommonCache.masterConnection = masterConnection;
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
