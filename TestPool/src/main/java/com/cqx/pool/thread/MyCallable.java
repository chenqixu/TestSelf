package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * callable测试TimeoutException
 *
 * @author chenqixu
 * @date 2018/11/29 15:26
 */
public class MyCallable implements Callable<Boolean> {

    private static Logger logger = LoggerFactory.getLogger(MyCallable.class);
    private long timeoutMs;
    long start = System.currentTimeMillis();

    public MyCallable(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Boolean call() throws Exception {
        // do
        logger.info("do");
        Thread.sleep(5000);
        // 防止外部已经取消了这个线程，但线程依然连接上了。导致连接对象泄漏。
        long currentTimeMillis = System.currentTimeMillis();
        logger.info("currentTimeMillis：{} - start：{} = {}", currentTimeMillis, start, currentTimeMillis - start);
        if (currentTimeMillis - start - 1000 > this.timeoutMs) {
            logger.warn("timeout");
            throw new RuntimeException("连接超时,获取连接等待已经结束");
        }
        return true;
    }
}
