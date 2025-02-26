package cn.atlas.atlasmq.broker.event.model;

import cn.atlas.atlasmq.common.dto.ConsumerMsgReqDTO;
import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 消费mq消息事件
 */
public class ConsumerMsgEvent extends Event {
    private ConsumerMsgReqDTO consumerMsgReqDTO;

    public ConsumerMsgReqDTO getConsumerMsgReqDTO() {
        return consumerMsgReqDTO;
    }

    public void setConsumerMsgReqDTO(ConsumerMsgReqDTO consumerMsgReqDTO) {
        this.consumerMsgReqDTO = consumerMsgReqDTO;
    }
}
