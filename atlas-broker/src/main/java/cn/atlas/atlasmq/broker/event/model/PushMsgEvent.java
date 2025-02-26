package cn.atlas.atlasmq.broker.event.model;


import cn.atlas.atlasmq.common.dto.MessageDTO;
import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @Author idea
 * @Date: Created in 09:45 2024/6/16
 * @Description
 */
public class PushMsgEvent extends Event {

    private MessageDTO messageDTO;

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }
}
