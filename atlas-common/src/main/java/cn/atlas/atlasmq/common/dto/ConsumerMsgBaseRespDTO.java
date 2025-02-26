package cn.atlas.atlasmq.common.dto;

import java.util.List;

/**
 * @Author xiaoxin
 * @Create 2025/2/25 下午4:31
 * @Version 1.0
 */
public class ConsumerMsgBaseRespDTO extends BaseBrokerRemoteDTO{

    private List<ConsumerMsgRespDTO> consumerMsgRespDTOList;

    public List<ConsumerMsgRespDTO> getConsumerMsgRespDTOList() {
        return consumerMsgRespDTOList;
    }

    public void setConsumerMsgRespDTOList(List<ConsumerMsgRespDTO> consumerMsgRespDTOList) {
        this.consumerMsgRespDTOList = consumerMsgRespDTOList;
    }
}
