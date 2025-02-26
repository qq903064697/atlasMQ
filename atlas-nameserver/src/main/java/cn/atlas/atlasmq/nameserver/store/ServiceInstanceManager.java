package cn.atlas.atlasmq.nameserver.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiaoxin
 * @Description
 */
public class ServiceInstanceManager {

    private Map<String, ServiceInstance> serviceInstanceMap = new ConcurrentHashMap<>();

    public void putIfExist(ServiceInstance serviceInstance) {
        ServiceInstance currentInstance = this.get(serviceInstance.getIp(), serviceInstance.getPort());
        if (currentInstance != null && currentInstance.getFirstRegistryTime() != null) {
            currentInstance.setLastHeartBeatTime(serviceInstance.getLastHeartBeatTime());
            serviceInstanceMap.put(serviceInstance.getIp() + ":" + serviceInstance.getPort(), currentInstance);
        } else {
            throw new RuntimeException("之前心跳缓存已经删除，请重新注册");
        }
    }

    public void put(ServiceInstance serviceInstance) {
        serviceInstanceMap.put(serviceInstance.getIp() + ":" + serviceInstance.getPort(), serviceInstance);
    }

    public ServiceInstance get(String brokerIp, Integer brokerPort) {
        return serviceInstanceMap.get(brokerIp + ":" + brokerPort);
    }

    public ServiceInstance remove(String key) {
        return serviceInstanceMap.remove(key);
    }

    public Map<String, ServiceInstance> getServiceInstanceMap() {
        return serviceInstanceMap;
    }

    public void setServiceInstanceMap(Map<String, ServiceInstance> serviceInstanceMap) {
        this.serviceInstanceMap = serviceInstanceMap;
    }
}
