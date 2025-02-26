package cn.atlas.atlasmq.client.consumer;


import java.util.List;

/**
 * @Author xiaoxin
 * @Description 数据消费监听器
 */
public interface MessageConsumerListener {


    /**
     * 默认的消费处理函数
     * @param consumeMessages
     * @return
     */
    ConsumerResult consume(List<ConsumerMessage> consumeMessages);
}
