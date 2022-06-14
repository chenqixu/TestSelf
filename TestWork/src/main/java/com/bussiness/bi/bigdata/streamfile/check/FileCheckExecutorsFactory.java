package com.bussiness.bi.bigdata.streamfile.check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * FileCheckExecutorsFactory
 *
 * @author chenqixu
 */
public class FileCheckExecutorsFactory {

    // 日志类
    private static Logger logger = LoggerFactory.getLogger(FileCheckExecutorsFactory.class);
    // 封闭线程池
    private ExecutorService executor;
    // CancelQueue
    private BlockingQueue<Future> cancelQueue;
    // AwaitQueue
    private BlockingQueue<Future> awaitQueue;
    // SubmitQueue
    private BlockingQueue<Callable> submitQueue;

    public FileCheckExecutorsFactory(int parallel_num) {
        executor = Executors.newFixedThreadPool(parallel_num);
        cancelQueue = new LinkedBlockingQueue<>();
        awaitQueue = new LinkedBlockingQueue<>();
        submitQueue = new LinkedBlockingQueue<>();
    }

    public void addTask(Callable callable) throws InterruptedException {
        submitQueue.put(callable);
    }

    public void startTask() throws InterruptedException {
        Callable callable;
        while ((callable = submitQueue.poll()) != null) {
            Future future = executor.submit(callable);
            cancelQueue.put(future);
            awaitQueue.put(future);
        }
    }

    public void submitTask(Callable callable) throws InterruptedException {
        Future future = executor.submit(callable);
        cancelQueue.put(future);
        awaitQueue.put(future);
    }

    public void await() {
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

    public void cancelTask() {
        Future future;
        while ((future = cancelQueue.poll()) != null) {
            boolean result = future.cancel(true);
            logger.debug("{} cancel：{}", future, result);
        }
    }
}
