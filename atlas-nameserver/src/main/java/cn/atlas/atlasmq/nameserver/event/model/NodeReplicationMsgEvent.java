package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.common.event.model.Event;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;

/**
 * @Author xiaoxin
 * @Create 2025/2/23 下午10:09
 * @Version 1.0
 */
public class NodeReplicationMsgEvent extends Event {
    private Integer type;
    private ServiceInstance serviceInstance;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
