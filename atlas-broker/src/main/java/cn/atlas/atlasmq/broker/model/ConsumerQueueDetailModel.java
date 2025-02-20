package cn.atlas.atlasmq.broker.model;

import cn.atlas.atlasmq.broker.utils.ByteConvertUtil;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: ConsumerQueue数据结构存储的最小单元对象
 */
public class ConsumerQueueDetailModel {
    private int commitLogFileName;  // 4byte

    private int msgIndex; // 4byte  commitLog数据存储的地址，mmap映射的地址，会有Integer.max校验，因此只能使用int类型

    private int msgLength;  // 消息长度

    public int getCommitLogFileName() {
        return commitLogFileName;
    }

    public void setCommitLogFileName(int commitLogFileName) {
        this.commitLogFileName = commitLogFileName;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    public Integer getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(Integer msgLength) {
        this.msgLength = msgLength;
    }

    public byte[] convertToBytes() {
        byte[] commitLogFileNameBytes = ByteConvertUtil.intToBytes(commitLogFileName);
        byte[] msgIndexBytes = ByteConvertUtil.intToBytes(msgIndex);
        byte[] msgLengthBytes = ByteConvertUtil.intToBytes(msgLength);
        byte[] finalBytes = new byte[12];
        System.arraycopy(commitLogFileNameBytes, 0, finalBytes, 0, commitLogFileNameBytes.length);
        System.arraycopy(msgIndexBytes, 0, finalBytes, commitLogFileNameBytes.length, msgIndexBytes.length);
        System.arraycopy(msgLengthBytes, 0, finalBytes, commitLogFileNameBytes.length + msgIndexBytes.length, msgLengthBytes.length);
        return finalBytes;
    }

    public void buildFromBytes(byte[] bytes) {
        this.setCommitLogFileName(ByteConvertUtil.bytesToInt(ByteConvertUtil.readInPos(bytes, 0, 4)));
        this.setMsgIndex(ByteConvertUtil.bytesToInt(ByteConvertUtil.readInPos(bytes, 4, 4)));
        this.setMsgLength(ByteConvertUtil.bytesToInt(ByteConvertUtil.readInPos(bytes, 8, 4)));
    }


}
