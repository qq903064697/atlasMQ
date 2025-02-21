package cn.atlas.atlasmq.nameserver.event.spi.listener;


import cn.atlas.atlasmq.nameserver.event.model.Event;

/**
 * @Author xiaoxin
 * @Description
 */
public interface Listener<E extends Event> {

    /**
     * 回调通知
     *
     * @param event
     */
    void onReceive(E event) throws Exception;
}
