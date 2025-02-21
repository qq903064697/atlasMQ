package cn.atlas.atlasmq.broker.utils;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午5:05
 * @Version 1.0
 */
public class LogFileNameUtil {
    /**
     * 构建第一份commitLog文件名称
     * @return
     */
    public static String buildFirstCommitLogFileName() {
        return "00000000";
    }
    public static String buildCommitLogFilePath(String topicName, String commitLogFileName) {
        return CommonCache.getGlobalProperties().getAtlasMqHome()
                + BrokerConstants.BASE_COMMIT_LOG_PATH
                + topicName
                + BrokerConstants.SPLIT
                + commitLogFileName;
    }

    /**
     * 构建consumerQueue文件路径
     * @param topicName
     * @param queueId
     * @param consumerQueueFileName
     * @return
     */
    public static String buildConsumerQueueFilePath(String topicName, Integer queueId, String consumerQueueFileName) {
        return CommonCache.getGlobalProperties().getAtlasMqHome()
                + BrokerConstants.BASE_CONSUMER_QUEUE_PATH
                + topicName
                + BrokerConstants.SPLIT
                + queueId
                + BrokerConstants.SPLIT
                + consumerQueueFileName;
    }

    public static String incrementCommitLogFileName(String oldFileName) {
        if (oldFileName.length() != 8) {
            throw new IllegalArgumentException("fileName must be 8 chars");
        }
        Long fileIndex = Long.valueOf(oldFileName);
        fileIndex++;
        String newFileName = String.valueOf(fileIndex);
        int newFileNameLen = newFileName.length();
        int needFullLen = 8 - newFileNameLen;
        if (needFullLen < 0) {
            throw new RuntimeException("unknown exception");
        }
        StringBuffer stb = new StringBuffer();
        for (int i = 0; i < needFullLen; i++) {
            stb.append("0");
        }
        stb.append(newFileName);
        return stb.toString();
    }

    public static String incrementConsumerQueueFileName(String oldFileName) {
        return incrementCommitLogFileName(oldFileName);
    }





}
