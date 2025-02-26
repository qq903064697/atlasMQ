package cn.atlas.atlasmq.broker.event.model;

import cn.atlas.atlasmq.common.dto.ConsumeMsgAckReqDTO;
import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class ConsumeMsgAckEvent extends Event {
    private ConsumeMsgAckReqDTO consumeMsgAckReqDTO;

    public ConsumeMsgAckReqDTO getConsumeMsgAckReqDTO() {
        return consumeMsgAckReqDTO;
    }

    public void setConsumeMsgAckReqDTO(ConsumeMsgAckReqDTO consumeMsgAckReqDTO) {
        this.consumeMsgAckReqDTO = consumeMsgAckReqDTO;
    }
}
