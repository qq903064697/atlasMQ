package cn.atlas.atlasmq.broker.netty.nameserver;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @description nameserver通道
 */
@ChannelHandler.Sharable
public class NameServerRespChannelHandler extends SimpleChannelInboundHandler {
    private final Logger logger = LoggerFactory.getLogger(NameServerRespChannelHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        if (NameServerResponseCode.REGISTRY_SUCCESS.getCode() == tcpMsg.getCode()) {
            // 注册成功
            // 开启一个定时任务，上报心跳数据给nameserver
            logger.info("注册成功，开启心跳任务");
            CommonCache.getHeartBeatTaskManager().startTask();
        } else if (NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode() == tcpMsg.getCode()) {
            // 验证失败，抛出异常
            throw new IllegalAccessException("error account to connected!");
        } else if (NameServerResponseCode.HEART_BEAT_SUCCESS.getCode() == tcpMsg.getCode()) {
            logger.info("收到nameserver心跳回应ack");
        }
    }
}
