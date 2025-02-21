package cn.atlas.atlasmq.broker.config;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.ConsumerQueueOffsetModel;
import cn.atlas.atlasmq.broker.utils.FileContentUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.JSON;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class ConsumerQueueOffsetLoader {
    private String filePath;

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String basePath = globalProperties.getAtlasMqHome();
        if (StringUtil.isNullOrEmpty(basePath)) {
            throw new IllegalArgumentException("ATLAS_MQ_HOME is null");
        }
        filePath = basePath + "/broker/config/consumerqueue-offset.json";
        String fileContent = FileContentUtil.readFromFile(filePath);
        ConsumerQueueOffsetModel consumerQueueOffsetModel = JSON.parseObject(fileContent, ConsumerQueueOffsetModel.class);
        CommonCache.setConsumerQueueOffsetModel(consumerQueueOffsetModel);
    }

    /**
     * 开启一个刷新内存到磁盘的任务
     */
    public void startRefreshConsumerQueueOffsetTask() {
        //异步线程
        //每个10秒将内存的配置刷新到磁盘中
        CommonThreadPoolConfig.refreshConsumerQueueOffsetExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(BrokerConstants.DEFAULT_REFRESH_MQ_TOPIC_TIME_STEP);
//                        System.out.println("consumerQueueOffset刷新磁盘");
                        ConsumerQueueOffsetModel consumerQueueOffsetModel = CommonCache.getConsumerQueueOffsetModel();
                        FileContentUtil.overWriteToFile(filePath, JSON.toJSONString(consumerQueueOffsetModel, SerializerFeature.PrettyFormat));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } while (true);
            }
        });
    }
}
