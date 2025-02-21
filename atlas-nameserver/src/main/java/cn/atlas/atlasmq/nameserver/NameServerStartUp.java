package cn.atlas.atlasmq.nameserver;

import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.core.InValidServiceRemoveTask;
import cn.atlas.atlasmq.nameserver.core.NameServerStarter;
import cn.atlas.atlasmq.nameserver.enums.ReplicationModeEnum;
import cn.atlas.atlasmq.nameserver.enums.ReplicationRoleEnum;
import cn.atlas.atlasmq.nameserver.replication.MasterReplicationMsgSendTask;
import cn.atlas.atlasmq.nameserver.replication.ReplicationService;
import cn.atlas.atlasmq.nameserver.replication.ReplicationTask;
import cn.atlas.atlasmq.nameserver.replication.SlaveReplicationHeartBeatTask;

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
        // 这里面会根据角色开启不同的netty进程
        replicationService.startReplicationTask(replicationModeEnum);
        //开启定时任务
        if(replicationModeEnum == ReplicationModeEnum.MASTER_SLAVE) {
            ReplicationRoleEnum roleEnum = ReplicationRoleEnum.of(CommonCache.getNameserverProperties().getMasterSlaveReplicationProperties().getRole());
            ReplicationTask replicationTask = null;
            if(roleEnum == ReplicationRoleEnum.MASTER) {
                replicationTask = new MasterReplicationMsgSendTask("master-replication-msg-send-task");
                replicationTask.startTaskAsync();
            } else if (roleEnum == ReplicationRoleEnum.SLAVE) {
                //发送链接主节点的请求
                //开启心跳任务，发送给主节点
                replicationTask = new SlaveReplicationHeartBeatTask("slave-replication-heart-beat-send-task");
                replicationTask.startTaskAsync();
            }
            CommonCache.setReplicationTask(replicationTask);
        }
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
        new Thread(new InValidServiceRemoveTask()).start();
        nameServerStarter = new NameServerStarter(9090);
        // 阻塞
        nameServerStarter.startServer();
    }
}
