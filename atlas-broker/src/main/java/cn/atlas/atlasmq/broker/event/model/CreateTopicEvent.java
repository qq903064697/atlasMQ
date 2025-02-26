package cn.atlas.atlasmq.broker.event.model;


import cn.atlas.atlasmq.common.dto.CreateTopicReqDTO;
import cn.atlas.atlasmq.common.event.model.Event;

/**
 * @author idea
 * @description 创建topic事件
 */
public class CreateTopicEvent extends Event {

    private CreateTopicReqDTO createTopicReqDTO;

    public CreateTopicReqDTO getCreateTopicReqDTO() {
        return createTopicReqDTO;
    }

    public void setCreateTopicReqDTO(CreateTopicReqDTO createTopicReqDTO) {
        this.createTopicReqDTO = createTopicReqDTO;
    }
}
