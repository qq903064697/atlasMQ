package cn.atlas.atlasmq.common.enums;

/**
 * @Author xiaoxin
 * @Create 2025/2/25 下午2:16
 * @Version 1.0
 */
public enum ConsumerResultStatus {
    CONSUMER_SUCCESS(1),
    CONSUMER_LATER(2),
    ;

    int code;

    ConsumerResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
