package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.model.ConsumerQueueDetailModel;
import cn.atlas.atlasmq.broker.model.ConsumerQueueOffsetModel;
import cn.atlas.atlasmq.broker.model.QueueModel;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Decription: 消费者队列消费处理器
 */
public class ConsumerQueueConsumeHandler {

    /**
     * 读取当前最新一条ConsumerQueue的消息内容
     * @return
     */
    public byte[] consume(String topic, String consumerGroup, Integer queueId) {
        // 1.检查参数合法性
        // 2.获取当前匹配的队列的最新的consumerQueue的offset是多少
        // 3.获取当前匹配的队列存储文件的mmap对象，然后读取offset地址的数据
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        if (atlasMqTopicModel == null) {
            throw new RuntimeException("topic" + topic + " is undefined");
        }
        ConsumerQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumerQueueOffsetModel().getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> consumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = consumerGroupDetailMap.get(topic);

        // 如果是首次消费
        if (consumerGroupDetail == null) {
            consumerGroupDetail = new ConsumerQueueOffsetModel.ConsumerGroupDetail();
            consumerGroupDetailMap.put(topic, consumerGroupDetail);
        }
        Map<String, Map<String, String>> consumerGroupOffsetMap = consumerGroupDetail.getConsumerGroupDetailMap();
        Map<String, String> queueOffsetDetailMap = consumerGroupOffsetMap.get(consumerGroup);
        List<QueueModel> queueList = atlasMqTopicModel.getQueueList();
        if (queueOffsetDetailMap == null) {
            queueOffsetDetailMap = new HashMap<>();
            for (QueueModel queueModel : queueList) {
                queueOffsetDetailMap.put(String.valueOf(queueModel.getId()), "00000000#0");
            }
            consumerGroupOffsetMap.put(consumerGroup, queueOffsetDetailMap);
        }
        String offsetStrInfo = queueOffsetDetailMap.get(String.valueOf(queueId));
        String[] split = offsetStrInfo.split("#");
//        String consumerQueueFileName = split[0];
        Integer consumerQueueOffset = Integer.parseInt(split[1]);
        QueueModel queueModel = queueList.get(queueId);
        // 消费到了尽头
        if (queueModel.getLatestOffset().get() <= consumerQueueOffset) {
            return null;
        }

        List<ConsumerQueueMMapFileModel> consumerQueueMMapFileModelList = CommonCache.getConsumerQueueMMapFileModelManager().get(topic);
        ConsumerQueueMMapFileModel consumerQueueMMapFileModel = consumerQueueMMapFileModelList.get(queueId);

        byte[] content = consumerQueueMMapFileModel.readContent(consumerQueueOffset);
        ConsumerQueueDetailModel consumerQueueDetailModel = new ConsumerQueueDetailModel();
        consumerQueueDetailModel.buildFromBytes(content);

        CommitLogMMapFileModel commitLogMMapFileModel = CommonCache.getCommitLogMMapFileModelManager().get(topic);
        return commitLogMMapFileModel.readContent(consumerQueueDetailModel.getMsgIndex(), consumerQueueDetailModel.getMsgLength());
    }

    /**
     * 更新consumerQueue-offset的值
     * @return
     */
    public boolean ack(String topic, String consumerGroup, Integer queueId) {
        ConsumerQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumerQueueOffsetModel().getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> consumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = consumerGroupDetailMap.get(topic);
        Map<String, String> consumerQueueOffsetDeailMap = consumerGroupDetail.getConsumerGroupDetailMap().get(consumerGroup);
        String offsetStrInfo = consumerQueueOffsetDeailMap.get(String.valueOf(queueId));
        String[] split = offsetStrInfo.split("#");
        String consumerQueueFileName = split[0];
        Integer consumerQueueOffset = Integer.parseInt(split[1]);
        consumerQueueOffset += 12;
        consumerQueueOffsetDeailMap.put(String.valueOf(queueId), consumerQueueFileName + "#" + consumerQueueOffset);
        return true;
    }
}
