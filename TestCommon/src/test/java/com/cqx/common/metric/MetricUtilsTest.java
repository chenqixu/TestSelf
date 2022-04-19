package com.cqx.common.metric;

import com.codahale.metrics.Meter;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MetricUtilsTest {
    private final Object lock = new Object();
    private Meter producer = MetricUtils.getMeter("producer");
    private Meter consumer = MetricUtils.getMeter("consumer");

    @Before
    public void setUp() throws Exception {
        MetricUtils.reset();
        MetricUtils.build(3, TimeUnit.SECONDS);
    }

    @Test
    public void report() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        Thread prouducer = new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                while (true) {
//                    if (cnt % 1000 == 0) {
//                        SleepUtil.sleepMilliSecond(3000);
//                    }
                    try {
                        queue.put("" + cnt++);
                        producer.mark(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(prouducer);
        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String content;
                    while (true) {
                        while ((content = queue.poll()) != null) {
                            synchronized (lock) {
                                consumer.mark(1);
                            }
                        }
                    }
                }
            });
            threadList.add(t);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
    }
}