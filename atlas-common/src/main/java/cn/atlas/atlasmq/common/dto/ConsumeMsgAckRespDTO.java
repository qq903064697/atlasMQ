package cn.atlas.atlasmq.common.dto;


/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class ConsumeMsgAckRespDTO extends BaseBrokerRemoteDTO{

    /**
     * ack是否成功
     * @see cn.atlas.atlasmq.common.enums.AckStatus
     */
    private int ackStatus;

    public int getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(int ackStatus) {
        this.ackStatus = ackStatus;
    }
}