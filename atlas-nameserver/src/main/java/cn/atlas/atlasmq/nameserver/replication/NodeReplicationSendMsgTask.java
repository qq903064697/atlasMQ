package cn.atlas.atlasmq.nameserver.replication;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.NodeAckDTO;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.NodeReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;

import java.util.UUID;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 链式复制中，非尾部节点发送数据给下一个节点的任务
 */
public class NodeReplicationSendMsgTask extends ReplicationTask{
    public NodeReplicationSendMsgTask(String taskName) {
        super(taskName);
    }

    @Override
    void startTask() {
        while (true) {
            try {
                // 如果你是头节点 或者中间节点
                ReplicationMsgEvent replicationMsgEvent = CommonCache.getReplicationMsgQueueManager().getReplicationMsgQueue().take();
                replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
                Channel nextNodeChannel = CommonCache.getConnectNodeChannel();
                NodeReplicationMsgEvent nodeReplicationMsgEvent = new NodeReplicationMsgEvent();
                nodeReplicationMsgEvent.setMsgId(replicationMsgEvent.getMsgId());
                nodeReplicationMsgEvent.setServiceInstance(replicationMsgEvent.getServiceInstance());
                NodeAckDTO nodeAckDTO = new NodeAckDTO();
                //broker的连接通道
                nodeAckDTO.setChannelHandlerContext(replicationMsgEvent.getChannelHandlerContext());
                CommonCache.getNodeAckMap().put(replicationMsgEvent.getMsgId(),nodeAckDTO);
                if(nextNodeChannel.isActive()) {
                    nextNodeChannel.writeAndFlush(new TcpMsg(NameServerEventCode.NODE_REPLICATION_MSG.getCode(), JSON.toJSONBytes(nodeReplicationMsgEvent)));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
