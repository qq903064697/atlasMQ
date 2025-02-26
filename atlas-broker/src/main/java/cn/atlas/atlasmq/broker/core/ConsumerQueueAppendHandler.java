package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.model.QueueModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description:
 */
public class ConsumerQueueAppendHandler {
//    private ConsumerQueueMMapFileModelManager consumerQueueMMapFileModelManager = new ConsumerQueueMMapFileModelManager();

    public void prepareConsumerQueueMMapLoading(String topicName) throws IOException {
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topicName);
        List<QueueModel> queueModelList = atlasMqTopicModel.getQueueList();
        List<ConsumerQueueMMapFileModel> consumerQueueMMapFileModels = new ArrayList<>();
        // 循环遍历做mmap的初始化
        for (QueueModel queueModel : queueModelList) {
            ConsumerQueueMMapFileModel consumerQueueMMapFileModel = new ConsumerQueueMMapFileModel();
            consumerQueueMMapFileModel.loadFileInMMap(
                    topicName,
                    queueModel.getId(),
                    queueModel.getLastOffset(),
                    queueModel.getLatestOffset().get(),
                    queueModel.getOffsetLimit());
            consumerQueueMMapFileModels.add(consumerQueueMMapFileModel);
        }
        CommonCache.getConsumerQueueMMapFileModelManager().put(topicName, consumerQueueMMapFileModels);
    }

}
