package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.SlaveAckDTO;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.SlaveReplicationMsgAckEvent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 主节点接收从节点同步ack信号处理器
 */
public class SlaveReplicationMsgAckEventListener implements Listener<SlaveReplicationMsgAckEvent> {
    @Override
    public void onReceive(SlaveReplicationMsgAckEvent event) throws Exception {
        String slaveAckMsgId = event.getMsgId();
        SlaveAckDTO slaveAckDTO = CommonCache.getAckMap().get(slaveAckMsgId);
        if (slaveAckDTO == null) {
            return;
        }
        int currentAckTime = slaveAckDTO.getNeedAckTime().decrementAndGet();
        // 如果是同步复制模式，代表所有从节点已经ack完毕了
        // 如果是半同步复制模式，
        if (currentAckTime == 0) {
            CommonCache.getAckMap().remove(slaveAckMsgId);
            slaveAckDTO.getBrokerChannel().writeAndFlush(new TcpMsg(NameServerResponseCode.REGISTRY_SUCCESS.getCode(), NameServerResponseCode.REGISTRY_SUCCESS.getDesc().getBytes()));
        }
    }
}
