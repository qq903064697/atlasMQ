package cn.atlas.atlasmq.common.enums;

/**
 * @Author xiaoxin
 * @Create 2025/2/25 下午10:18
 * @Version 1.0
 */
public enum AckStatus {
    SUCCESS(1),
    FAIL(0),
    ;

    AckStatus(int code) {
        this.code = code;
    }

    int code;

    public int getCode() {
        return code;
    }
}
