package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorFactoryTest.class);
    // 创建一个同步列表
    private final List<String> synchedList = Collections.synchronizedList(new LinkedList<>());
    // 同步对象
    private final Object lock = new Object();

    @Test
    public void submit() throws ExecutionException, InterruptedException {
        List<String> result;
        ExecutorFactory<String> executorFactory = ExecutorFactory.newInstance(2);
        executorFactory.add(new BaseCallable<String>() {
            Random random = new Random();

            @Override
            public String exec() throws Exception {
                SleepUtil.sleepMilliSecond(1);
                return random.nextInt(10000) + "";
            }
        }, null);

        executorFactory.submit(100);
        SleepUtil.sleepMilliSecond(10);
        result = executorFactory.get();
        logger.info("{}", result);

        executorFactory.submit(100);
        SleepUtil.sleepMilliSecond(10);
        result = executorFactory.get();
        logger.info("{}", result);

        executorFactory.stop();
    }

    @Test
    public void waitTest() throws Exception {
        AtomicBoolean t1Flag = new AtomicBoolean(true);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                long cnt = 0L;
                while (t1Flag.get()) {
                    SleepUtil.sleepMilliSecond(10);
                    logger.info("{}", cnt++);
                    if (cnt % 30 == 0) {
                        synchronized (lock) {
                            logger.warn("NotifyAll!");
                            lock.notifyAll();
                        }
                    }
                }
            }
        });
        t1.start();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int rd = random.nextInt(100);
            logger.info("i：{}，rd：{}", i, rd);
            if (rd % 2 == 0) {
                synchronized (lock) {
                    logger.warn("Wait!");
                    // 实际上是当前线程wait
                    lock.wait();
                    logger.warn("Wake up!");
                }
            }
            SleepUtil.sleepMilliSecond(rd);
        }
        t1Flag.set(false);
    }

    @Test
    public void RunoobTest() {
        Runnable runA = new Runnable() {

            public void run() {
                try {
                    String item = removeElement();
                    logger.info("removeElement:'{}'", item);
                } catch (InterruptedException ix) {
                    logger.warn("Interrupted Exception!");
                } catch (Exception x) {
                    logger.warn("Exception thrown.");
                }
            }
        };

        Runnable runB = new Runnable() {

            // 执行添加元素操作，并开始循环
            public void run() {
                addElement("Hello!");
            }
        };

        try {
            Thread threadA1 = new Thread(runA, "Google");
            threadA1.start();

            Thread.sleep(500);

            Thread threadA2 = new Thread(runA, "Runoob");
            threadA2.start();

            Thread.sleep(500);

            Thread threadB = new Thread(runB, "Taobao");
            threadB.start();

            Thread.sleep(1000);

            threadA1.interrupt();
            threadA2.interrupt();
        } catch (InterruptedException x) {
            // no
        }
    }

    // 删除列表中的元素
    private String removeElement() throws InterruptedException {
        synchronized (synchedList) {
            // 列表为空就等待
            while (synchedList.isEmpty()) {
                logger.info("List is empty...");
                synchedList.wait();
                logger.info("Waiting...");
            }
            return synchedList.remove(0);
        }
    }

    // 添加元素到列表
    private void addElement(String element) {
        logger.info("Opening...");
        synchronized (synchedList) {
            // 添加一个元素
            synchedList.add(element);
            logger.info("New Element:'{}'", element);
            // 通知元素已存在
            synchedList.notifyAll();
            logger.info("notifyAll called!");
        }
        logger.info("Closing...");
    }
}