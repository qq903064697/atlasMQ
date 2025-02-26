package cn.atlas.atlasmq.broker.rebalance.strategy.impl;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.rebalance.ConsumerInstance;
import cn.atlas.atlasmq.broker.rebalance.strategy.IReBalanceStrategy;
import cn.atlas.atlasmq.broker.rebalance.strategy.ReBalanceInfo;
import org.apache.commons.collections4.CollectionUtils;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author idea
 * @Date: Created in 14:09 2024/6/23
 * @Description 平均分配
 */
public class RangeReBalanceStrategyImpl implements IReBalanceStrategy {

    @Override
    public void doReBalance(ReBalanceInfo reBalanceInfo) {
        Map<String, List<ConsumerInstance>> consumerInstanceMap = reBalanceInfo.getConsumerInstanceMap();
        Map<String, AtlasMqTopicModel> atlasMqTopicModelMap = CommonCache.getAtlasMqTopicModelMap();
        for (String topic : consumerInstanceMap.keySet()) {
            List<ConsumerInstance> consumerInstances = consumerInstanceMap.get(topic);
            if (CollectionUtils.isEmpty(consumerInstances)) {
                continue;
            }
            //每个消费组实例
            Map<String, List<ConsumerInstance>> consumerGroupMap = consumerInstances.stream().collect(Collectors.groupingBy(ConsumerInstance::getConsumerGroup));
            AtlasMqTopicModel eagleMqTopicModel = atlasMqTopicModelMap.get(topic);
            int queueSize = eagleMqTopicModel.getQueueList().size();
            for (String consumerGroup : consumerGroupMap.keySet()) {
                List<ConsumerInstance> consumerInstanceList = consumerGroupMap.get(consumerGroup);
                //算出每个消费者平均拥有多少条队列
                int eachConsumerQueueNum = queueSize / consumerInstanceList.size();
                int queueId = 0;
                for (int i = 0; i < consumerInstanceList.size(); i++) {
                    for (int queueNums = 0; queueNums < eachConsumerQueueNum; queueNums++) {
                        consumerInstanceList.get(i).getQueueIdSet().add(queueId++);
                    }
                }
                //代表有多余队列没有被用到
                int remainQueueCount = queueSize - queueId;
                if (remainQueueCount > 0) {
                    for (int i = 0; i < remainQueueCount; i++) {
                        consumerInstanceList.get(i).getQueueIdSet().add(queueId++);
                    }
                }
            }
        }
    }

}
