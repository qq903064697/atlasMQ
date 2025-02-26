package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @Author xiaoxin
 * @Create 2025/2/23 下午10:21
 * @Version 1.0
 */
public class NodeReplicationAckMsgEvent extends Event {
    private Integer type;

    private String nodeIp;

    private Integer nodePort;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public Integer getNodePort() {
        return nodePort;
    }

    public void setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
    }
}
