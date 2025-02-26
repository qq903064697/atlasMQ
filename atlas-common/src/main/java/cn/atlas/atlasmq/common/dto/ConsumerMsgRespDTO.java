package cn.atlas.atlasmq.common.dto;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 22:29 2024/6/19
 * @Description
 */
public class ConsumerMsgRespDTO {

    /**
     * 队列id
     */
    private Integer queueId;

    /**
     * 拉数据返回内容
     */
    private List<byte[]> commitLogContentList;


    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public List<byte[]> getCommitLogContentList() {
        return commitLogContentList;
    }

    public void setCommitLogContentList(List<byte[]> commitLogContentList) {
        this.commitLogContentList = commitLogContentList;
    }
}
