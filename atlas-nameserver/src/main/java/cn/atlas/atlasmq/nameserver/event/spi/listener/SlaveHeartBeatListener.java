package cn.atlas.atlasmq.nameserver.event.spi.listener;


import cn.atlas.atlasmq.common.event.Listener;
import cn.atlas.atlasmq.nameserver.event.model.SlaveHeartBeatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class SlaveHeartBeatListener implements Listener<SlaveHeartBeatEvent> {
    private final Logger logger = LoggerFactory.getLogger(SlaveHeartBeatListener.class);

    @Override
    public void onReceive(SlaveHeartBeatEvent event) throws Exception {
        logger.info("接收到从节点心跳信号");
    }
}
