package com.newland.bi.bigdata.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExecutorsFactory
 *
 * @author chenqixu
 */
public class ExecutorsFactory<T> {

    // 日志类
    private static Logger logger = LoggerFactory.getLogger(ExecutorsFactory.class);
    // 封闭线程池
    private ThreadPoolExecutor executor;
    // 具体实现
    private IExecutorsRun<T> iExecutorsRun;
    // 并发
    private int parallel_num;
    //    // FutureList
//    private List<Future> futureListPool = new ArrayList<>();
    // CancelQueue
    private BlockingQueue<Future> cancelQueue;
    // AwaitQueue
    private BlockingQueue<Future> awaitQueue;
    // SubmitQueue
    private BlockingQueue<Callable> submitQueue;

    public ExecutorsFactory(int parallel_num) {
        this.parallel_num = parallel_num;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(parallel_num);
        cancelQueue = new LinkedBlockingQueue<>();
        awaitQueue = new LinkedBlockingQueue<>();
        submitQueue = new LinkedBlockingQueue<>();
    }

    public void setiExecutorsRun(IExecutorsRun iExecutorsRun) {
        this.iExecutorsRun = iExecutorsRun;
    }

    private void check() {
        if (iExecutorsRun == null) throw new NullPointerException("iExecutorsRun is null ! Please check !");
    }

    /**
     * 启动Callable
     *
     * @throws InterruptedException
     */
    public void startCallable() throws ExecutionException, InterruptedException {
        Callable myCallable = new Callable() {
            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public Long call() throws Exception {
                int mod = atomicInteger.incrementAndGet();
                logger.info("{} run. mod is：{}", this, mod--);
                check();
                iExecutorsRun.run();
                return null;
            }
        };
        addFutureList(myCallable);
//        awaitFuture(addFutureList(myCallable));
//        awaitCallable(addCallableList(myCallable));
    }

    public void printlnStatus() {
        logger.info("{} getActiveCount：{}，getTaskCount：{}，getCompletedTaskCount：{}",
                this, executor.getActiveCount(), executor.getTaskCount(), executor.getCompletedTaskCount());
        printlnFutureListPoolStatus();
    }

    private void printlnFutureListPoolStatus() {
        logger.info("{} cancelQueue：{}，awaitQueue：{}", this, cancelQueue.size(), awaitQueue.size());
    }

    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    private List<Callable<Long>> addCallableList(Callable myCallable) {
        List<Callable<Long>> callableList = new ArrayList<>();
        for (int i = 0; i < parallel_num; i++) {
            callableList.add(myCallable);
        }
        return callableList;
    }

    private List<Future> addFutureList(Callable myCallable) {
        List<Future> futureList = new ArrayList<>();
        for (int i = 0; i < parallel_num; i++) {
            futureList.add(executor.submit(myCallable));
        }
        return futureList;
    }

    private void awaitCallable(List<Callable<Long>> callableList) throws InterruptedException {
        executor.invokeAll(callableList);
    }

    /**
     * 等待执行完成
     *
     * @param futureList
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void awaitFuture(List<Future> futureList) {
        Iterator<Future> futureIterator = futureList.iterator();
        while (futureIterator.hasNext()) {
            Future future = null;
            try {
                future = futureIterator.next();
                future.get();
                futureIterator.remove();
                logger.debug("{} futureIterator.remove", future);
            } catch (CancellationException e) {
                logger.error(future + "，任务被取消");
                futureIterator.remove();
                logger.debug("{} futureIterator.remove", future);
            } catch (InterruptedException e) {
                logger.error(future + "，当前线程被中断");
                futureIterator.remove();
                logger.debug("{} futureIterator.remove", future);
            } catch (ExecutionException e) {
                logger.error(future + "，任务执行出错");
                futureIterator.remove();
                logger.debug("{} futureIterator.remove", future);
            }
        }
    }

    private void awaitFutureQueue() {
        Future future;
        while ((future = awaitQueue.poll()) != null) {
            try {
                future.get();
            } catch (CancellationException e) {
                logger.error(future + "，任务被取消");
            } catch (InterruptedException e) {
                logger.error(future + "，当前线程被中断");
            } catch (ExecutionException e) {
                logger.error(future + "，任务执行出错");
            }
        }
        // 所有任务不是完成，就是取消，可以把cancelQueue队列消费掉了
        while (cancelQueue.poll() != null) {
        }
    }

    public void addCallable(final T t) throws InterruptedException {
        Callable myCallable = new Callable() {
            @Override
            public Long call() throws Exception {
                check();
                iExecutorsRun.run(t);
                return null;
            }
        };
        submitQueue.put(myCallable);
    }

    public void startCallableQueue() throws InterruptedException {
        Callable callable;
        while ((callable = submitQueue.poll()) != null) {
            Future future = executor.submit(callable);
            cancelQueue.put(future);
            awaitQueue.put(future);
        }
    }

    public void submitCallable(final T t) throws InterruptedException {
        Callable myCallable = new Callable() {
            @Override
            public Long call() throws Exception {
                check();
                iExecutorsRun.run(t);
                return null;
            }
        };
        //往List中增加提交的任务
//        futureListPool.add(executor.submit(myCallable));
        Future future = executor.submit(myCallable);
        cancelQueue.put(future);
        awaitQueue.put(future);
    }

    public void awaitFutureListPool() {
//        awaitFuture(futureListPool);
        awaitFutureQueue();
    }

    public void cancelFutureListPool() {
        Future future;
        while ((future = cancelQueue.poll()) != null) {
            boolean result = future.cancel(true);
            logger.debug("{} cancel：{}", future, result);
        }
    }

    public void close() {
        if (executor != null)
            // 禁止往线程池提交任务
            executor.shutdown();
        // 取消所有任务
    }
}
