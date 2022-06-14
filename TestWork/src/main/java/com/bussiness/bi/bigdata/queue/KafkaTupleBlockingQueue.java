package com.bussiness.bi.bigdata.queue;

import com.bussiness.bi.bigdata.bean.KafkaTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
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
    private Deque<KafkaTuple> kafkaTupleDeque = new ArrayDeque<>();
    private volatile boolean status = true;
    private volatile int discardCount = 0;
    private volatile boolean islose = false;

    public KafkaTupleBlockingQueue() {
    }

    public KafkaTupleBlockingQueue(boolean islose) {
        this.islose = islose;
    }

    public void put(KafkaTuple kafkaTuple) throws InterruptedException {
        if (this.status) {
            if (islose && this.kafkaTupleBlockingQueue.size() >= maxCount) {
                // 顶层抛掉
                KafkaTuple kafkaTuple1 = poll();
                // 抛弃计数
                discardCount++;
                logger.info("抛弃数据：{}", kafkaTuple1);
            }
            this.kafkaTupleBlockingQueue.put(kafkaTuple);
        }
//        addFirst(kafkaTuple);
    }

    public KafkaTuple poll() {
        return this.status ? this.kafkaTupleBlockingQueue.poll() : null;
//        return pollLast();
    }

    public void addFirst(KafkaTuple kafkaTuple) {
        kafkaTupleDeque.addFirst(kafkaTuple);
    }

    public KafkaTuple pollLast() {
        return kafkaTupleDeque.pollLast();
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
