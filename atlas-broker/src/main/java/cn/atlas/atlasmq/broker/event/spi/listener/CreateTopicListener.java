package cn.atlas.atlasmq.broker.event.spi.listener;


import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.event.model.CreateTopicEvent;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.model.CommitLogModel;
import cn.atlas.atlasmq.broker.model.QueueModel;
import cn.atlas.atlasmq.broker.utils.LogFileNameUtil;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.common.dto.CreateTopicReqDTO;
import cn.atlas.atlasmq.common.event.Listener;
import cn.atlas.atlasmq.common.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiaoxin
 * @description 创建topic监听器
 */
public class CreateTopicListener implements Listener<CreateTopicEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CreateTopicListener.class);

    @Override
    public void onReceive(CreateTopicEvent event) throws Exception {
        CreateTopicReqDTO createTopicReqDTO = event.getCreateTopicReqDTO();
        String topic = createTopicReqDTO.getTopic();
        AssertUtils.isTrue(createTopicReqDTO.getQueueSize() > 0 && createTopicReqDTO.getQueueSize() < 100, "queueSize参数异常");
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        AssertUtils.isTrue(atlasMqTopicModel == null, "topic已经存在");
        createTopicFile(createTopicReqDTO);
        loadFileInMMap(createTopicReqDTO);
        addTopicInCommonCache(createTopicReqDTO);
        logger.info("topic:{} is created! queueSize is {}", createTopicReqDTO.getTopic(), createTopicReqDTO.getQueueSize());
    }

    /**
     * 创建topic对应的文件
     *
     * @param createTopicReqDTO
     * @throws IOException
     */
    public static void createTopicFile(CreateTopicReqDTO createTopicReqDTO) throws IOException {

        String baseCommitLogDirPath = LogFileNameUtil.buildCommitLogBasePath(createTopicReqDTO.getTopic());
        File commitLogDir = new File(baseCommitLogDirPath);
        commitLogDir.mkdir();
        File commitLogFile = new File(baseCommitLogDirPath + BrokerConstants.SPLIT + LogFileNameUtil.buildFirstCommitLogFileName());
        commitLogFile.createNewFile();

        String baseConsumerQueueDirPath = LogFileNameUtil.buildConsumerQueueBasePath(createTopicReqDTO.getTopic());
        File consumerQueueDir = new File(baseConsumerQueueDirPath);
        consumerQueueDir.mkdir();
        for (int i = 0; i < createTopicReqDTO.getQueueSize(); i++) {
            new File(baseConsumerQueueDirPath + BrokerConstants.SPLIT + i).mkdir();
            new File(baseConsumerQueueDirPath + BrokerConstants.SPLIT + i + BrokerConstants.SPLIT + LogFileNameUtil.buildFirstConsumerQueueFileName()).createNewFile();
        }

    }

    /**
     * 加载文件到mmap中
     *
     * @param createTopicReqDTO
     * @throws IOException
     */
    public static void loadFileInMMap(CreateTopicReqDTO createTopicReqDTO) throws IOException {
        CommonCache.getCommitLogAppendHandler().prepareMMapLoading(createTopicReqDTO.getTopic());
        CommonCache.getConsumerQueueAppendHandler().prepareConsumerQueueMMapLoading(createTopicReqDTO.getTopic());

    }

    /**
     * 添加topic到缓存中
     *
     * @param createTopicReqDTO
     */
    public static void addTopicInCommonCache(CreateTopicReqDTO createTopicReqDTO) {
        AtlasMqTopicModel atlasMqTopicModel = new AtlasMqTopicModel();
        atlasMqTopicModel.setTopic(createTopicReqDTO.getTopic());
        long currentTimeStamp = System.currentTimeMillis();
        atlasMqTopicModel.setCreateAt(currentTimeStamp);
        atlasMqTopicModel.setUpdateAt(currentTimeStamp);
        CommitLogModel commitLogModel = new CommitLogModel();
        commitLogModel.setFileName(LogFileNameUtil.buildFirstCommitLogFileName());
        commitLogModel.setOffsetLimit(BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE.longValue());
        commitLogModel.setOffset(new AtomicInteger(0));
        atlasMqTopicModel.setCommitLogModel(commitLogModel);
        List<QueueModel> queueModelList = new ArrayList<>();
        for (int i = 0; i < createTopicReqDTO.getQueueSize(); i++) {
            QueueModel queueModel = new QueueModel();
            queueModel.setId(i);
            queueModel.setFileName(LogFileNameUtil.buildFirstConsumerQueueFileName());
            queueModel.setOffsetLimit(BrokerConstants.COMSUMER_QUEUE_DEFAULT_MMAP_SIZE);
            queueModel.setLatestOffset(new AtomicInteger(0));
            queueModel.setLastOffset(0);
            queueModelList.add(queueModel);
        }
        atlasMqTopicModel.setQueueList(queueModelList);
        CommonCache.getAtlasMqTopicModelList().add(atlasMqTopicModel);
    }



}
