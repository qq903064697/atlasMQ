package cn.atlas.atlasmq.common.dto;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author xiaoxin
 * @Description 链式复制中的ack对象
 */
public class NodeAckDTO {

    private ChannelHandlerContext channelHandlerContext;

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
