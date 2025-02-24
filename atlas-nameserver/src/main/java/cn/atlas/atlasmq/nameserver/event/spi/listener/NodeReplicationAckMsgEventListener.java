package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.NodeReplicationAckMsgEvent;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.NodeAckDTO;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.enums.ReplicationMsgTypeEnum;
import cn.atlas.atlasmq.nameserver.event.model.NodeReplicationAckMsgEvent;


/**
 * @Author idea
 * @Date: Created in 10:50 2024/6/1
 * @Description 接收上一个节点同步完成的ack信号
 */
public class NodeReplicationAckMsgEventListener implements Listener<NodeReplicationAckMsgEvent> {

    @Override
    public void onReceive(NodeReplicationAckMsgEvent event) throws Exception {
        boolean isHeadNode = CommonCache.getPreNodeChannel() == null;
        if (isHeadNode) {
            System.out.println("当前是头节点，收到下游节点ack反馈");
            //如果是头节点，直接告诉broker客户端整个链路同步复制完成
            NodeAckDTO nodeAckDTO = CommonCache.getNodeAckMap().get(event.getMsgId());
            //根据下游返回的msgId匹配先前发送端的msgId，找到broker的连接通道
            Channel brokerChannel = nodeAckDTO.getChannelHandlerContext().channel();
            if (!brokerChannel.isActive()) {
                //如果broker已经断开了，异常抛出
                throw new RuntimeException("broker connection is broken!");
            }
            CommonCache.getNodeAckMap().remove(event.getMsgId());
            if(ReplicationMsgTypeEnum.REGISTRY.getCode() == event.getType()) {
                brokerChannel.writeAndFlush(new TcpMsg(NameServerResponseCode.REGISTRY_SUCCESS.getCode(), NameServerResponseCode.REGISTRY_SUCCESS.getDesc().getBytes()));
            } else if (ReplicationMsgTypeEnum.HEART_BEAT.getCode() == event.getType()) {
                brokerChannel.writeAndFlush(new TcpMsg(NameServerResponseCode.HEART_BEAT_SUCCESS.getCode(), NameServerResponseCode.HEART_BEAT_SUCCESS.getDesc().getBytes()));
            }
        } else {
            System.out.println("当前是中间节点，通知给上游节点ack回应");
            //当前节点是不是中间节点，中间节点，还得告知上一个节点同步完成
            CommonCache.getPreNodeChannel().writeAndFlush(new TcpMsg(NameServerEventCode.NODE_REPLICATION_ACK_MSG.getCode(), JSON.toJSONBytes(event)));
        }
    }
}
