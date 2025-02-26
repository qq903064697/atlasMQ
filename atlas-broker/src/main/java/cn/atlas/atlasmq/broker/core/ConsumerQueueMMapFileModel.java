package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.model.QueueModel;
import cn.atlas.atlasmq.broker.utils.LogFileNameUtil;
import cn.atlas.atlasmq.broker.utils.PutMessageLock;
import cn.atlas.atlasmq.broker.utils.UnfairReentrantLock;
import cn.atlas.atlasmq.nameserver.event.spi.listener.HeartBeatListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 对consumerQueue文件做mmap映射的核心对象
 */
public class ConsumerQueueMMapFileModel {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerQueueMMapFileModel.class);

    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private ByteBuffer readBuffer;
    private FileChannel fileChannel;
    private String topic;
    private Integer queueId;
    private String consumerQueueFileName;
    private PutMessageLock putMessageLock;




    /**
     * 指定offset做文件的映射
     *
     * @param topicName    消息主题
     * @param queueId       消息队列id
     * @param startOffset 开始映射的offset
     * @param mappedSize  映射的内存体积
     */
    public void loadFileInMMap(String topicName,int queueId, int startOffset,int latestWriteOffset, int mappedSize) throws IOException {
        this.topic = topicName;
        this.queueId = queueId;

        String filePath = getLatestConsumerQueueFile();
        this.doMMap(filePath, startOffset, latestWriteOffset,  mappedSize);
        putMessageLock = new UnfairReentrantLock();
    }

    /**
     *
     * @param filePath
     * @param startOffset
     * @param mappedSize
     */
    private void doMMap(String filePath, int startOffset,int latestWriteOffset, int mappedSize) throws IOException {
        this.file = new File(filePath);
        if (!this.file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " invalid");
        }
        this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
        this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
        this.readBuffer = mappedByteBuffer.slice();
        this.mappedByteBuffer.position(latestWriteOffset);
    }

    public void writeContent(byte[] content) {
        this.writeContent(content, false);
    }

    public void writeContent(byte[] content, boolean force) {
        try {
            putMessageLock.lock();
            mappedByteBuffer.put(content);
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            putMessageLock.unlock();
        }
    }

    /**
     * 读取consumerQueue数据内容
     * @param pos
     * @return
     */
    public byte[] readContent(int pos) {
        // consumerQueue每个单元文件存储的固定大小是12字节
//        int size = 12;
        // 再开一个slice的目的是每个线程都可以独立操作，互相不影响
        ByteBuffer readBuf = readBuffer.slice();
        readBuf.position(pos);
        byte[] content = new byte[BrokerConstants.CONSUMER_QUEUE_EACH_MSG_SIZE];
        readBuf.get(content);
        return content;

    }
    /**
     * 读取consumerqueue数据内容
     *
     * @param pos      消息读取开始位置
     * @param msgCount 消息条数
     * @return
     */
    public List<byte[]> readContent(int pos, int msgCount) {
        ByteBuffer readBuf = readBuffer.slice();
        readBuf.position(pos);
        List<byte[]> loadContentList = new ArrayList<>();
        for (int i = 0; i < msgCount; i++) {
            byte[] content = new byte[BrokerConstants.CONSUMER_QUEUE_EACH_MSG_SIZE];
            readBuf.get(content);
            loadContentList.add(content);
        }
        return loadContentList;
    }

    /**
     * 获取最新的consumerQueue文件
     * @return
     */
    private String getLatestConsumerQueueFile() {
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        if (atlasMqTopicModel == null) {
            throw new IllegalArgumentException("topic is invalid! topicName is " + topic);
        }
        List<QueueModel> queueList = atlasMqTopicModel.getQueueList();
        QueueModel queueModel = queueList.get(queueId);
        if (queueModel == null) {
            throw new IllegalArgumentException("queueId is invalid! queueId is " + queueId);
        }
        int diff = queueModel.countDiff();
        String filePath = null;
        if (diff == 0) {
            // 已经写满了，需要创建新的文件
            filePath = this.createNewConsumerQueueFile(queueModel.getFileName());
        } else if (diff > 0) {
            // 还有机会写入
            filePath = LogFileNameUtil.buildConsumerQueueFilePath(topic, queueId, queueModel.getFileName());
        }
        return filePath;
    }

    private String createNewConsumerQueueFile(String consumerQueueFileName) {
        // 创建新的文件
        String fileName = LogFileNameUtil.incrementConsumerQueueFileName(consumerQueueFileName);
        String filePath = LogFileNameUtil.buildConsumerQueueFilePath(topic, queueId, fileName);
        File file = new File(filePath);
        try {
            file.createNewFile();
            logger.info("创建了新的consumerQueue文件");
        } catch (IOException e) {}
        return filePath;
    };

    public Integer getQueueId() {
        return queueId;
    }

}
