package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorFactory
 *
 * @author chenqixu
 */
public class ExecutorFactory<T> {

    private static Logger logger = LoggerFactory.getLogger(ExecutorFactory.class);
    // 线程池接口
    private ExecutorService executor;
    // 线程池
    private ThreadPoolExecutor threadPoolExecutor;
    // 线程列表
    private List<Callable<T>> callableList;
    // 并发
    private int parallel_num;

    public ExecutorFactory(int parallel_num) {
        executor = Executors.newFixedThreadPool(parallel_num);
        threadPoolExecutor = (ThreadPoolExecutor) executor;
        callableList = new ArrayList<>();
        this.parallel_num = parallel_num;
    }

    public void addCallable(Callable<T> callable) {
        callableList.add(callable);
    }

    public List<Future<T>> submit() {
        List<Future<T>> futures = new ArrayList<>();
//        if (getQueueSize() > parallel_num) {
//            return futures;
//        }
        for (Callable<T> callable : callableList) {
            futures.add(executor.submit(callable));
        }
        return futures;
    }

    public void submitNoReturn() {
//        if (getQueueSize() > parallel_num) {
//            return futures;
//        }
        for (Callable<T> callable : callableList) {
            executor.submit(callable);
        }
    }

    public void submit(Callable<T> callable) {
        executor.submit(callable);
    }

    public List<T> get(List<Future<T>> futures, long timeout, TimeUnit unit) {
        List<T> results = new ArrayList<>();
        for (Future<T> future : futures) {
            try {
                results.add(future.get(timeout, unit));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error(e.getMessage());
            }
        }
        return results;
    }

    private int getQueueSize() {
        return threadPoolExecutor.getQueue().size();
    }

    public void shutdonw() {
        executor.shutdown();
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
