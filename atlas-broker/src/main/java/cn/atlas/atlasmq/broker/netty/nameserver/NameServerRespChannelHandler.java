package cn.atlas.atlasmq.broker.netty.nameserver;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
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
        if (NameServerResponseCode.REGISTRY_SUCCESS.getCode() == tcpMsg.getCode()) {
            // 注册成功
            // 开启一个定时任务，上报心跳数据给nameserver
            CommonCache.getHeartBeatTaskManager().startTask();
        } else if (NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode() == tcpMsg.getCode()) {
            // 验证失败，抛出异常
            throw new IllegalAccessException("error account to connected!");
        }
        System.out.println("resp:" + JSON.toJSONString(tcpMsg));
    }
}
