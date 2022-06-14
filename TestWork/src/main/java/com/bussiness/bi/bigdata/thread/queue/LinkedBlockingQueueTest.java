package com.bussiness.bi.bigdata.thread.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * LinkedBlockingQueueTest
 *
 * @author chenqixu
 */
public class LinkedBlockingQueueTest {
    private static final Logger logger = LoggerFactory.getLogger(LinkedBlockingQueueTest.class);
    private LinkedBlockingQueue<String> exeQueue = new LinkedBlockingQueue<>(10000);

    public static void main(String[] args) {
        new LinkedBlockingQueueTest().case1();
    }

    /**
     * 队列满了就等于抛掉了，写不进去了
     */
    public void case1() {
        for (int i = 0; i < 10005; i++) {
            try {
                exeQueue.offer(i + "", 1, TimeUnit.SECONDS);
                logger.info("{} {}", i, exeQueue.size());
            } catch (InterruptedException e) {
                logger.error("add", e);
            }
        }
    }
}
