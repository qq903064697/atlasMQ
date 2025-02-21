package cn.atlas.atlasmq.broker.config;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.utils.FileContentUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.util.internal.StringUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 负责将mq的主题配置加载到内存中
 */
public class AtlasMqTopicLoader {
//    private AtlasMqTopicModel atlasMqTopicModel;
    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getAtlasMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("ATLAS_MQ_HOME is null");
        }
        filePath = basePath + "/broker/config/atlasmq-topic.json";
        String fileContent = FileContentUtil.readFromFile(filePath);
        List<AtlasMqTopicModel> atlasMqTopicLoaderList = JSON.parseArray(fileContent, AtlasMqTopicModel.class);
        CommonCache.setAtlasMqTopicModelList(atlasMqTopicLoaderList);
//        CommonCache.setAtlasMqTopicModelMap(atlasMqTopicLoaderList.stream().collect(Collectors.toMap(AtlasMqTopicModel::getTopic, item -> item)));
    }

    /**
     * 开启一个刷新内存到磁盘的任务
     */
    public void startRefreshAtlasMqTopicInfoTask() {
        //异步线程
        //每个10秒将内存的配置刷新到磁盘中
        CommonThreadPoolConfig.refreshAtlasMqTopicExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP);
//                        System.out.println("刷新磁盘");
                        List<AtlasMqTopicModel> atlasMqTopicModelList = CommonCache.getAtlasMqTopicModelList();
                        FileContentUtil.overWriteToFile(filePath, JSON.toJSONString(atlasMqTopicModelList, SerializerFeature.PrettyFormat));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } while (true);
            }
        });
    }
}
