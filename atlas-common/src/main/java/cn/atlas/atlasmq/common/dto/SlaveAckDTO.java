package cn.atlas.atlasmq.common.dto;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Decription:
 */
public class SlaveAckDTO {
    /**
     * 需要接受多少个ack信号
     */
    private AtomicInteger needAckTime;

    public SlaveAckDTO(AtomicInteger needAckTime, ChannelHandlerContext brokerChannel) {
        this.needAckTime = needAckTime;
        this.brokerChannel = brokerChannel;
    }

    /**
     * broker连接主节点的channel
     */

    private ChannelHandlerContext brokerChannel;

    public ChannelHandlerContext getBrokerChannel() {
        return brokerChannel;
    }

    public void setBrokerChannel(ChannelHandlerContext brokerChannel) {
        this.brokerChannel = brokerChannel;
    }

    public AtomicInteger getNeedAckTime() {
        return needAckTime;
    }

    public void setNeedAckTime(AtomicInteger needAckTime) {
        this.needAckTime = needAckTime;
    }
}
