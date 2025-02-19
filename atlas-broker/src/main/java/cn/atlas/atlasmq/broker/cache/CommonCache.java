package cn.atlas.atlasmq.broker.cache;

import cn.atlas.atlasmq.broker.config.GlobalProperties;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;

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
    public static GlobalProperties globalProperties = new GlobalProperties();
    public static List<AtlasMqTopicModel> atlasMqTopicModelList = new ArrayList<>();
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
}
