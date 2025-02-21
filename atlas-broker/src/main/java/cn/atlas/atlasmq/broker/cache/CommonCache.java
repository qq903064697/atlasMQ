package cn.atlas.atlasmq.broker.cache;

import cn.atlas.atlasmq.broker.config.GlobalProperties;
import cn.atlas.atlasmq.broker.core.CommitLogMMapFileModelManager;
import cn.atlas.atlasmq.broker.core.ConsumerQueueMMapFileModel;
import cn.atlas.atlasmq.broker.core.ConsumerQueueMMapFileModelManager;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.model.ConsumerQueueOffsetModel;
import cn.atlas.atlasmq.broker.netty.nameserver.HeartBeatTaskManager;
import cn.atlas.atlasmq.broker.netty.nameserver.NameServerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 统一缓存对象
 */
public class CommonCache {
    private static GlobalProperties globalProperties = new GlobalProperties();
    private static List<AtlasMqTopicModel> atlasMqTopicModelList = new ArrayList<>();
    private static ConsumerQueueOffsetModel consumerQueueOffsetModel = new ConsumerQueueOffsetModel();
    private static ConsumerQueueMMapFileModelManager consumerQueueMMapFileModelManager = new ConsumerQueueMMapFileModelManager();
    private static CommitLogMMapFileModelManager commitLogMMapFileModelManager = new CommitLogMMapFileModelManager();
    private static NameServerClient nameServerClient = new NameServerClient();
    private static HeartBeatTaskManager heartBeatTaskManager = new HeartBeatTaskManager();

    public static HeartBeatTaskManager getHeartBeatTaskManager() {
        return heartBeatTaskManager;
    }

    public static void setHeartBeatTaskManager(HeartBeatTaskManager heartBeatTaskManager) {
        CommonCache.heartBeatTaskManager = heartBeatTaskManager;
    }

    public static NameServerClient getNameServerClient() {
        return nameServerClient;
    }

    public static void setNameServerClient(NameServerClient nameServerClient) {
        CommonCache.nameServerClient = nameServerClient;
    }

    public static CommitLogMMapFileModelManager getCommitLogMMapFileModelManager() {
        return commitLogMMapFileModelManager;
    }

    public static void setCommitLogMMapFileModelManager(CommitLogMMapFileModelManager commitLogMMapFileModelManager) {
        CommonCache.commitLogMMapFileModelManager = commitLogMMapFileModelManager;
    }

    //    public static Map<String, AtlasMqTopicModel> atlasMqTopicModelMap = new HashMap<>();

//    public static Map<String, AtlasMqTopicModel> getAtlasMqTopicModelMap() {
//        return atlasMqTopicModelMap;
//    }
//
//    public static void setAtlasMqTopicModelList(Map<String, AtlasMqTopicModel> atlasMqTopicModelMap) {
//        CommonCache.atlasMqTopicModelMap = atlasMqTopicModelMap;
//    }

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static Map<String, AtlasMqTopicModel> getAtlasMqTopicModelMap() {
        return atlasMqTopicModelList.stream().collect(Collectors.toMap(AtlasMqTopicModel::getTopic, item -> item));
    }

    public static List<AtlasMqTopicModel> getAtlasMqTopicModelList() {
        return atlasMqTopicModelList;
    }

    public static void setAtlasMqTopicModelList(List<AtlasMqTopicModel> atlasMqTopicModelList) {
        CommonCache.atlasMqTopicModelList = atlasMqTopicModelList;
    }

    public static ConsumerQueueOffsetModel getConsumerQueueOffsetModel() {
        return consumerQueueOffsetModel;
    }

    public static void setConsumerQueueOffsetModel(ConsumerQueueOffsetModel consumerQueueOffsetModel) {
        CommonCache.consumerQueueOffsetModel = consumerQueueOffsetModel;
    }

    public static ConsumerQueueMMapFileModelManager getConsumerQueueMMapFileModelManager() {
        return consumerQueueMMapFileModelManager;
    }

    public static void setConsumerQueueMMapFileModelManager(ConsumerQueueMMapFileModelManager consumerQueueMMapFileModelManager) {
        CommonCache.consumerQueueMMapFileModelManager = consumerQueueMMapFileModelManager;
    }
}
