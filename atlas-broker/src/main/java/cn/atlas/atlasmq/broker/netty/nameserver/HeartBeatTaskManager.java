package cn.atlas.atlasmq.broker.netty.nameserver;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.HeartBeatDTO;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 心跳数据上报任务
 */
public class HeartBeatTaskManager {
    private AtomicInteger flag = new AtomicInteger(0);

    //开启心跳传输任务
    public void startTask() {
        if (flag.getAndIncrement() >= 1) {
            return;
        }
        Thread heartBeatRequestTask = new Thread(new HeartBeatRequestTask());
        heartBeatRequestTask.setName("heart-beat-request-task");
        heartBeatRequestTask.start();
    }


    private class HeartBeatRequestTask implements Runnable{
        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    //心跳包不需要额外透传过多的参数，只需要告诉nameserver这个channel依然存活即可
                    Channel channel = CommonCache.getNameServerClient().getChannel();
                    HeartBeatDTO heartBeatDTO = new HeartBeatDTO();
                    heartBeatDTO.setMsgId(UUID.randomUUID().toString());
                    TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.HEART_BEAT.getCode(), JSON.toJSONBytes(heartBeatDTO));
                    channel.writeAndFlush(tcpMsg);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
