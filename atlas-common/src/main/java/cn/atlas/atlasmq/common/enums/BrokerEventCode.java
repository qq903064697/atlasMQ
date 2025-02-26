package cn.atlas.atlasmq.common.enums;

/**
 * @Author xjiaoxin
 * @Description broker服务端事件处理code
 */
public enum BrokerEventCode {

    PUSH_MSG(1001, "推送消息"),
    CONSUME_MSG(1002,"消费消息"),
    CONSUME_SUCCESS_MSG(1003,"消费成功"),
    ;

    int code;
    String desc;

    BrokerEventCode(int code, String desc) {
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
