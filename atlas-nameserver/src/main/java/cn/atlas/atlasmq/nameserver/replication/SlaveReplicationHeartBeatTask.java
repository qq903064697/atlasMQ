package cn.atlas.atlasmq.nameserver.replication;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.StartReplicationEvent;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.event.model.SlaveHeartBeatEvent;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 10:46 2024/5/19
 * @Description 从节点给主节点发送心跳数据 定时任务
 */
public class SlaveReplicationHeartBeatTask extends ReplicationTask {


    public SlaveReplicationHeartBeatTask(String taskName) {
        super(taskName);
    }

    @Override
    public void startTask() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StartReplicationEvent startReplicationEvent = new StartReplicationEvent();
        startReplicationEvent.setUser(CommonCache.getNameserverProperties().getNameserverUser());
        startReplicationEvent.setPassword(CommonCache.getNameserverProperties().getNameserverPwd());
        TcpMsg startReplicationMsg = new TcpMsg(NameServerEventCode.START_REPLICATION.getCode(), JSON.toJSONBytes(startReplicationEvent));
        CommonCache.getMasterConnection().writeAndFlush(startReplicationMsg);

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
                //发送数据给到主节点
                Channel channel = CommonCache.getMasterConnection();
                TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.SLAVE_HEART_BEAT.getCode(), JSON.toJSONBytes(new SlaveHeartBeatEvent()));
                channel.writeAndFlush(tcpMsg);
                System.out.println("从节点发送心跳数据给master");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
