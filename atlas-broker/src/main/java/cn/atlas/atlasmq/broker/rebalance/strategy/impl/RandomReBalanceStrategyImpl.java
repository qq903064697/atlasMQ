package cn.atlas.atlasmq.broker.rebalance.strategy.impl;



import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.rebalance.ConsumerInstance;
import cn.atlas.atlasmq.broker.rebalance.strategy.IReBalanceStrategy;
import cn.atlas.atlasmq.broker.rebalance.strategy.ReBalanceInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author idea
 * @Date: Created in 14:07 2024/6/23
 * @Description 随机重平衡
 */
public class RandomReBalanceStrategyImpl implements IReBalanceStrategy {

    @Override
    public void doReBalance(ReBalanceInfo reBalanceInfo) {
        Map<String, List<ConsumerInstance>> consumeInstanceMap = reBalanceInfo.getConsumerInstanceMap();
        Map<String, AtlasMqTopicModel> atlasMqTopicModelMap = CommonCache.getAtlasMqTopicModelMap();
        for (String topic : consumeInstanceMap.keySet()) {
            //指定topic当下的所有消费者实例
            List<ConsumerInstance> consumerInstances = consumeInstanceMap.get(topic);
            AtlasMqTopicModel atlasMqTopicModel = atlasMqTopicModelMap.get(topic);
            if (atlasMqTopicModel == null) {
                // 异常topic
                continue;
            }
            Map<String, List<ConsumerInstance>> consumerGroupMap = consumerInstances.stream().collect(Collectors.groupingBy(ConsumerInstance::getConsumerGroup));
            //此时可能队列还没分配
            if (atlasMqTopicModel.getQueueList() == null) {
                continue;
            }
            //队列数
            int queueNum = atlasMqTopicModel.getQueueList().size();
            //取出当前topic有变更过的消费组名
            Set<String> changeConsumerGroup = reBalanceInfo.getChangeConsumerGroupMap().get(topic);
            if(changeConsumerGroup==null || changeConsumerGroup.isEmpty()) {
                //目前没有新消费者加入，不需要触发重平衡
                return;
            }
            Map<String, List<ConsumerInstance>> consumeGroupHoldMap = new ConcurrentHashMap<>();
            for (String consumerGroup : consumerGroupMap.keySet()) {
                // 变更的消费组名单中没包含当前消费组，不触发重平衡
                if (!changeConsumerGroup.contains(consumerGroup)) {
                    //依旧保存之前的消费组信息
                    consumeGroupHoldMap.put(consumerGroup, consumerGroupMap.get(consumerGroup));
                    continue;
                }
                //当前消费组有变更
                List<ConsumerInstance> consumerGroupInstanceList = consumerGroupMap.get(consumerGroup);
                List<ConsumerInstance> newConsumerQueueInstanceList = new ArrayList<>();
                int consumeNum = consumerGroupInstanceList.size();
                //队列数大于消费者个数，那么每个消费者都会有队列持有
                Collections.shuffle(consumerGroupInstanceList);
                if (queueNum >= consumeNum) {
                    int j = 0;
                    for (int i = 0; i < consumeNum; i++, j++) {
                        ConsumerInstance consumeInstance = consumerGroupInstanceList.get(i);
                        consumeInstance.getQueueIdSet().add(j);
                        newConsumerQueueInstanceList.add(consumeInstance);
                    }
                    for (; j < queueNum; j++) {
                        Random random = new Random();
                        int randomConsumerId = random.nextInt(consumeNum);
                        ConsumerInstance consumerInstance = consumerGroupInstanceList.get(randomConsumerId);
                        consumerInstance.getQueueIdSet().add(j);
                        newConsumerQueueInstanceList.add(consumerInstance);
                    }
                } else {
                    for (int i = 0; i < queueNum; i++) {
                        ConsumerInstance consumerInstance = consumerGroupInstanceList.get(i);
                        consumerInstance.getQueueIdSet().add(i);
                        newConsumerQueueInstanceList.add(consumerInstance);
                    }
                }
                consumeGroupHoldMap.put(consumerGroup, newConsumerQueueInstanceList);
            }
            CommonCache.getConsumerHoldMap().put(topic, consumeGroupHoldMap);
        }
    }


}
