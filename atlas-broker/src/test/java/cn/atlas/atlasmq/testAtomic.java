package cn.atlas.atlasmq;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午11:21
 * @Version 1.0
 */
public class testAtomic {
    @Test
    public void test(){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        System.out.println(atomicInteger.getAndIncrement());
    }
}
