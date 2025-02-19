package cn.atlas.atlasmq.broker;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.config.AtlasMqTopicLoader;
import cn.atlas.atlasmq.broker.config.GlobalPropertiesLoader;
import cn.atlas.atlasmq.broker.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.core.CommitLogAppendHandler;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午3:55
 * @Version 1.0
 */
public class BrokerStartUp {
    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static AtlasMqTopicLoader atlasMqTopicLoader;
    private static CommitLogAppendHandler commitLogAppendHandler;

    /**
     * 初始化配置逻辑
     */
    private static void initProperties() throws IOException {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();
        atlasMqTopicLoader = new AtlasMqTopicLoader();
        atlasMqTopicLoader.loadProperties();
        atlasMqTopicLoader.startRefreshAtlasMqTopicInfoTask();
        commitLogAppendHandler = new CommitLogAppendHandler();
//        List<AtlasMqTopicModel> atlasMqTopicModelList = CommonCache.getAtlasMqTopicModelList();
        for (AtlasMqTopicModel atlasMqTopicModel : CommonCache.getAtlasMqTopicModelMap().values()) {
            String topicName = atlasMqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // 加载配置，缓存对象的生成
        initProperties();

        // 模拟初始化文件映射
        String topic = "order_cancel_topic";
        for (int i = 0; i < 50000; i++) {
            commitLogAppendHandler.appendMessage(topic, ("this is content" + i).getBytes());
            TimeUnit.MILLISECONDS.sleep(1);
        }

//        commitLogAppendHandler.readMsg(topic);
    }
}
