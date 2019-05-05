package com.newland.bi.bigdata.thread;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class MyFutureCancelTest {

    private static Logger logger = LoggerFactory.getLogger(MyFutureCancelTest.class);
    private MyFutureCancel myFutureCancel;

    @Before
    public void setUp() {
        myFutureCancel = new MyFutureCancel();
    }

    @Test
    public void testPrimerTask() throws Exception {
        logger.info("start");
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        long num = 10L;
        Future<Boolean> future = threadPool.submit(myFutureCancel.newPrimerTask(num));
        threadPool.shutdown(); // 发送关闭线程池的指令
        myFutureCancel.cancelTask(future, 500); // 在XXX之后取消该任务

        try {
            boolean result = future.get();
            logger.info("{}", String.format("%d 是否为素数？ %b\n", num, result));
        } catch (CancellationException ex) {
            logger.error("任务被取消");
        } catch (InterruptedException ex) {
            logger.error("当前线程被中断");
        } catch (ExecutionException ex) {
            logger.error("任务执行出错");
        }
        SleepUtils.sleepMilliSecond(5000);
    }

    @Test
    public void testThreadTask() throws Exception {
        Thread t = myFutureCancel.newThreadTask();
        t.start();
        myFutureCancel.delayTask(t, 500);
        t.join();
//        SleepUtils.sleepMilliSecond(3000);
    }

    @Test
    public void testFor() {
        for (int i = 0; i < 1000000; i++) {
            if (i % 9 == 0)
                logger.info("{}", i);
        }
    }
}