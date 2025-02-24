package cn.atlas.atlasmq.nameserver.store;

import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.TraceReplicationProperties;
import cn.atlas.atlasmq.nameserver.enums.ReplicationModeEnum;
import cn.atlas.atlasmq.nameserver.enums.ReplicationRoleEnum;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午10:07
 * @Version 1.0
 */
public class ReplicationMsgQueueManager {
    private BlockingQueue<ReplicationMsgEvent> replicationMsgQueue = new ArrayBlockingQueue(5000);

    public BlockingQueue<ReplicationMsgEvent> getReplicationMsgQueue() {
        return replicationMsgQueue;
    }

    public void put(ReplicationMsgEvent replicationMsgEvent) {
        ReplicationModeEnum replicationModeEnum = ReplicationModeEnum.of(CommonCache.getNameserverProperties().getReplicationMode());
        if (replicationModeEnum == null) {
            //单机架构，不做复制处理
            return;
        }
        if (replicationModeEnum == ReplicationModeEnum.MASTER_SLAVE) {
            ReplicationRoleEnum roleEnum = ReplicationRoleEnum.of(CommonCache.getNameserverProperties().getMasterSlaveReplicationProperties().getRole());
            if (roleEnum != ReplicationRoleEnum.MASTER) {
                return;
            }
            this.sendMsgToQueue(replicationMsgEvent);
        } else if (replicationModeEnum == ReplicationModeEnum.TRACE) {
            TraceReplicationProperties traceReplicationProperties = CommonCache.getNameserverProperties().getTraceReplicationProperties();
            if (traceReplicationProperties.getNextNode() != null) {
                this.sendMsgToQueue(replicationMsgEvent);
            }
        }


    }

    private void sendMsgToQueue(ReplicationMsgEvent replicationMsgEvent) {
        try {
            replicationMsgQueue.put(replicationMsgEvent);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
