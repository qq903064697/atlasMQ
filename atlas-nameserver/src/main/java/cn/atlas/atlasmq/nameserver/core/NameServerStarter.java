package cn.atlas.atlasmq.nameserver.core;

import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import cn.atlas.atlasmq.nameserver.event.EventBus;
import cn.atlas.atlasmq.nameserver.handler.TcpNettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 基于netty启动nameserver服务
 */
public class NameServerStarter {
    private int port;

    public NameServerStarter(int port) {
        this.port = port;
    }

    public void startServer() throws InterruptedException {
        // 构建netty服务
        // 注入编解码器
        // 注入特定的handler
        // 启动netty服务


        // 处理网络io中的accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理网络io中的read/write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new TcpNettyServerHandler(new EventBus("broker-connection-")));
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("nameserver is closed");
        }));

        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        System.out.println("start nameserver application on port: " + port);
        // 阻塞代码
        channelFuture.channel().closeFuture().sync();
    }
}
