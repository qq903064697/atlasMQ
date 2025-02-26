package cn.atlas.atlasmq.common.dto;


import cn.atlas.atlasmq.common.enums.SendMessageToBrokerResponseStatus;

/**
 * @Author idea
 * @Date: Created in 20:08 2024/6/15
 * @Description
 */
public class SendMessageToBrokerResponseDTO extends BaseBrokerRemoteDTO {

    /**
     * 发送消息的结果状态
     * @see SendMessageToBrokerResponseStatus
     */
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
