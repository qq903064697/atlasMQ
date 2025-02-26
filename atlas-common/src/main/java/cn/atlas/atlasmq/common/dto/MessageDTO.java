package cn.atlas.atlasmq.common.dto;

import cn.atlas.atlasmq.common.enums.MessageSendWay;

/**
 * @Author xiaoxin
 * @Description mq消息发送参数
 */
public class MessageDTO {

    private String topic;
    private int queueId = -1;
    private String msgId;
    /**
     * 发送方式（同步/异步）
     * @see MessageSendWay
     */
    private int sendWay;
    private byte[] body;

    public int getSendWay() {
        return sendWay;
    }

    public void setSendWay(int sendWay) {
        this.sendWay = sendWay;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
