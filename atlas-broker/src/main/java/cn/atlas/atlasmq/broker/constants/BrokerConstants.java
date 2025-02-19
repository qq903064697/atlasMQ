package cn.atlas.atlasmq.broker.constants;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class BrokerConstants {

    public static final String ATLAS_MQ_HOME = "ATLAS_MQ_HOME";
    public static final String BASE_STORE_PATH = "/broker/store/";
//    public static final Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1024 * 1024 * 1024; // 正常情况下为1GB,最后压测使用
    public static final Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1 * 1024 * 1024; // 1MB方便使用
    public static final Integer DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP = 3;

}
