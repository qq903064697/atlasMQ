package cn.atlas.atlasmq.nameserver.event.spi.listener;


import cn.atlas.atlasmq.nameserver.event.model.SlaveHeartBeatEvent;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class SlaveHeartBeatEventListener implements Listener<SlaveHeartBeatEvent> {

    @Override
    public void onReceive(SlaveHeartBeatEvent event) throws Exception {
        System.out.println("接收到从节点心跳信号");
    }
}
