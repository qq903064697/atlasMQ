package cn.atlas.atlasmq.broker;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.config.AtlasMqTopicLoader;
import cn.atlas.atlasmq.broker.config.ConsumerQueueOffsetLoader;
import cn.atlas.atlasmq.broker.config.GlobalPropertiesLoader;
import cn.atlas.atlasmq.broker.core.CommitLogAppendHandler;
import cn.atlas.atlasmq.broker.core.ConsumerQueueAppendHandler;
import cn.atlas.atlasmq.broker.core.ConsumerQueueConsumeHandler;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.netty.broker.BrokerServer;
import cn.atlas.atlasmq.broker.netty.nameserver.NameServerClient;
import cn.atlas.atlasmq.broker.rebalance.ConsumerInstancePool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午3:55
 * @Version 1.0
 */
public class BrokerStartUp {
    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static AtlasMqTopicLoader atlasMqTopicLoader;
    private static CommitLogAppendHandler commitLogAppendHandler;
    private static ConsumerQueueOffsetLoader consumerQueueOffsetLoader;
    private static ConsumerQueueAppendHandler consumerQueueAppendHandler;
    private static ConsumerQueueConsumeHandler consumerQueueConsumeHandler;

    /**
     * 初始化配置逻辑
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        atlasMqTopicLoader = new AtlasMqTopicLoader();
        consumerQueueOffsetLoader = new ConsumerQueueOffsetLoader();
        consumerQueueConsumeHandler = new ConsumerQueueConsumeHandler();
        commitLogAppendHandler = new CommitLogAppendHandler();
        consumerQueueAppendHandler = new ConsumerQueueAppendHandler();

        globalPropertiesLoader.loadProperties();
        atlasMqTopicLoader.loadProperties();
        atlasMqTopicLoader.startRefreshAtlasMqTopicInfoTask();
        consumerQueueOffsetLoader.loadProperties();
        consumerQueueOffsetLoader.startRefreshConsumerQueueOffsetTask();


        for (AtlasMqTopicModel atlasMqTopicModel : CommonCache.getAtlasMqTopicModelMap().values()) {
            String topicName = atlasMqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
            consumerQueueAppendHandler.prepareConsumerQueueMMapLoading(topicName);
        }
        CommonCache.setConsumerQueueConsumeHandler(consumerQueueConsumeHandler);
        CommonCache.setCommitLogAppendHandler(commitLogAppendHandler);
        CommonCache.setConsumerQueueAppendHandler(consumerQueueAppendHandler);
    }

    /**
     * 初始化nameserver的长连接通道
     */
    private static void initNameServerChannel() {
        CommonCache.getNameServerClient().initConnection();
        CommonCache.getNameServerClient().sendRegistryMsg();

    }
    private static void initBrokerServer() throws InterruptedException {
        BrokerServer brokerServer = new BrokerServer(CommonCache.getGlobalProperties().getBrokerPort());
        brokerServer.startServer();
    }
    //开启重平衡任务
    private static void initReBalanceJob() {
        CommonCache.getConsumerInstancePool().startReBalanceJob();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // 加载配置，缓存对象的生成
        initProperties();
        initNameServerChannel();
        initReBalanceJob();
        //这个函数是会阻塞的
        initBrokerServer();

//        // 模拟初始化文件映射
//        String topic = "order_cancel_topic";
//        String userServiceConsumerGroup = "user_service_group";
//        String orderServiceConsumerGroup = "order_service_group";
//        new Thread(() -> {
//            while (true) {
//                byte[] content = consumerQueueConsumeHandler.consume(topic, userServiceConsumerGroup, 0);
//                if (content != null && content.length != 0) {
//                    System.out.println(userServiceConsumerGroup + "消费内容:" + new String(content));
//                    consumerQueueConsumeHandler.ack(topic, userServiceConsumerGroup, 0);
//                } else {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }).start();
//
//        new Thread(() -> {
//            while (true) {
//                byte[] content = consumerQueueConsumeHandler.consume(topic, orderServiceConsumerGroup, 0);
//                if (content != null && content.length != 0) {
//                    System.out.println(orderServiceConsumerGroup + "消费内容:" + new String(content));
//                    consumerQueueConsumeHandler.ack(topic, orderServiceConsumerGroup, 0);
//                } else {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }).start();
//
//        AtomicInteger i = new AtomicInteger();
//        new Thread(() -> {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            while (true) {
//                try {
//                    commitLogAppendHandler.appendMessage(topic, ("message_" + (i.getAndIncrement())).getBytes());
//                    TimeUnit.MILLISECONDS.sleep(100);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//
//    }
//
//    public static void m1(String[] args) throws IOException, InterruptedException {
//        // 加载配置，缓存对象的生成
//        initProperties();
//
//        // 模拟初始化文件映射
//        String topic = "order_cancel_topic";
//        String consumerGroup = "user_service_group";
//        for (int i = 0; i < 50000; i++) {
//            byte[] content = consumerQueueConsumeHandler.consume(topic, consumerGroup, 0);
//            System.out.println("消费数据:" + new String(content));
//            consumerQueueConsumeHandler.ack(topic, consumerGroup, 0);
////            commitLogAppendHandler.appendMessage(topic, ("this is content " + i).getBytes());
////            consumerQueueAppendHandler.readMsg(topic, i * 12);
////            commitLogAppendHandler.readMsg(topic);
//            TimeUnit.MILLISECONDS.sleep(100);
//
//        }
//
//
////        commitLogAppendHandler.readMsg(topic);
    }
}
