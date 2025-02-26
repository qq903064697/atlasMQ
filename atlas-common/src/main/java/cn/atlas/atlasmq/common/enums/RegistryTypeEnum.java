package cn.atlas.atlasmq.common.enums;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 注册类型
 */
public enum RegistryTypeEnum {
    PRODUCER("producer"),
    CONSUMER("consumer"),
    BROKER("broker")
            ;
    String code;

    RegistryTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
