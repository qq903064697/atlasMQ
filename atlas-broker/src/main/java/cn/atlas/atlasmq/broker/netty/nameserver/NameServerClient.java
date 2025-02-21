package cn.atlas.atlasmq.broker.netty.nameserver;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.config.GlobalProperties;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import cn.atlas.atlasmq.common.constants.NameServerConstants;
import cn.atlas.atlasmq.common.dto.RegistryDTO;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 负责与nameserver服务端创建长连接，支持连接创建，支持重试机制
 */
public class NameServerClient {
    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String DEFAULT_NAMESERVER_IP = "127.0.0.1";

    /**
     * 初始化连接
     */
    public void initConnection() {
        String nameserverIp = CommonCache.getGlobalProperties().getNameserverIp();
        Integer nameserverPort = CommonCache.getGlobalProperties().getNameserverPort();
        if (StringUtil.isNullOrEmpty(nameserverIp) || nameserverPort == null || nameserverPort < 0) {
            throw new RuntimeException("error ip or port" + nameserverIp + ":" + nameserverPort);
        }
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new NameServerRespChannelHandler());
            }
        });
        ChannelFuture channelFuture = null;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TcpMsg tcpMsg = new TcpMsg(NameServerResponseCode.UN_REGISTRY_SERVICE.getCode(), new byte[]{});
            channel.writeAndFlush(tcpMsg);
            clientGroup.shutdownGracefully();
            System.out.println("nameserver client is closed");
        }));
        try {
            channelFuture = bootstrap.connect(
                    nameserverIp,
                    nameserverPort
            ).sync();
            channel = channelFuture.channel();
            System.out.println("success connected to nameserver!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取channel
     */
    public Channel getChannel() {
        if (channel == null) {
            throw new RuntimeException("channel has not been connected!");
        }
        return channel;
    }

    public void sendRegistryMsg() {
        RegistryDTO registryDTO = new RegistryDTO();
        try {
            registryDTO.setBrokerIp(Inet4Address.getLocalHost().getHostAddress());
            GlobalProperties globalProperties = CommonCache.getGlobalProperties();
            registryDTO.setBrokerPort(globalProperties.getBrokerPort());
            registryDTO.setUser(globalProperties.getNameserverUser());
            registryDTO.setPassword(globalProperties.getNameserverPassword());
            byte[] body = JSON.toJSONBytes(registryDTO);
            TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.REGISTRY.getCode(), body);
            channel.writeAndFlush(tcpMsg);
            System.out.println("发送注册事件");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
}
