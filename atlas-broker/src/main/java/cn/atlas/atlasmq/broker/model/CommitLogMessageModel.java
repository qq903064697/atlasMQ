package cn.atlas.atlasmq.broker.model;

import cn.atlas.atlasmq.broker.utils.ByteConvertUtil;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: commitLog真实数据存储对象模型
 */
public class CommitLogMessageModel {

//    /**
//     * 消息的体积大小，单位是字节
//     */
//    private int size;

    /**
     * 真正的消息内容
     */
    private byte[] content;

//    public int getSize() {
//        return size;
//    }
//
//    public void setSize(int size) {
//        this.size = size;
//    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] convertToBytes() {
//        byte[] sizeByte = ByteConvertUtil.intToBytes(this.getSize());
//        byte[] content = this.getContent();
//        byte[] mergeResultByte = new byte[sizeByte.length + content.length];
//        System.arraycopy(sizeByte, 0, mergeResultByte, 0, sizeByte.length);
//        System.arraycopy(content, 0, mergeResultByte, sizeByte.length, content.length);
//        return mergeResultByte;
        return this.getContent();
    }
}
