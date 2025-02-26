package cn.atlas.atlasmq.common.dto;

/**
 * @Author xiaoxin
 * @Description 消费端拉数据请求DTO
 */
public class ConsumerMsgReqDTO extends BaseBrokerRemoteDTO {

    private String topic;
    private String consumerGroup;
    private String ip;
    private Integer port;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
