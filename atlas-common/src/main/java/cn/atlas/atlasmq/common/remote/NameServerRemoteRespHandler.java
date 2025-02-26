package cn.atlas.atlasmq.common.remote;

import cn.atlas.atlasmq.common.dto.HeartBeatDTO;
import cn.atlas.atlasmq.common.dto.PullBrokerIpRespDTO;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cn.atlas.atlasmq.common.cache.NameServerSyncFutureManager;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.ServiceRegistryRespDTO;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;

/**
 * @Author xiaoxin
 * @Description 处理对nameserver给客户端返回的数据内容
 */
@ChannelHandler.Sharable
public class NameServerRemoteRespHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        TcpMsg tcpMsg = (TcpMsg) msg;
        int code = tcpMsg.getCode();
        byte[] body = tcpMsg.getBody();
        if (NameServerResponseCode.REGISTRY_SUCCESS.getCode() == code) {
            //msgId
            ServiceRegistryRespDTO serviceRegistryRespDTO = JSON.parseObject(tcpMsg.getBody(), ServiceRegistryRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(serviceRegistryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.ERROR_USER_OR_PASSWORD.getCode() == code) {
            ServiceRegistryRespDTO serviceRegistryRespDTO = JSON.parseObject(tcpMsg.getBody(), ServiceRegistryRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(serviceRegistryRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.HEART_BEAT_SUCCESS.getCode() == code) {
            HeartBeatDTO heartBeatDTO = JSON.parseObject(tcpMsg.getBody(), HeartBeatDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(heartBeatDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        } else if (NameServerResponseCode.PULL_BROKER_ADDRESS_SUCCESS.getCode() == code) {
            PullBrokerIpRespDTO pullBrokerIpRespDTO = JSON.parseObject(tcpMsg.getBody(), PullBrokerIpRespDTO.class);
            SyncFuture syncFuture = NameServerSyncFutureManager.get(pullBrokerIpRespDTO.getMsgId());
            if (syncFuture != null) {
                syncFuture.setResponse(tcpMsg);
            }
        }
    }
}
