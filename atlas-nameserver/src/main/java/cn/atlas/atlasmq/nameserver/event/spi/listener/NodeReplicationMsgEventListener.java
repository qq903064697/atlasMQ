package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.TraceReplicationProperties;
import cn.atlas.atlasmq.nameserver.event.model.NodeReplicationAckMsgEvent;
import cn.atlas.atlasmq.nameserver.event.model.NodeReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import com.alibaba.fastjson.JSON;
import io.netty.util.internal.StringUtil;

import java.net.Inet4Address;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 接收上一个节点同步过来的复制数据内容
 */
public class NodeReplicationMsgEventListener implements Listener<NodeReplicationMsgEvent>{
    @Override
    public void onReceive(NodeReplicationMsgEvent event) throws Exception {
        ServiceInstance serviceInstance = event.getServiceInstance();
        //接收到上一个节点同步过来的数据，然后存入本地内存
        CommonCache.getServiceInstanceManager().put(serviceInstance);
        ReplicationMsgEvent replicationMsgEvent = new ReplicationMsgEvent();
        replicationMsgEvent.setServiceInstance(serviceInstance);
        replicationMsgEvent.setMsgId(event.getMsgId());
        replicationMsgEvent.setType(event.getType());
        System.out.println("接收到上一个节点写入的数据:" + JSON.toJSONString(replicationMsgEvent));
        CommonCache.getReplicationMsgQueueManager().put(replicationMsgEvent);
        TraceReplicationProperties traceReplicationProperties = CommonCache.getNameserverProperties().getTraceReplicationProperties();
        if (StringUtil.isNullOrEmpty(traceReplicationProperties.getNextNode())) {
            //如果是尾部节点，不需要再给下一个节点做复制，但是要返回ack给上一个节点
            //node1->node2->node3->node4
            NodeReplicationAckMsgEvent nodeReplicationAckMsgEvent = new NodeReplicationAckMsgEvent();
            nodeReplicationAckMsgEvent.setNodeIp(Inet4Address.getLocalHost().getHostAddress());
            nodeReplicationAckMsgEvent.setNodePort(traceReplicationProperties.getPort());
            CommonCache.getPreNodeChannel().writeAndFlush(new TcpMsg(NameServerEventCode.NODE_REPLICATION_ACK_MSG.getCode(), JSON.toJSONBytes(nodeReplicationAckMsgEvent)));
        }
    }
}
