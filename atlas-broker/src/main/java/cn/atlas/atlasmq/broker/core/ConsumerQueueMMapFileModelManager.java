package cn.atlas.atlasmq.broker.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: consumerQueue的mmap映射对象的管理器
 */
public class ConsumerQueueMMapFileModelManager {

    private Map<String, List<ConsumerQueueMMapFileModel>> consumerQueueMMapFileModel = new HashMap<>();

    public void put(String topic, List<ConsumerQueueMMapFileModel> consumerQueueMMapFileModels) {
        consumerQueueMMapFileModel.put(topic, consumerQueueMMapFileModels);
    }

    public List<ConsumerQueueMMapFileModel> get(String topic) {
        return consumerQueueMMapFileModel.get(topic);
    }
}
