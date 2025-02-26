import cn.atlas.atlasmq.client.producer.DefaultProducerImpl;
import cn.atlas.atlasmq.common.dto.MessageDTO;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class TestProducerSuite {
    DefaultProducerImpl producer;
    @Before
    public void setUp() {
        producer = new DefaultProducerImpl();
        producer.setNsIp("127.0.0.1");
        producer.setNsPort(9093);
        producer.setNsPwd("altas_mq");
        producer.setNsUser("altas_mq");
        producer.start();
    }
    @Test
    public void sendMsg() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTopic("order_cancel_topic");
        messageDTO.setBody("this is async test content".getBytes());
        producer.sendAsync(messageDTO);
        messageDTO.setBody("this is test content".getBytes());
        producer.send(messageDTO);
    }
}
