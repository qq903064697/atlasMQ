package cn.atlas.atlasmq.nameserver;

import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.TraceReplicationProperties;
import cn.atlas.atlasmq.nameserver.core.InValidServiceRemoveTask;
import cn.atlas.atlasmq.nameserver.core.NameServerStarter;
import cn.atlas.atlasmq.nameserver.enums.ReplicationModeEnum;
import cn.atlas.atlasmq.nameserver.enums.ReplicationRoleEnum;
import cn.atlas.atlasmq.nameserver.replication.*;
import io.netty.util.internal.StringUtil;

import java.io.IOException;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class NameServerStartUp {
    private static NameServerStarter nameServerStarter;
    private static ReplicationService replicationService = new ReplicationService();

    private static void initReplication() {
        // 复制逻辑的初始化
        ReplicationModeEnum replicationModeEnum = replicationService.checkProperties();
        // 这里面会根据同步模式开启不同的netty进程
        replicationService.startReplicationTask(replicationModeEnum);
        //开启定时任务
        ReplicationTask replicationTask = null;
        if(replicationModeEnum == ReplicationModeEnum.MASTER_SLAVE) {
            ReplicationRoleEnum roleEnum = ReplicationRoleEnum.of(CommonCache.getNameserverProperties().getMasterSlaveReplicationProperties().getRole());
            if(roleEnum == ReplicationRoleEnum.MASTER) {
                replicationTask = new MasterReplicationMsgSendTask("master-replication-msg-send-task");
                replicationTask.startTaskAsync();
            } else if (roleEnum == ReplicationRoleEnum.SLAVE) {
                //发送链接主节点的请求
                //开启心跳任务，发送给主节点
                replicationTask = new SlaveReplicationHeartBeatTask("slave-replication-heart-beat-send-task");
                replicationTask.startTaskAsync();
            }
        } else if (replicationModeEnum == ReplicationModeEnum.TRACE) {
            // 判断当前节点是否为尾节点，如果不是就开启一个复制数据的一步任务
            TraceReplicationProperties traceReplicationProperties = CommonCache.getNameserverProperties().getTraceReplicationProperties();
            if (!StringUtil.isNullOrEmpty(traceReplicationProperties.getNextNode())) {
                replicationTask = new NodeReplicationSendMsgTask("node-replication-msg-send-task");
                replicationTask.startTaskAsync();
            }
        }
        CommonCache.setReplicationTask(replicationTask);
    }

    private static void initInvalidServerRemoveTask() {
        Thread inValidServiceRemoveTask = new Thread(new InValidServiceRemoveTask());
        inValidServiceRemoveTask.setName("invalid-server-remove-task");
        inValidServiceRemoveTask.start();
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        CommonCache.getPropertiesLoader().loadProperties();
        // 获取到了集群复制的配置属性
        // 如果是主从复制-》master角色-》开启一个额外的netty进程-》slave链接接入-》当数据写入master的时候，把写入的数据同步给到slave节点
        // 如果是主从复制-》slave角色-》开启一个额外的netty进程-》slave端去链接master节点
        initReplication();
        initInvalidServerRemoveTask();
        nameServerStarter = new NameServerStarter(CommonCache.getNameserverProperties().getNameserverPort());
        // 阻塞
        nameServerStarter.startServer();
    }
}
