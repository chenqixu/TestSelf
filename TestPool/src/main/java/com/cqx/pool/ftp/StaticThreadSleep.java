package com.cqx.pool.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author chenqixu
 * @date 2018/11/29 16:30
 */
public class StaticThreadSleep {

    private static Logger logger = LoggerFactory.getLogger(StaticThreadSleep.class);
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void sleep(long times) throws Exception {
        if (atomicInteger.incrementAndGet() <= 1) {
            logger.info("StaticThreadSleep.sleep：{}", times);
            Thread.sleep(times);
        }
    }
}
