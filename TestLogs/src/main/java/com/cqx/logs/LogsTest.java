package com.cqx.logs;

import org.apache.log4j.Logger;

/**
 * 测试Logger是否会输出堆栈日志
 *
 * @author chenqixu
 */
public class LogsTest {
    private static final Logger logger = Logger.getLogger(LogsTest.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("info test.");
        logger.warn("warn test.");
        logger.error("error test.", new RuntimeException("runtimeEx."));

        LogsTest lt = new LogsTest();
        lt.monitor(lt.newThread());
    }

    public void init() {

    }

    public Thread newThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    i++;
                    if (i % 10000 == 0) {
                        System.out.println(String.format("[i]%s", i));
                    }
                    if (i > 10000000) {
                        System.out.println("newThread break.");
                        break;
                    }
                }
            }
        });
        t.setName("newThread123");
        t.start();
        return t;
    }

    public void monitor(final Thread _t) throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5L);
                        boolean flag = _t.isAlive();
                        System.out.println(String.format("[name]%s, [isAlive]%s", _t.getName(), flag));
                        if (!flag) {
                            System.out.println("monitor break.");
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        t.join();
    }
}
