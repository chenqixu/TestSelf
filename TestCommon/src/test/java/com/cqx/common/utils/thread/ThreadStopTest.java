package com.cqx.common.utils.thread;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author chenqixu
 */
public class ThreadStopTest {

    @Test
    public void test1() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // 打开一个长连接，比如通过Socket连上一个服务器

            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("stop t1");
                try {
                    t1.stop();
                } catch (Exception e) {
                    System.out.println("catch t1 exception");
                    e.printStackTrace();
                }
            }
        });
        t2.start();
        t1.start();
        t1.join();
        t2.join();
    }
}
