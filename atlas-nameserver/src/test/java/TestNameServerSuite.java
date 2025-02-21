import cn.atlas.atlasmq.nameserver.test.NameServerRespChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import cn.atlas.atlasmq.common.constants.NameServerConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author
 * @Description
 */
public class TestNameServerSuite {

    private EventLoopGroup clientGroup = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private String DEFAULT_NAMESERVER_IP = "127.0.0.1";

    @Before
    public void setUp() {
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
        try {
            channelFuture = bootstrap.connect(DEFAULT_NAMESERVER_IP, NameServerConstants.DEFAULT_NAMESERVER_PORT).sync();
            channel = channelFuture.channel();
            System.out.println("success connected to nameserver!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSendMsg() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println("isActive:" + channel.isActive());
                TimeUnit.SECONDS.sleep(1);
                String msgBody = "this is client test string";
                TcpMsg tcpMsg = new TcpMsg(1, msgBody.getBytes());
                channel.writeAndFlush(tcpMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
