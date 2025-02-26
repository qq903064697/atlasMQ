package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.event.Listener;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import cn.atlas.atlasmq.nameserver.event.model.SlaveReplicationMsgAckEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;
import com.alibaba.fastjson.JSON;

/**
 * @Author xiaoxin
 * @Description 从节点专属的数据同步监听器
 */
public class SlaveReplicationMsgListener implements Listener<ReplicationMsgEvent> {


    @Override
    public void onReceive(ReplicationMsgEvent event) throws Exception {
        ServiceInstance serviceInstance = event.getServiceInstance();
        //从节点接收主节点同步数据逻辑
        CommonCache.getServiceInstanceManager().put(serviceInstance);

        System.out.println("从节点接收到主节点数据");
        SlaveReplicationMsgAckEvent slaveReplicationMsgAckEvent = new SlaveReplicationMsgAckEvent();
        slaveReplicationMsgAckEvent.setMsgId(event.getMsgId());
        event.getChannelHandlerContext().channel().writeAndFlush(new TcpMsg(NameServerEventCode.MASTER_START_REPLICATION_ACK.getCode(), JSON.toJSONBytes(slaveReplicationMsgAckEvent)));

    }
}
