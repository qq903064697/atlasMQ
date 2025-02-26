package cn.atlas.atlasmq.common.remote;

import cn.atlas.atlasmq.common.cache.BrokerServerSyncFutureManager;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * @Author xiaoxin
 * @Description 对Broker进行远程连接的客户端
 */
public class BrokerNettyRemoteClient {

    private String ip;
    private Integer port;

    public BrokerNettyRemoteClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;

    /**
     * 远程连接的初始化
     */
    public void buildConnection() {
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new BrokerRemoteRespHandler());
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(ip, port).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        throw new RuntimeException("connecting nameserver has error!");
                    }
                }
            });
            //初始化建立长链接
            channel = channelFuture.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public TcpMsg sendSyncMsg(TcpMsg tcpMsg, String msgId) {
        //future设计机制 jdk8里面有很多种，future，futuretask，CompletableFuture
        channel.writeAndFlush(tcpMsg);
        SyncFuture syncFuture = new SyncFuture();
        syncFuture.setMsgId(msgId);
        BrokerServerSyncFutureManager.put(msgId,syncFuture);
        try {
            return (TcpMsg) syncFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void sendAsyncMsg(TcpMsg tcpMsg) {
        channel.writeAndFlush(tcpMsg);
    }
}
