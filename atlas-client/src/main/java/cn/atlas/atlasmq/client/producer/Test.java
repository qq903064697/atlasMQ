package cn.atlas.atlasmq.client.producer;

/**
 * @Author xiaoxin
 * @Version 1.0
 *
 */
public class Test {

    //异步调用：server-》connection，channel-》writeAndFlush，server's response -》client，handler
    //链路存在先后依赖顺序的话，那么依赖的环节越多，调用的链路就越复杂，
    /*
    producer -》nameserver建立长链接，给nameserver发送registry信号包（密码校验），sendheartbeat（发送心跳数据包）-》拉broker的地址
    -》跟broker建立长链接通道，发送数据给broker
    */

    //封装一个基于netty的支持异步转同步的组件，client端使用（producer，consumer，broker）

    public static void main(String[] args) {
        DefaultProducerImpl defaultProducer = new DefaultProducerImpl();
        defaultProducer.setNsIp("127.0.0.1");
        defaultProducer.setNsPort(9093);
        defaultProducer.setNsPwd("altas_mq");
        defaultProducer.setNsUser("altas_mq");
        defaultProducer.start();
    }


}
