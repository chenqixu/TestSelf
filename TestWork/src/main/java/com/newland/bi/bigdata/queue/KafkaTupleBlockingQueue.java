package com.newland.bi.bigdata.queue;

import com.newland.bi.bigdata.bean.KafkaTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * KafkaTupleBlockingQueue
 *
 * @author chenqixu
 */
public class KafkaTupleBlockingQueue {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTupleBlockingQueue.class);
    private final int maxCount = 10;
    private BlockingQueue<KafkaTuple> kafkaTupleBlockingQueue = new LinkedBlockingQueue<>();
    private volatile boolean status = true;
    private volatile int discardCount = 0;

    public void put(KafkaTuple kafkaTuple) throws InterruptedException {
        if (this.status) {
            if (this.kafkaTupleBlockingQueue.size() >= maxCount) {
                // 顶层抛掉
                KafkaTuple kafkaTuple1 = poll();
                // 抛弃计数
                discardCount++;
                logger.info("抛弃数据：{}", kafkaTuple1);
            }
            this.kafkaTupleBlockingQueue.put(kafkaTuple);
        }
    }

    public KafkaTuple poll() {
        return this.status ? this.kafkaTupleBlockingQueue.poll() : null;
    }

    public void stop() {
        this.status = false;
    }

    public int getDiscardCount() {
        return discardCount;
    }

    public int getQueueSize() {
        return this.kafkaTupleBlockingQueue.size();
    }
}
