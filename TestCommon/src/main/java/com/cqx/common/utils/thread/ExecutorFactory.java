package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ExecutorFactory
 *
 * @author chenqixu
 */
public class ExecutorFactory<V> {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorFactory.class);
    private ExecutorService executor;
    private Future<List<V>> future;
    private BaseCallable<V> callable;

    private ExecutorFactory(int nThreads) {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public static <V> ExecutorFactory<V> newInstance(int nThreads) {
        return new ExecutorFactory<>(nThreads);
    }

    public void add(BaseCallable<V> task, Map params) {
        task.init(params);
        callable = task;
    }

    public boolean hasTask() {
        return callable != null;
    }

    public void submit(long timeout) {
        callable.restart(timeout);
        future = executor.submit(callable);
    }

    public List<V> get() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void stop() {
        callable.stop();
        executor.shutdown();
        //shutdown并不会join到主线程中
        while (!executor.isTerminated()) {
            SleepUtil.sleepMilliSecond(200);
        }
    }
}
