package cn.atlas.atlasmq.client.consumer;

/**
 * @Author idea
 * @Date: Created in 11:07 2024/6/16
 * @Description
 */
public class ConsumerMessage {

    private int queueId;

    private byte[] body;

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
