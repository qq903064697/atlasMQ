package cn.atlas.atlasmq.broker.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 自旋间隙锁
 */
public class SpinLock implements PutMessageLock{
    AtomicInteger atomicInteger = new AtomicInteger(0);
    @Override
    public void lock() {
        do {
            int result = atomicInteger.getAndIncrement();
            if (result == 1) {
                return;
            }
        } while (true);


    }

    @Override
    public void unlock() {
        atomicInteger.decrementAndGet();
    }
}
