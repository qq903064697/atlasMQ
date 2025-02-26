package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @Author xiaoxin
 * @Create 2025/2/24 下午7:56
 * @Version 1.0
 */
public class PullBrokerIpEvent extends Event {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
