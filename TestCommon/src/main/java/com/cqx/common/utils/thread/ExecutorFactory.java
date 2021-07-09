package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ExecutorFactory
 *
 * @author chenqixu
 */
public class ExecutorFactory {
    private ExecutorService executor;
    private List<Future> futureList = new ArrayList<>();
    private List<BaseCallable> baseCallableList = new ArrayList<>();

    private ExecutorFactory(int nThreads) {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public static ExecutorFactory newInstance(int nThreads) {
        return new ExecutorFactory(nThreads);
    }

    public void submit(BaseCallable task) {
        baseCallableList.add(task);
        futureList.add(executor.submit(task));
    }

    public void join() throws ExecutionException, InterruptedException {
        for (Future future : futureList) {
            future.get();
        }
    }

    public void stop() {
        for (BaseCallable baseCallable : baseCallableList) {
            baseCallable.stop();
        }
        executor.shutdown();
        //shutdown并不会join到主线程中
        while (!executor.isTerminated()) {
            SleepUtil.sleepMilliSecond(200);
        }
    }
}
