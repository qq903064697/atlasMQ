package cn.atlas.atlasmq.common.event.model;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午2:22
 * @Version 1.0
 */
public abstract class Event {
    private long timeStamp;

    private String msgId;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    private ChannelHandlerContext channelHandlerContext;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
