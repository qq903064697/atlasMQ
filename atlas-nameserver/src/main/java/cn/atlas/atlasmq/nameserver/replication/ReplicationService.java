package cn.atlas.atlasmq.nameserver.replication;

import cn.atlas.atlasmq.nameserver.enums.ReplicationRoleEnum;
import cn.atlas.atlasmq.nameserver.handler.SlaveReplicationServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;
import cn.atlas.atlasmq.common.coder.TcpMsgDecoder;
import cn.atlas.atlasmq.common.coder.TcpMsgEncoder;
import cn.atlas.atlasmq.common.utils.AssertUtils;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.MasterSlaveReplicationProperties;
import cn.atlas.atlasmq.nameserver.common.NameserverProperties;
import cn.atlas.atlasmq.nameserver.enums.ReplicationModeEnum;
import cn.atlas.atlasmq.nameserver.event.EventBus;
import cn.atlas.atlasmq.nameserver.handler.MasterReplicationServerHandler;

/**
 * @Author xiaoxin
 * @Description 集群复制服务
 */
public class ReplicationService {

    //参数的校验
    public ReplicationModeEnum checkProperties() {
        NameserverProperties nameserverProperties = CommonCache.getNameserverProperties();
        String mode = nameserverProperties.getReplicationMode();
        if (StringUtil.isNullOrEmpty(mode)) {
            System.out.println("执行单机模式");
            return null;
        }
        //为空，参数不合法，抛异常
        ReplicationModeEnum replicationModeEnum = ReplicationModeEnum.of(mode);
        AssertUtils.isNotNull(replicationModeEnum, "复制模式参数异常");
        if (replicationModeEnum == ReplicationModeEnum.TRACE) {
            //链路复制
        } else {
            //主从复制
            MasterSlaveReplicationProperties masterSlaveReplicationProperties = nameserverProperties.getMasterSlaveReplicationProperties();
            AssertUtils.isNotBlank(masterSlaveReplicationProperties.getMaster(), "master参数不能为空");
            AssertUtils.isNotBlank(masterSlaveReplicationProperties.getRole(), "role参数不能为空");
            AssertUtils.isNotBlank(masterSlaveReplicationProperties.getType(), "type参数不能为空");
            AssertUtils.isNotNull(masterSlaveReplicationProperties.getPort(), "同步端口不能为空");
        }
        return replicationModeEnum;
    }

    //根据参数判断复制的方式 开启一个netty进程 用于做复制操作
    public void startReplicationTask(ReplicationModeEnum replicationModeEnum) {
        //单机版本，不用做处理
        if (replicationModeEnum == null) {
            return;
        }
        int port = 0;
        NameserverProperties nameserverProperties = CommonCache.getNameserverProperties();
        if (replicationModeEnum == ReplicationModeEnum.MASTER_SLAVE) {
            port = nameserverProperties.getMasterSlaveReplicationProperties().getPort();
        }
        ReplicationRoleEnum roleEnum = ReplicationRoleEnum.of(nameserverProperties.getMasterSlaveReplicationProperties().getRole());
        int replicationPort = port;

        Thread replicationTask = new Thread(() -> {
            //master角色，开启netty进程同步数据给master
            if (roleEnum == ReplicationRoleEnum.MASTER) {
                startMasterServer(new MasterReplicationServerHandler(new EventBus("master-replication-task-")), replicationPort);
            } else if (roleEnum == ReplicationRoleEnum.SLAVE) {
                //slave角色，主动连接master角色
                String masterAddress = nameserverProperties.getMasterSlaveReplicationProperties().getMaster();
                startMasterConn(new SlaveReplicationServerHandler(new EventBus("slave-replication-task-")), masterAddress);
            }
        });

        replicationTask.setName("replication-task");
        replicationTask.start();
    }

    /**
     * 开启对目标进程的链接
     *
     * @param simpleChannelInboundHandler
     * @param address
     */
    private void startMasterConn(SimpleChannelInboundHandler simpleChannelInboundHandler, String address) {
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        Channel channel;
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(simpleChannelInboundHandler);
            }
        });
        ChannelFuture channelFuture = null;
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                clientGroup.shutdownGracefully();
                System.out.println("nameserver's replication connect application is closed");
            }));
            String[] addr = address.split(":");
            channelFuture = bootstrap.connect(addr[0], Integer.parseInt(addr[1])).sync();
            //连接了master节点的channel对象，建议保存
            channel = channelFuture.channel();
            System.out.println("success connected to nameserver replication!");
            CommonCache.setMasterConnection(channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启一个netty的进程
     *
     * @param simpleChannelInboundHandler
     * @param port
     */
    private void startMasterServer(SimpleChannelInboundHandler simpleChannelInboundHandler, int port) {
        //负责netty启动
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理网络io中的read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new TcpMsgDecoder());
                ch.pipeline().addLast(new TcpMsgEncoder());
                ch.pipeline().addLast(simpleChannelInboundHandler);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("nameserver's replication application is closed");
        }));
        ChannelFuture channelFuture = null;
        try {
            //master-slave架构
            //写入数据的节点，这里就会开启一个服务
            //非写入数据的节点，这里就需要链接一个服务
            //trace架构
            //又要接收外界数据，又要复制数据给外界
            channelFuture = bootstrap.bind(port).sync();
            System.out.println("start nameserver's replication application on port:" + port);
            //阻塞代码
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
