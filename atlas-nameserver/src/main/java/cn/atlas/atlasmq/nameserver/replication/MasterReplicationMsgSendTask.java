package cn.atlas.atlasmq.nameserver.replication;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.SlaveAckDTO;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.MasterSlaveReplicationProperties;
import cn.atlas.atlasmq.nameserver.enums.MasterSlaveReplicationTypeEnum;
import cn.atlas.atlasmq.nameserver.event.model.ReplicationMsgEvent;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 主从同步专用的数据发送任务
 */
public class MasterReplicationMsgSendTask extends ReplicationTask {


    public MasterReplicationMsgSendTask(String taskName) {
        super(taskName);
    }

    @Override
    public void startTask() {
        MasterSlaveReplicationProperties masterSlaveReplicationProperties = CommonCache.getNameserverProperties().getMasterSlaveReplicationProperties();
        MasterSlaveReplicationTypeEnum replicationTypeEnum = MasterSlaveReplicationTypeEnum.of(masterSlaveReplicationProperties.getType());
        // 判断当前的复制模式
        // 如果是异步复制，直接发送同步数据，同时返回注册成功信号给到broker节点
        // 如果是同步复制，发送同步数据给到slave节点，slave节点返回ack信号，主节点收到ack信号后通知给broker注册成功
        // 半同步复制其实和同步复制思路很相似

        while (true) {
            try {
                ReplicationMsgEvent replicationMsgEvent = CommonCache.getReplicationMsgQueueManager().getReplicationMsgQueue().take();
                replicationMsgEvent.setMsgId(UUID.randomUUID().toString());
                Channel brokerChannel = replicationMsgEvent.getChannelHandlerContext().channel();
                //判断当前采用的同步模式是哪种方式：同步、异步、半同步
                Map<String, ChannelHandlerContext> channelHandlerContextMap = CommonCache.getReplicationChannelManager().getValidSlaveChannelMap();
                int validSlaveChannelCount = channelHandlerContextMap.keySet().size();
                if (replicationTypeEnum == MasterSlaveReplicationTypeEnum.ASYNC) {
                    this.sendMsgToSlave(replicationMsgEvent);
                    brokerChannel.writeAndFlush(new TcpMsg(NameServerResponseCode.REGISTRY_SUCCESS.getCode(), NameServerResponseCode.REGISTRY_SUCCESS.getDesc().getBytes()));
                } else if (replicationTypeEnum == MasterSlaveReplicationTypeEnum.SYNC) {
                    // 需要接受多少个ack的次数
                    this.inputMsgToAckMap(replicationMsgEvent, validSlaveChannelCount);
                    this.sendMsgToSlave(replicationMsgEvent);
                } else if (replicationTypeEnum == MasterSlaveReplicationTypeEnum.HALF_SYNC) {
                    this.inputMsgToAckMap(replicationMsgEvent, validSlaveChannelCount / 2);
                    this.sendMsgToSlave(replicationMsgEvent);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 将主节点需要发送出去的数据注入到一个map中，然后当从节点返回ack的时候，该map的数据会被剔除对应记录
     * @param replicationMsgEvent
     * @param needAckCount
     */
    private void inputMsgToAckMap(ReplicationMsgEvent replicationMsgEvent, int needAckCount) {
        CommonCache.getAckMap().put(replicationMsgEvent.getMsgId(), new SlaveAckDTO(new AtomicInteger(needAckCount), replicationMsgEvent.getChannelHandlerContext()));
    }

    /**
     * 发送数据给到从节点
     *
     * @param replicationMsgEvent
     */
    private void sendMsgToSlave(ReplicationMsgEvent replicationMsgEvent) {
        Map<String, ChannelHandlerContext> channelHandlerContextMap = CommonCache.getReplicationChannelManager().getValidSlaveChannelMap();
        for (String reqId : channelHandlerContextMap.keySet()) {
            replicationMsgEvent.setChannelHandlerContext(null);
            byte[] body = JSON.toJSONBytes(replicationMsgEvent);
            channelHandlerContextMap.get(reqId).writeAndFlush(new TcpMsg(NameServerEventCode.MASTER_REPLICATION_MSG.getCode(), body));
        }

    }
}
