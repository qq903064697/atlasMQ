package cn.atlas.atlasmq.nameserver.common;

/**
 * @Author xiaoxin
 * @Description 链路化方式同步配置
 */
public class TraceReplicationProperties {

    private String nextNode;

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }
}
