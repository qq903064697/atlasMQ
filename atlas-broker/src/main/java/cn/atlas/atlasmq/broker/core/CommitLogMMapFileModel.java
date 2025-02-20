package cn.atlas.atlasmq.broker.core;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.constants.BrokerConstants;
import cn.atlas.atlasmq.broker.model.*;
import cn.atlas.atlasmq.broker.utils.*;
import sun.nio.ch.FileChannelImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 最基础的mmap对象模型
 */
public class CommitLogMMapFileModel {
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private ByteBuffer readByteBuffer;
    private FileChannel fileChannel;
    private String topic;
    private PutMessageLock putMessageLock;

    /**
     * 指定offset做文件的映射
     *
     * @param topicName    消息主题
     * @param startOffset 开始映射的offset
     * @param mappedSize  映射的内存体积
     */
    public void loadFileInMMap(String topicName, int startOffset, int mappedSize) throws IOException {
        this.topic = topicName;
        String filePath = getLatestCommitLogFile(topicName);
        this.doMMap(filePath, startOffset, mappedSize);
        // 默认非公平
        putMessageLock = new UnfairReentrantLock();
    }

    /**
     * 执行mmap映射
     * @param filePath
     * @param startOffset
     * @param mappedSize
     * @throws FileNotFoundException
     */
    private void doMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        this.file = new File(filePath);
        if (!this.file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " invalid");
        }
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
        this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
        this.readByteBuffer = mappedByteBuffer.slice();
        this.mappedByteBuffer.position(atlasMqTopicModel.getCommitLogModel().getOffset().get());
    }



    /**
     * 获取最新的commitLog文件
     * @param topicName
     * @return
     */
    private String getLatestCommitLogFile(String topicName) {
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topicName);
        if (atlasMqTopicModel == null) {
            throw new IllegalArgumentException("topic is invalid! topicName is " + topicName);
        }
        CommitLogModel commitLogModel = atlasMqTopicModel.getCommitLogModel();
        long diff = commitLogModel.countDiff();
