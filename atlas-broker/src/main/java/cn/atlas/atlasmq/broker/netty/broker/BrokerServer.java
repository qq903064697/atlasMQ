package cn.atlas.atlasmq.broker.netty.broker;

import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import cn.atlas.atlasmq.common.constants.TcpConstants;
import cn.atlas.atlasmq.common.event.EventBus;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class BrokerServer {
    private static final Logger logger = LoggerFactory.getLogger(BrokerServer.class);

    private int port;

    public BrokerServer(int port) {
        this.port = port;
    }

    /**
     * 启动服务端程序
     *
     * @throws InterruptedException
     */
    public void startServer() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ByteBuf delimiter = Unpooled.copiedBuffer(TcpConstants.DEFAULT_DECODE_CHAR.getBytes());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024 * 8, delimiter));
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(new BrokerServerHandler(new EventBus("broker-server-handle")));
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("broker is closed");
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        logger.info("start nameserver application on port:{}", port);
        //阻塞代码
        channelFuture.channel().closeFuture().sync();
    }
}
