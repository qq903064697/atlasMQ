package cn.atlas.atlasmq.nameserver.test;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @description nameserver通道
 */
@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        System.out.println("resp:" + JSON.toJSONString(tcpMsg));
    }
}
