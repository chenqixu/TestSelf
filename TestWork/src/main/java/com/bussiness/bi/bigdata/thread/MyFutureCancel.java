package com.bussiness.bi.bigdata.thread;

import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

/**
 * MyFutureCancel
 *
 * @author chenqixu
 */
public class MyFutureCancel {

    private static Logger logger = LoggerFactory.getLogger(MyFutureCancel.class);
    private Random random = new Random();

    public void cancelTask(final Future<?> future, final int delay) {
        Runnable cancellation = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    future.cancel(true); // 取消与 future 关联的正在运行的任务
//                    future.get(1, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
//                } catch (ExecutionException e) {
//                    logger.error(e.getMessage(), e);
//                } catch (TimeoutException e) {
//                    logger.error(e.getMessage(), e);
                }
            }
        };
        new Thread(cancellation).start();
    }

    public void delayTask(final Thread t, final int delay) {
        Runnable cancellation = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    t.interrupt();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        new Thread(cancellation).start();
    }

    public PrimerTask newPrimerTask(long num) {
        return new PrimerTask(num);
    }

    public Thread newThreadTask() {
        return new ThreadTask();
    }

    public interface ThreadTaskInterrupt {
        void checkInterrupt() throws InterruptedException;
    }

    public static class MyFutureCancelUtil {
        public static void merge(ThreadTaskInterrupt threadTaskInterrupt) throws InterruptedException {
            for (int i = 0; i < 10; i++) {
                logger.info("merge……");
                threadTaskInterrupt.checkInterrupt();
                SleepUtils.sleepMilliSecond(100);
            }
        }

        public static void getCacheList(ThreadTaskInterrupt threadTaskInterrupt) throws InterruptedException {
            logger.info("connect Cache Service");
            SleepUtils.sleepMilliSecond(200);
            logger.info("getCacheList");
        }

        public static void copyFromLocalFile(ThreadTaskInterrupt threadTaskInterrupt) throws InterruptedException {
            logger.info("copyFromLocalFile start");
            SleepUtils.sleepMilliSecond(2000);
            logger.info("copyFromLocalFile ok");
        }
    }

    public class ThreadTask extends Thread implements ThreadTaskInterrupt {

        private volatile boolean flag = false;

        @Override
        public void interrupt() {
            super.interrupt();
            flag = true;
            logger.info("interrupt……");
        }

        @Override
        public void checkInterrupt() throws InterruptedException {
            if (flag)
                throw new InterruptedException("interruptTask");
        }

        @Override
        public void run() {
            try {
//                for (int i = 0; i < 20; i++) {
//                    checkInterrupt();
//                    logger.info("i：{}，flag：{}", i, flag);
//                    SleepUtils.sleepMilliSecond(100);
//                }
                MyFutureCancelUtil.getCacheList(this);
                MyFutureCancelUtil.merge(this);
                MyFutureCancelUtil.copyFromLocalFile(this);
            } catch (InterruptedException e) {
                logger.error("收到InterruptedException");
//                Thread.currentThread().interrupt();
            }
        }
    }

    public class PrimerTask implements Callable<Boolean> {

        private long num;
        private int interrupted = 0;

        public PrimerTask(long num) {
            this.num = num;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                // i < num 让任务有足够的运行时间
                for (long i = 2; i < num; i++) {
                    logger.info("{} % {}：{}", num, i, num % i);
                    SleepUtils.sleepMilliSecond(random.nextInt(300));
                    boolean cancel = Thread.currentThread().isInterrupted();
                    if (cancel) {// 任务被取消
                        logger.info("PrimerTask.call： status {}，你取消我干啥？", cancel);
                        interrupted++;
//                    return false;
                    }
                    if (interrupted > 0 && interrupted < 10) {
                        logger.info("isInterrupted {}", Thread.currentThread().isInterrupted());
                        interrupted++;
                    }
//                if (num % i == 0) {
//                    return false;
//                }
                }
                logger.info("result……");
            } catch (CancellationException e) {
                logger.error("CancellationException");
            }
            return true;
        }
    }

}
