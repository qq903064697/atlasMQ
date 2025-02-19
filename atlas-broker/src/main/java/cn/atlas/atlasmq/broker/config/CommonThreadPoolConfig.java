package cn.atlas.atlasmq.broker.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 通用的线程池配置
 */
public class CommonThreadPoolConfig {

    public static ThreadPoolExecutor refreshAtlasMqTopicExecutor = new ThreadPoolExecutor(
            1,
            1,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10), r -> {
                Thread thread = new Thread(r);
                thread.setName("refresh-atlas-mq-topic-config");
                return thread;
            }
    );
}
