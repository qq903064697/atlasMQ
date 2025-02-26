package cn.atlas.atlasmq.client.producer;

import cn.atlas.atlasmq.common.dto.MessageDTO;

/**
 * @Author xiaoxin
 * @Description
 */
public interface Producer {

    /**
     * 同步发送
     *
     * @param messageDTO
     * @return
     */
    SendResult send(MessageDTO messageDTO);

    /**
     * 异步发送
     *
     * @param messageDTO
     * @return
     */
    void sendAsync(MessageDTO messageDTO);
}
