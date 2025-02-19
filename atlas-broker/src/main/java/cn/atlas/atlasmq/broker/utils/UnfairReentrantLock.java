package cn.atlas.atlasmq.broker.utils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午11:17
 * @Version 1.0
 */
public class UnfairReentrantLock implements PutMessageLock {
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void lock() {
        reentrantLock.lock();
    }

    @Override
    public void unlock() {
        reentrantLock.unlock();
    }
}
