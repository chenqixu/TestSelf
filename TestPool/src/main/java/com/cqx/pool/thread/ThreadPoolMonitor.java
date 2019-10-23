package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池监控
 *
 * @author chenqixu
 */
public class ThreadPoolMonitor {

    private static Logger logger = LoggerFactory.getLogger(ThreadPoolMonitor.class);
    private ThreadPoolExecutor threadPoolExecutor;
    private HeartUtil heartUtil;

    public ThreadPoolMonitor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = (ThreadPoolExecutor) threadPoolExecutor;
    }

    public ThreadPoolMonitor(HeartUtil heartUtil) {
        this.heartUtil = heartUtil;
    }

    /**
     * 启动心跳监控
     */
    public void startHeartMonitor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    heartUtil.printStatus();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

    /**
     * 启动线程池监控
     */
    public void startPoolMonitor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    printPoolStatus();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

    /**
     * 打印线程池状态
     */
    private void printPoolStatus() {
        logger.info("ActiveCount：{}，CorePoolSize：{}，PoolSize：{}，Queue.size：{}，TaskCount：{}，CompletedTaskCount：{}，KeepAliveTime：{}",
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getCorePoolSize(),
                threadPoolExecutor.getPoolSize(),
                threadPoolExecutor.getQueue().size(),
                threadPoolExecutor.getTaskCount(),
                threadPoolExecutor.getCompletedTaskCount(),
                threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS));
    }
}
