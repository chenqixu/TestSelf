package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorFactoryV1
 *
 * @author chenqixu
 */
public class ExecutorFactoryV1 {
    private ExecutorService executor;
    private List<Future> futureList = new ArrayList<>();
    private List<BaseCallableV1> baseCallableList = new ArrayList<>();

    private ExecutorFactoryV1(int nThreads) {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public static ExecutorFactoryV1 newInstance(int nThreads) {
        return new ExecutorFactoryV1(nThreads);
    }

    public void submit(BaseCallableV1 task) {
        baseCallableList.add(task);
        futureList.add(executor.submit(task));
    }

    public void join() throws ExecutionException, InterruptedException {
        for (Future future : futureList) {
            future.get();
        }
    }

    public void joinAndClean() throws ExecutionException, InterruptedException {
        join();
        futureList.clear();
        baseCallableList.clear();
    }

    public void stop() {
        for (BaseCallableV1 baseCallable : baseCallableList) {
            baseCallable.stop();
        }
        executor.shutdown();
        //shutdown并不会join到主线程中
        while (!executor.isTerminated()) {
            SleepUtil.sleepMilliSecond(200);
        }
    }

    /**
     * 打印线程池状态
     */
    public void printPoolStatus(Logger logger) {
        logger.info("ActiveCount：{}，CorePoolSize：{}，PoolSize：{}，Queue.size：{}，TaskCount：{}，CompletedTaskCount：{}，KeepAliveTime：{}",
                ((ThreadPoolExecutor) executor).getActiveCount(),
                ((ThreadPoolExecutor) executor).getCorePoolSize(),
                ((ThreadPoolExecutor) executor).getPoolSize(),
                ((ThreadPoolExecutor) executor).getQueue().size(),
                ((ThreadPoolExecutor) executor).getTaskCount(),
                ((ThreadPoolExecutor) executor).getCompletedTaskCount(),
                ((ThreadPoolExecutor) executor).getKeepAliveTime(TimeUnit.MILLISECONDS));
    }
}
