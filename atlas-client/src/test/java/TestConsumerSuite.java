import cn.atlas.atlasmq.client.consumer.ConsumerMessage;
import cn.atlas.atlasmq.client.consumer.ConsumerResult;
import cn.atlas.atlasmq.client.consumer.DefaultMqConsumer;
import cn.atlas.atlasmq.client.consumer.MessageConsumerListener;
import cn.atlas.atlasmq.client.producer.DefaultProducerImpl;
import cn.atlas.atlasmq.client.producer.SendResult;
import cn.atlas.atlasmq.common.dto.MessageDTO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author xiaoxin
 * @Create 2025/2/25 下午2:11
 * @Version 1.0
 */
public class TestConsumerSuite {
    private DefaultMqConsumer consumer;
    private static final Logger logger = LoggerFactory.getLogger(TestConsumerSuite.class);

    @Test
    public void setUp() throws Exception {
        consumer = new DefaultMqConsumer();
        consumer.setNsIp("127.0.0.1");
        consumer.setNsPort(9093);
        consumer.setNsPwd("altas_mq");
        consumer.setNsUser("altas_mq");
        consumer.setTopic("order_cancel_topic");
        consumer.setConsumerGroup("test-consume-group");
        consumer.setBatchSize(10);
        consumer.setMessageConsumerListener(new MessageConsumerListener() {
            @Override
            public ConsumerResult consume(List<ConsumerMessage> consumeMessages) {
                for (ConsumerMessage consumerMessage : consumeMessages) {
                    System.out.println("消费端获取的数据内容:" + new String(consumerMessage.getBody()));
                }
                return ConsumerResult.CONSUME_SUCCESS();
            }
        });
        consumer.start();
    }
}
