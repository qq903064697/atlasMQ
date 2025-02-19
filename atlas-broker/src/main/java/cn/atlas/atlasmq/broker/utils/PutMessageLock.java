package cn.atlas.atlasmq.broker.utils;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public interface PutMessageLock {
    /**
     * 加锁
     */
    void lock();
    /**
     * 解锁
     */
    void unlock();
}
