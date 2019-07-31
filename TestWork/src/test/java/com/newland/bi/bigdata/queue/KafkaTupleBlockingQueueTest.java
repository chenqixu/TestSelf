package com.newland.bi.bigdata.queue;

import com.newland.bi.bigdata.bean.KafkaTuple;
import com.newland.bi.bigdata.time.TimeCostUtil;
import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        SleepUtils.sleepSecond(15);
    }

    /**
     * 测试生产27000条数据要多少时间
     */
    @Test
    public void putTest() throws InterruptedException {
        BlockingQueue<KafkaTuple> kafkaTupleBlockingQueue = new LinkedBlockingQueue<>();
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        for (int i = 0; i < 27000; i++) {
            kafkaTupleBlockingQueue.put(new KafkaTuple());
        }
        long cost = timeCostUtil.stopAndGet();
        logger.info("cost：{}", cost);
    }

    private void put() {
        // 生产数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 300;
                while (cnt > 0) {
                    logger.info("生产文件：{}", cnt);
                    putDataToQueue();
                    SleepUtils.sleepMilliSecond(100);
                    cnt--;
                }
            }
        }).start();
    }

    private void putDataToQueue() {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        for (int i = 0; i < 27000; i++) {
            try {
                kafkaTupleBlockingQueue.put(new KafkaTuple());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long cost = timeCostUtil.stopAndGet();
        if (cost > 10) logger.warn("生产文件消耗告警：{}", cost);
        else logger.info("生产文件消耗：{}", cost);
    }

    private void poll() {
        // 消费数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                while (true) {
                    KafkaTuple kafkaTuple;
                    while ((kafkaTuple = kafkaTupleBlockingQueue.poll()) != null) {
                        cnt++;
                        if (cnt % 2000 == 0) {
//                            logger.info("消费，cnt：{}", cnt);
                            SleepUtils.sleepMilliSecond(5);
                        }
                    }
                    SleepUtils.sleepMilliSecond(1);
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