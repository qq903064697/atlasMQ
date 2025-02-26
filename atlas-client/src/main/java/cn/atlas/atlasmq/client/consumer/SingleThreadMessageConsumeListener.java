package cn.atlas.atlasmq.client.consumer;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 11:08 2024/6/16
 * @Description 单线程数据消费监听器
 */
public class SingleThreadMessageConsumeListener implements MessageConsumerListener{

    @Override
    public ConsumerResult consume(List<ConsumerMessage> consumeMessages) {
        return null;
    }
}
