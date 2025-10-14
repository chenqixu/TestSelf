package com.bussiness.bi.bigdata.label.rm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

public class LogsParseTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parse() {
    }

    @Test
    public void parse1() throws Exception {
        Object lock = new Object();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        // 启动两个线程，使用锁交互进行
        Thread t1 = new Thread(() -> {
            System.out.printf("[%s] start%n", Thread.currentThread());
            while (true) {
                // 同步
                synchronized (lock) {
                    Object v = map.get("1");
                    if (v == null) {
                        try {
                            System.out.printf("[%s] wait%n", Thread.currentThread());
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.printf("[%s] ok%n", Thread.currentThread());
                        break;
                    }
                }
            }
            System.out.printf("[%s] end%n", Thread.currentThread());
        });

        Thread t2 = new Thread(() -> {
            System.out.printf("[%s] start%n", Thread.currentThread());
            // 同步
            synchronized (lock) {
                map.put("1", "1");
                lock.notify();
            }
            System.out.printf("[%s] end%n", Thread.currentThread());
        });

        t1.start();
        Thread.sleep(500L);
        t2.start();
        t2.join();
        t1.join();
    }
}