package cn.atlas.atlasmq.common.event;


import cn.atlas.atlasmq.common.event.model.Event;

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
