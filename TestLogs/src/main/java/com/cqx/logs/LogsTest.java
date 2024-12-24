package com.cqx.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试Logger是否会输出堆栈日志<br>
 * VM options：-Xms512m -Xmx512m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:d:/tmp/logs/msgsend/gc.log
 *
 * @author chenqixu
 */
public class LogsTest {
    private static final Logger logger = LoggerFactory.getLogger(LogsTest.class);

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        // System.out输出
        try (PrintStream ps = new PrintStream(new FileOutputStream("d:/tmp/logs/msgsend/stdout.log", true))) {
            System.setOut(ps);

            // logger测试
            logger.info("info test.");
            logger.warn("warn test.");
            logger.error("error test.", new RuntimeException("runtimeEx."));

            LogsTest lt = new LogsTest();
            lt.monitor(lt.newThread());

            // 另一个类测试System.out输出
            new SystemLogsTest().print();

            // 不断创建对象并添加到列表中，模拟内存泄漏
            List<Object> list = new ArrayList<>();
            try {
                while (true) {
                    list.add(new Object());
                }
            } catch (Exception e) {
                // 经测试，内存泄露，这里不会打压
                logger.error(e.getMessage(), e);
            }
        }
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
