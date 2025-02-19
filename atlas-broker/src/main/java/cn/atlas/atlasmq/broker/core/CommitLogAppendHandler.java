package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.CommitLogMessageModel;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Decription:
 */
public class CommitLogAppendHandler {
    private MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();

//    private String filePath = "D:\\Sync\\Study\\项目\\atlasMQ\\代码\\atlasmq\\broker\\store\\order_cancel_topic\\00000000";
//    private static String topicName = "order_cancel_topic";
    public CommitLogAppendHandler() {
    }

    public void prepareMMapLoading(String topicName) throws IOException {
        MMapFileModel mMapFileModel = new MMapFileModel();
        mMapFileModel.loadFileInMMap( topicName,0, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        mMapFileModelManager.put(topicName, mMapFileModel);
    }

    public void appendMessage(String topic, byte[] content) throws IOException {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw  new RuntimeException("topic is invalid");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setSize(content.length);
        commitLogMessageModel.setContent(content);
        mMapFileModel.writeContent(commitLogMessageModel);
    }

    public void readMsg(String topic) {
        MMapFileModel mMapFileModel = mMapFileModelManager.get(topic);
        if (mMapFileModel == null) {
            throw  new RuntimeException("topic is invalid");
        }
        byte[] content = mMapFileModel.readContent(0, 1000);
        System.out.println(new String(content));
    }

}