//        String latestCommitLogFileName= null;
        String filePath = null;
        if (diff == 0) {
            // 已经写满了，需要创建新的文件
            CommitLogFilePath commitLogFilePath = this.createNewCommitLogFile(topicName, commitLogModel);
            filePath = commitLogFilePath.getFilePath();
        } else if (diff > 0) {
            // 还有机会写入
//            latestCommitLogFileName = commitLogModel.getFileName();
            filePath = LogFileNameUtil.buildCommitLogFilePath(topicName, commitLogModel.getFileName());
        }
        return filePath;
    }



    /**
     *
     */
    private CommitLogFilePath createNewCommitLogFile(String topicName, CommitLogModel commitLogModel) {
        String newFileName = LogFileNameUtil.incrementCommitLogFileName(commitLogModel.getFileName());
        String newFilePath = LogFileNameUtil.buildCommitLogFilePath(topicName, newFileName);
        File newCommitLogFile = new File(newFilePath);
        try {
            // 创建新的commitLog文件
            newCommitLogFile.createNewFile();
            System.out.println("创建了新的commitLog文件");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new CommitLogFilePath(newFilePath, newFileName);
    }

    /**
     * 支持从文件的指定offset开始读取指定大小的内容
     *
     * @param pos
     * @param length
     * @return
     */
    public byte[] readContent(int pos, int length) {
        ByteBuffer readBuf = readByteBuffer.slice();
        readBuf.position(pos);
        byte[] readBytes = new byte[length];
        readBuf.get(readBytes);
        return readBytes;
    }

    /**
     * 写入数据到磁盘中，默认方法
     *
     * @param commitLogMessageModel
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel) throws IOException {
        // 默认写入到page cache中
        // 如果需要强制刷盘，这里要兼容
        this.writeContent(commitLogMessageModel, false);
    }

    /**
     * 写入数据到磁盘中，支持刷盘
     *
     * @param commitLogMessageModel
     * @param force
     */
    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) throws IOException {
        // 定位到最新的commitLog文件中，记录下当前文件是否已经写满，如果已经写满，则创建一个新的文件，并且做新的mmap映射
        // 如果当前文件没有写满，对content内容做一层封装 done
        // 再判断写入是否会导致commitLog写满，如果不会，则选择当前commitLog，否则创建一个新的文件，并且做新的mmap映射 done
        // 定位到最新的commitLog文件之后，写入 done
        // 定义一个对象专门管理各个topic的最新写入offset值，并且定时刷新到磁盘中（缺少同步到磁盘的机制）
        // 写入数据，offset变更，如果是高并发场景，offset是不是会被多个线程访问？


        // offset会用一个原子类AtomicLong去管理
        // 加锁机制（锁的选择非常重要）

        // 判断当前commitLog是否还有空间
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        if (atlasMqTopicModel == null) {
            throw new IllegalArgumentException("atlasMqTopicModel is null! topicName is " + topic);
        }
        CommitLogModel commitLogModel = atlasMqTopicModel.getCommitLogModel();
        if (commitLogModel == null) {
            throw new IllegalArgumentException("commitLogModel is null! topicName is " + topic);
        }
        putMessageLock.lock();
        this.checkCommitLogHasEnableSpace(commitLogMessageModel);
        byte[] writeContent = commitLogMessageModel.convertToBytes();
        mappedByteBuffer.put(writeContent);
        AtomicInteger currentLatestMsgOffset = commitLogModel.getOffset();
        this.dispatcher(writeContent, currentLatestMsgOffset.get());
        currentLatestMsgOffset.addAndGet(writeContent.length);
        if (force) {
            // 强制刷盘
            mappedByteBuffer.force();
        }

        putMessageLock.unlock();
    }

    /**
     * 将ConsumerQueue文件写入
     * @param writeContent
     */
    private void dispatcher(byte[] writeContent, int msgIndex) {
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        if (atlasMqTopicModel == null) {
            throw new RuntimeException("topic is undefined");
        }
        // todo
        int queueId = 0;

        ConsumerQueueDetailModel consumerQueueDetailModel = new ConsumerQueueDetailModel();
        consumerQueueDetailModel.setCommitLogFileName(Integer.parseInt(atlasMqTopicModel.getCommitLogModel().getFileName()));
        consumerQueueDetailModel.setMsgIndex(msgIndex);
        consumerQueueDetailModel.setMsgLength(writeContent.length);
//        System.out.println("写入consumerQueue内容：" + JSON.toJSONString(consumerQueueDetailModel));
        byte[] content = consumerQueueDetailModel.convertToBytes();
        consumerQueueDetailModel.buildFromBytes(content);
//        System.out.println("byte convert is " + JSON.toJSONString(consumerQueueDetailModel));
        List<ConsumerQueueMMapFileModel> consumerQueueMMapFileModelList = CommonCache.getConsumerQueueMMapFileModelManager().get(topic);
        ConsumerQueueMMapFileModel consumerQueueMMapFileModel = consumerQueueMMapFileModelList.stream().filter(queueModel -> queueModel.getQueueId().equals(queueId)).findFirst().orElse(null);
        consumerQueueMMapFileModel.writeContent(content, true);
        // 刷新offset
        QueueModel queueModel = atlasMqTopicModel.getQueueList().get(queueId);
        queueModel.getLatestOffset().addAndGet(content.length);


    }

    /**
     * 判断当前commitLog是否还有空间
     * @param commitLogMessageModel
     * @throws FileNotFoundException
     */
    private void checkCommitLogHasEnableSpace(CommitLogMessageModel commitLogMessageModel) throws IOException {
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(this.topic);
        CommitLogModel commitLogModel = atlasMqTopicModel.getCommitLogModel();
        long writeEnableOffset = commitLogModel.countDiff();
        // 空间不足，需要创建新的commitLog文件
        if (writeEnableOffset < commitLogMessageModel.convertToBytes().length) {
            // 00000000文件 -> 00000001文件
            // commitLog文件会有空闲碎片
            CommitLogFilePath commitLogFilePath = this.createNewCommitLogFile(this.topic, commitLogModel);
            commitLogModel.setOffsetLimit((long) BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
            commitLogModel.setOffset(new AtomicInteger(0));
            commitLogModel.setFileName(commitLogFilePath.getFileName());
            // 新文件路径映射
            this.doMMap(commitLogFilePath.getFilePath(), 0, BrokerConstants.COMMIT_LOG_DEFAULT_MMAP_SIZE);
        }
    }

    //不推荐使用
    public void clear() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 在关闭资源是执行以下代码释放内存
        // 不推荐的原因是因为使用了sun包下不稳定的代码
        Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
        m.setAccessible(true);
        m.invoke(FileChannelImpl.class, mappedByteBuffer);
    }


    /**
     * 释放mmap内存占用
     */
    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) {
            return;
        }
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null) {
            return buffer;
        } else {
            return viewed(viewedBuffer);
        }
    }

    class CommitLogFilePath {
        private String filePath;
        private String fileName;

        public CommitLogFilePath(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

}
