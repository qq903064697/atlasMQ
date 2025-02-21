package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.nameserver.store.ServiceInstance;

/**
 * @Author idea
 * @Description 复制消息
 */
public class ReplicationMsgEvent extends Event{

    private ServiceInstance serviceInstance;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
