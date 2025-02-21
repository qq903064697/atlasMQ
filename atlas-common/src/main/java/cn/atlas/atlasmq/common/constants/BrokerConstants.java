package cn.atlas.atlasmq.common.constants;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class BrokerConstants {

    public static final String ATLAS_MQ_HOME = "ATLAS_MQ_HOME";
    public static final String BASE_COMMIT_LOG_PATH = "/broker/commitlog/";
    public static final String BASE_CONSUMER_QUEUE_PATH = "/broker/consumerqueue/";
    public static final String BROKER_PROPERTIES_PATH = "/broker/config/broker.properties";
    public static final String SPLIT = "/";
//    public static final Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1024 * 1024 * 1024; // 正常情况下为1GB,最后压测使用
    public static final Integer COMMIT_LOG_DEFAULT_MMAP_SIZE = 1 * 1024 * 1024; // 1MB方便使用
    public static final Integer DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP = 3;
    public static final Integer DEFAULT_CONSUMER_QUEUE_OFFSET_TIME_STEP = 1;
    public static final Integer CONSUMER_QUEUE_EACH_MSG_SIZE = 12;
    public static final Short DEFAULT_MAGIC_NUM = 17671;

}
