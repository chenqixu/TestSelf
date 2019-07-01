package com.newland.bi.bigdata.queue;

import com.newland.bi.bigdata.bean.KafkaTuple;
import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaTupleBlockingQueueTest {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTupleBlockingQueueTest.class);
    private KafkaTupleBlockingQueue kafkaTupleBlockingQueue;

    @Before
    public void setUp() throws Exception {
        kafkaTupleBlockingQueue = new KafkaTupleBlockingQueue();
    }

    @After
    public void tearDown() throws Exception {
        kafkaTupleBlockingQueue.stop();
    }

    @Test
    public void exec() {
        put();
        poll();
        getDiscardcount();
        getQueueSize();
        SleepUtils.sleepSecond(5);
    }

    private void put() {
        // 生产数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        kafkaTupleBlockingQueue.put(new KafkaTuple());
                        SleepUtils.sleepMilliSecond(50);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

    private void poll() {
        // 消费数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    kafkaTupleBlockingQueue.poll();
                    SleepUtils.sleepMilliSecond(100);
                }
            }
        }).start();
    }

    private void getDiscardcount() {
        // 实时打印抛弃个数
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    logger.info("DiscardCount：{}", kafkaTupleBlockingQueue.getDiscardCount());
                    SleepUtils.sleepMilliSecond(100);
                }
            }
        }).start();
    }

    private void getQueueSize() {
        // 实时打印当前队列个数
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    logger.info("QueueSize：{}", kafkaTupleBlockingQueue.getQueueSize());
                    SleepUtils.sleepMilliSecond(100);
                }
            }
        }).start();
    }
}