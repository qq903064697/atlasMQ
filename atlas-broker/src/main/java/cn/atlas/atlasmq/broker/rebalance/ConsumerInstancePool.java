package cn.atlas.atlasmq.broker.rebalance;


import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.rebalance.strategy.IReBalanceStrategy;
import cn.atlas.atlasmq.broker.rebalance.strategy.ReBalanceInfo;
import cn.atlas.atlasmq.broker.rebalance.strategy.impl.RandomReBalanceStrategyImpl;
import cn.atlas.atlasmq.common.utils.AssertUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author xiaoxin
 * @Create 2025/2/25 下午2:30
 * @Version 1.0
 */
public class ConsumerInstancePool {

    private final Logger logger = LoggerFactory.getLogger(ConsumerInstancePool.class);
    private Map<String, List<ConsumerInstance>> consumerInstanceMap = new ConcurrentHashMap<>();
    private static Map<String, IReBalanceStrategy> reBalanceStrategyMap = new HashMap<>();
    private ReBalanceInfo reBalanceInfo = new ReBalanceInfo();

    static {
        reBalanceStrategyMap.put("random", new RandomReBalanceStrategyImpl());
        reBalanceStrategyMap.put("range", new RandomReBalanceStrategyImpl());
    }

    /**
     * 加入到池中
     *
     * @param consumerInstance
     */
    public void addInstancePool(ConsumerInstance consumerInstance) {
        synchronized (this) {
            String topic = consumerInstance.getTopic();
            //校验topic是否合法
            AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
            AssertUtils.isNotNull(atlasMqTopicModel,"topic非法");
            List<ConsumerInstance> consumerInstanceList = consumerInstanceMap.getOrDefault(topic,new ArrayList<>());
            for (ConsumerInstance instance : consumerInstanceList) {
                if (instance.getConsumerReqId().equals(consumerInstance.getConsumerReqId())) {
                    return;
                }
            }
            consumerInstanceList.add(consumerInstance);
            consumerInstanceMap.put(topic, consumerInstanceList);
            Set<String> consumerGroupSet = reBalanceInfo.getChangeConsumerGroupMap().get(topic);
            if (CollectionUtils.isEmpty(consumerGroupSet)) {
                consumerGroupSet = new HashSet<>();
            }
            consumerGroupSet.add(consumerInstance.getConsumerGroup());
            reBalanceInfo.getChangeConsumerGroupMap().put(topic,consumerGroupSet);
            logger.info("new instance add in pool:{}", JSON.toJSONString(consumerInstance));
        }
    }

    /**
     * 从池中移除
     *
     * @param consumerInstance
     */
    public void removeFromInstancePool(ConsumerInstance consumerInstance) {
        synchronized (this) {
            String topic = consumerInstance.getTopic();
            List<ConsumerInstance> currentConsumeInstanceList = consumerInstanceMap.get(topic);
            List<ConsumerInstance> filterConsumeInstanceList = currentConsumeInstanceList.stream()
                    .filter(item -> !item.getConsumerReqId().equals(consumerInstance.getConsumerReqId())).collect(Collectors.toList());
            consumerInstanceMap.put(topic, filterConsumeInstanceList);
            Set<String> consumerGroupSet = reBalanceInfo.getChangeConsumerGroupMap().get(topic);
            if (CollectionUtils.isEmpty(consumerGroupSet)) {
                return;
            }
            consumerGroupSet.remove(consumerInstance.getConsumerGroup());
        }
    }

    /**
     * 执行重平衡逻辑
     * 定时任务触发，把已有的队列分配给消费者
     */
    public void doReBalance() {
        synchronized (this) {
            String reBalanceStrategy = CommonCache.getGlobalProperties().getReBalanceStrategy();
            //触发重平衡行为，根据参数决定重平衡策略的不同
            reBalanceInfo.setConsumeInstanceMap(this.consumerInstanceMap);
            reBalanceStrategyMap.get(reBalanceStrategy).doReBalance(reBalanceInfo);
            reBalanceInfo.getChangeConsumerGroupMap().clear();
            logger.info("do reBalance,{}", JSON.toJSONString(CommonCache.getConsumerHoldMap()));
        }
    }

    public void startReBalanceJob() {
        Thread reBalanceTask = new Thread(()->{
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    doReBalance();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        reBalanceTask.setName("reBalance-task");
        reBalanceTask.start();
    }
}
