package cn.atlas.atlasmq.common.enums;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午3:22
 * @Version 1.0
 */
public enum NameServerResponseCode {
    ERROR_USER_OR_PASSWORD(1001, "账号验证异常"),
    UN_REGISTRY_SERVICE(1002, "服务正常下线"),
    REGISTRY_SUCCESS(1003, "服务正常注册"),
    HEART_BEAT_SUCCESS(1004,"心跳ACK"),
    PULL_BROKER_ADDRESS_SUCCESS(1005,"拉broker地址成功"),
    ;

    int code;
    String desc;

    NameServerResponseCode(int code, String desc) {
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
