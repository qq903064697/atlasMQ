package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.common.event.model.Event;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;

/**
 * @Author idea
 * @Description 复制消息
 */
public class ReplicationMsgEvent extends Event {
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
