package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.CommitLogMessageModel;
import cn.atlas.atlasmq.common.dto.MessageDTO;

import java.io.IOException;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Decription:
 */
public class CommitLogAppendHandler {

    public void prepareMMapLoading(String topicName) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel = new CommitLogMMapFileModel();
        commitLogMMapFileModel.loadFileInMMap( topicName,0, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        CommonCache.getCommitLogMMapFileModelManager().put(topicName, commitLogMMapFileModel);
    }

    public void appendMessage(MessageDTO messageDTO) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel = CommonCache.getCommitLogMMapFileModelManager().get(messageDTO.getTopic());
        if (commitLogMMapFileModel == null) {
            throw  new RuntimeException("topic is invalid");
        }
        commitLogMMapFileModel.writeContent(messageDTO, true);
    }


}
