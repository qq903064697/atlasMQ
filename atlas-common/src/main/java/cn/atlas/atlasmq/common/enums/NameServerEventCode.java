package cn.atlas.atlasmq.common.enums;

/**
 * @Author xiaoxin
 * @Description
 */
public enum NameServerEventCode {
    REGISTRY(1,"注册事件"),
    UN_REGISTRY(2,"下线事件"),
    HEART_BEAT(3,"心跳事件"),
    START_REPLICATION(4,"开启复制"),
    MASTER_START_REPLICATION_ACK(5,"master回应slave节点开启同步"),
    MASTER_REPLICATION_MSG(6,"主从同步数据"),
    SLAVE_HEART_BEAT(7,"从节点心跳数据"),
    SLAVE_REPLICATION_ACK_MSG(8,"从节点接收同步数据成功"),
    ;

    int code;
    String desc;

    NameServerEventCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
