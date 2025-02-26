package cn.atlas.atlasmq.broker.event.spi.listener;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.event.model.PushMsgEvent;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.MessageDTO;
import cn.atlas.atlasmq.common.dto.SendMessageToBrokerResponseDTO;
import cn.atlas.atlasmq.common.enums.BrokerResponseCode;
import cn.atlas.atlasmq.common.enums.MessageSendWay;
import cn.atlas.atlasmq.common.enums.SendMessageToBrokerResponseStatus;
import cn.atlas.atlasmq.common.event.Listener;
import com.alibaba.fastjson.JSON;


/**
 * @Author idea
 * @Date: Created in 09:46 2024/6/16
 * @Description
 */
public class PushMsgListener implements Listener<PushMsgEvent> {

    @Override
    public void onReceive(PushMsgEvent event) throws Exception {
        //消息写入commitLog
        MessageDTO messageDTO = event.getMessageDTO();
        CommonCache.getCommitLogAppendHandler().appendMessage(messageDTO);
        int sendWay = messageDTO.getSendWay();
        if(MessageSendWay.ASYNC.getCode() == sendWay) {
            return;
        }
        SendMessageToBrokerResponseDTO sendMessageToBrokerResponseDTO = new SendMessageToBrokerResponseDTO();
        sendMessageToBrokerResponseDTO.setStatus(SendMessageToBrokerResponseStatus.SUCCESS.getCode());
        sendMessageToBrokerResponseDTO.setMsgId(messageDTO.getMsgId());
        TcpMsg responseMsg = new TcpMsg(BrokerResponseCode.SEND_MSG_RESP.getCode(), JSON.toJSONBytes(sendMessageToBrokerResponseDTO));
        event.getChannelHandlerContext().writeAndFlush(responseMsg);
    }
}
