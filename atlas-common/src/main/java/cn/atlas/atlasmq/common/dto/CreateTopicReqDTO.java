package cn.atlas.atlasmq.common.dto;

/**
 * @Author xiaoxin
 * @description 创建topic请求
 */
public class CreateTopicReqDTO extends BaseBrokerRemoteDTO {
    private String topic;

    private Integer queueSize;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}
