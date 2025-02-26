package cn.atlas.atlasmq.broker.rebalance.strategy;

import cn.atlas.atlasmq.broker.rebalance.ConsumerInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author xiaoxin
 * @Description 需要参与重平衡的消费组
 */
public class ReBalanceInfo {

    private Map<String, List<ConsumerInstance>> consumerInstanceMap;
    //消费者发生变化的消费组
    private Map<String, Set<String>> changeConsumerGroupMap = new HashMap<>();

    public Map<String, List<ConsumerInstance>> getConsumerInstanceMap() {
        return consumerInstanceMap;
    }

    public void setConsumeInstanceMap(Map<String, List<ConsumerInstance>> consumerInstanceMap) {
        this.consumerInstanceMap = consumerInstanceMap;
    }

    public Map<String, Set<String>> getChangeConsumerGroupMap() {
        return changeConsumerGroupMap;
    }

    public void setChangeConsumerGroupMap(Map<String, Set<String>> changeConsumerGroupMap) {
        this.changeConsumerGroupMap = changeConsumerGroupMap;
    }
}
