package com.cqx.work.stream;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.*;

/**
 * CompletableFutureDemo
 *
 * @author chenqixu
 */
public class CompletableFutureDemo implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFutureDemo.class);
    private ExecutorService executorService = new ThreadPoolExecutor(10
            , 100, 30L, TimeUnit.SECONDS
            , new LinkedBlockingQueue<Runnable>(100)
            , Executors.defaultThreadFactory()
            , new ThreadPoolExecutor.DiscardOldestPolicy());
    private Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        try (CompletableFutureDemo demo = new CompletableFutureDemo()) {
            demo.runAsyncDefaultPool();
            demo.runAsyncCustomPool();
            demo.supplyAsync();
            demo.thenApplyAsync();
            demo.thenAcceptAsync();
            demo.thenRunAsync();
            demo.thenCompose();
            demo.thenCombine();
            demo.thenAcceptBothAsync();
            demo.runAfterBothAsync();
            demo.applyToEitherAsync();
            demo.acceptEitherAsync();
            demo.runAfterEitherAsync();
            demo.allOf();
            demo.anyOf();
            demo.handleAsync();
            demo.whenComplete();
            demo.exceptionally();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 异步完成，无返回值<br>
     * 由于使用默认线程池，不会等待所有线程执行完成
     */
    public void runAsyncDefaultPool() {
        logger.info("[runAsyncDefaultPool] start task");
        CompletableFuture.runAsync(() -> {
            int sleep = 5;
            logger.info("[runAsyncDefaultPool] start thread");
            logger.info("[runAsyncDefaultPool] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[runAsyncDefaultPool] stop thread");
        });
        logger.info("[runAsyncDefaultPool] stop task.");
    }

    /**
     * 异步完成，无返回值<br>
     * 使用自定义线程池，会等待所有线程执行完成
     */
    public void runAsyncCustomPool() {
        logger.info("[runAsyncCustomPool] start task");
        CompletableFuture.runAsync(() -> {
            int sleep = 3;
            logger.info("[runAsyncCustomPool] start thread");
            logger.info("[runAsyncCustomPool] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[runAsyncCustomPool] stop thread");
        }, executorService);
        logger.info("[runAsyncCustomPool] stop task.");
    }

    /**
     * 异步完成，有返回值<br>
     * 使用自定义线程池，会等待所有线程执行完成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void supplyAsync() throws ExecutionException, InterruptedException {
        logger.info("[supplyAsync] start task");
        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> {
            int sleep = 2;
            logger.info("[supplyAsync] start thread");
            logger.info("[supplyAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[supplyAsync] stop thread");
            return sleep;
        }, executorService);
        logger.info("[supplyAsync] stop task. result: {}", supplyAsync.get());
    }

    /**
     * 使用上一步的异步结果另外开一个线程执行，有返回值<br>
     * 如果没有join或get，第二个任务可能等不到执行完成或执行开始
     */
    public void thenApplyAsync() throws ExecutionException, InterruptedException {
        logger.info("[thenApplyAsync] start task");
        CompletableFuture<String> thenApplyAsync = CompletableFuture.supplyAsync(() -> {
            int sleep = 2;
            logger.info("[thenApplyAsync.supplyAsync] start thread");
            logger.info("[thenApplyAsync.supplyAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[thenApplyAsync.supplyAsync] stop thread");
            return sleep;
        }, executorService).thenApplyAsync(result -> {
            int sleep2 = 1;
            logger.info("[thenApplyAsync.thenApplyAsync] start thread, get the return value of the previous task {}", result);
            logger.info("[thenApplyAsync.thenApplyAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep2);
            SleepUtil.sleepSecond(sleep2);
            logger.info("[thenApplyAsync.thenApplyAsync] stop thread");
            return "task2-" + result;
        }, executorService);
        logger.info("[thenApplyAsync] stop task. result: {}", thenApplyAsync.get());
    }

    /**
     * 使用上一步的异步结果另外开一个线程执行，无返回值<br>
     * 如果没有join，第二个任务可能等不到执行完成或执行开始
     */
    public void thenAcceptAsync() {
        logger.info("[thenAcceptAsync] start task");
        CompletableFuture.supplyAsync(() -> {
            int sleep = 2;
            logger.info("[thenAcceptAsync.supplyAsync] start thread");
            logger.info("[thenAcceptAsync.supplyAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[thenAcceptAsync.supplyAsync] stop thread");
            return sleep;
        }, executorService).thenAcceptAsync(result -> {
            int sleep2 = 1;
            logger.info("[thenAcceptAsync.thenAcceptAsync] start thread, get the return value of the previous task {}", result);
            logger.info("[thenAcceptAsync.thenAcceptAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep2);
            SleepUtil.sleepSecond(sleep2);
            logger.info("[thenAcceptAsync.thenAcceptAsync] stop thread");
        }, executorService).join();
        logger.info("[thenAcceptAsync] stop task");
    }

    /**
     * 等待上一步任务执行完成，无结果传递，也无返回值<br>
     * 如果没有join，第二个任务可能等不到执行完成或执行开始
     */
    public void thenRunAsync() {
        logger.info("[thenRunAsync] start task");
        CompletableFuture.supplyAsync(() -> {
            int sleep = 2;
            logger.info("[thenRunAsync.supplyAsync] start thread");
            logger.info("[thenRunAsync.supplyAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep);
            SleepUtil.sleepSecond(sleep);
            logger.info("[thenRunAsync.supplyAsync] stop thread");
            return sleep;
        }, executorService).thenRunAsync(() -> {
            int sleep2 = 1;
            logger.info("[thenRunAsync.thenRunAsync] start thread");
            logger.info("[thenRunAsync.thenRunAsync] now thread: {}，sleep: {}", Thread.currentThread().getName(), sleep2);
            SleepUtil.sleepSecond(sleep2);
            logger.info("[thenRunAsync.thenRunAsync] stop thread");
        }, executorService).join();
        logger.info("[thenRunAsync] stop task");
    }

    /**
     * thenCompose用于连接两个CompletableFuture，生成一个新的CompletableFuture<br>
     * 当原任务完成后接收返回值，返回一个新的任务
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void thenCompose() throws ExecutionException, InterruptedException {
        logger.info("[thenCompose] start task");
        CompletableFuture thenCompose = CompletableFuture.completedFuture("hello")
                .thenCompose(str -> CompletableFuture.supplyAsync(() -> "[" + str + " : thenCompose]", executorService));
        logger.info("[thenCompose] stop task, result: {}", thenCompose.get());
    }

    /**
     * 获取两个任务的执行结果，并计算返回
     */
    public void thenCombine() throws ExecutionException, InterruptedException {
        logger.info("[thenCombine] start task");
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[thenCombine.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[thenCombine.task1] random: {}", ret);
            return ret;
        }, executorService);

        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[thenCombine.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[thenCombine.task2] random: {}", ret);
            return ret;
        }, executorService);

        CompletableFuture<String> thenCombineAsync = task1.thenCombineAsync(task2, (result1, result2) -> {
            logger.info("[thenCombine.thenCombineAsync] result1: {}, result2: {}", result1, result2);
            return result1 + "-->" + result2;
        }, executorService);
        logger.info("[thenCombine] stop task, result: {}", thenCombineAsync.get());
    }

    /**
     * 获取两个任务的执行结果，无返回
     */
    public void thenAcceptBothAsync() {
        logger.info("[thenAcceptBothAsync] start task");
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[thenAcceptBothAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[thenAcceptBothAsync.task1] random: {}", ret);
            return ret;
        }, executorService);

        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[thenAcceptBothAsync.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[thenAcceptBothAsync.task2] random: {}", ret);
            return ret;
        }, executorService);

        task1.thenAcceptBothAsync(task2, (result1, result2) -> {
            logger.info("[thenAcceptBothAsync.thenAcceptBothAsync] result1: {}, result2: {}", result1, result2);
        }, executorService).join();
        logger.info("[thenAcceptBothAsync] stop task");
    }

    /**
     * 两个任务完成后接着运行下一个任务，无参数传递
     */
    public void runAfterBothAsync() {
        logger.info("[runAfterBothAsync] start task");
        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[runAfterBothAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[runAfterBothAsync.task1] random: {}", ret);
            return ret;
        }, executorService);

        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[runAfterBothAsync.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[runAfterBothAsync.task2] random: {}", ret);
            return ret;
        }, executorService);

        task1.runAfterBothAsync(task2, () -> {
            logger.info("[runAfterBothAsync.runAfterBothAsync] now thread: {}", Thread.currentThread().getName());
            logger.info("[runAfterBothAsync.runAfterBothAsync] runAfterBothAsync start");
        }, executorService).join();
        logger.info("[runAfterBothAsync] stop task");
    }

    /**
     * 两个任务只要完成一个，就会触发第三个任务，有返回值传递给第三个任务，最终有返回值
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void applyToEitherAsync() throws ExecutionException, InterruptedException {
        logger.info("[applyToEitherAsync] start task");
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[applyToEitherAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[applyToEitherAsync.task1] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[applyToEitherAsync.task1] task stop");
            return "[task1]" + ret;
        }, executorService);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[applyToEitherAsync.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[applyToEitherAsync.task2] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[applyToEitherAsync.task2] task stop");
            return "[task2]" + ret;
        }, executorService);

        CompletableFuture<String> applyToEitherAsync = task1.applyToEitherAsync(task2, result -> {
            logger.info("[applyToEitherAsync.applyToEitherAsync] {} is complete", result);
            return "[applyToEitherAsync]" + result;
        }, executorService);
        logger.info("[applyToEitherAsync] stop task, result: {}", applyToEitherAsync.get());
    }

    /**
     * 两个任务只要完成一个，就会触发第三个任务，有返回值传递给第三个任务，最终无返回值
     */
    public void acceptEitherAsync() {
        logger.info("[acceptEitherAsync] start task");
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[acceptEitherAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[acceptEitherAsync.task1] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[acceptEitherAsync.task1] task stop");
            return "[task1]" + ret;
        }, executorService);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[acceptEitherAsync.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[acceptEitherAsync.task2] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[acceptEitherAsync.task2] task stop");
            return "[task2]" + ret;
        }, executorService);

        task1.acceptEitherAsync(task2, result -> {
            logger.info("[acceptEitherAsync.acceptEitherAsync] {} is complete", result);
        }, executorService).join();
        logger.info("[acceptEitherAsync] stop task");
    }

    /**
     * 两个任务只要完成一个，就会触发第三个任务，无返回值传递给第三个任务，最终无返回值
     */
    public void runAfterEitherAsync() {
        logger.info("[runAfterEitherAsync] start task");
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[runAfterEitherAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[runAfterEitherAsync.task1] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[runAfterEitherAsync.task1] task stop");
            return "[task1]" + ret;
        }, executorService);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[runAfterEitherAsync.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[runAfterEitherAsync.task2] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[runAfterEitherAsync.task2] task stop");
            return "[task2]" + ret;
        }, executorService);

        task1.runAfterEitherAsync(task2, () -> {
            logger.info("[runAfterEitherAsync.runAfterEitherAsync] An unknown task has been completed");
        }, executorService).join();
        logger.info("[runAfterEitherAsync] stop task");
    }

    /**
     * 等待所有任务完成
     */
    public void allOf() {
        logger.info("[allOf] start task");
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[allOf.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[allOf.task1] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[allOf.task1] task stop");
            return "[task1]" + ret;
        }, executorService);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[allOf.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[allOf.task2] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[allOf.task2] task stop");
            return "[task2]" + ret;
        }, executorService);

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            logger.info("[allOf.task3] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[allOf.task3] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[allOf.task3] task stop");
            return "[task3]" + ret;
        }, executorService);

        CompletableFuture.allOf(task1, task2, task3).join();
        logger.info("[allOf] stop task");
    }

    /**
     * 返回第一个完成的任务
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void anyOf() throws ExecutionException, InterruptedException {
        logger.info("[anyOf] start task");
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            logger.info("[anyOf.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[anyOf.task1] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[anyOf.task1] task stop");
            return "[task1]" + ret;
        }, executorService);

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            logger.info("[anyOf.task2] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[anyOf.task2] random: {}", ret);
            SleepUtil.sleepSecond(ret);
            logger.info("[anyOf.task2] task stop");
            return "[task2]" + ret;
        }, executorService);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task1, task2);
        logger.info("[anyOf] 第一个完成的任务: {}", anyOf.get());
        logger.info("[anyOf] stop task");
    }

    /**
     * 捕获结果或异常并返回新结果
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void handleAsync() throws ExecutionException, InterruptedException {
        logger.info("[handleAsync] start task");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            logger.info("[handleAsync.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[handleAsync.task1] random: {}", ret);
            if (ret % 2 == 0) {
                throw new UnsupportedOperationException("模拟了一个偶数的异常！");
            }
            logger.info("[handleAsync.task1] task stop");
            return "[handleAsync]" + ret;
        }, executorService).handleAsync((result, throwable) -> {
            if (throwable != null) {
                return String.format("报错返回，异常信息：%s", throwable.getMessage());
            } else {
                return String.format("正确返回，返回结果：%s", result);
            }
        });
        logger.info("[handleAsync] stop task, result: {}", completableFuture.get());
    }

    /**
     * 感知结果或异常并返回相应信息，但是不能修改返回信息
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void whenComplete() throws ExecutionException, InterruptedException {
        logger.info("[whenComplete] start task");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            logger.info("[whenComplete.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[whenComplete.task1] random: {}", ret);
            if (ret % 2 == 0) {
                throw new UnsupportedOperationException("模拟了一个偶数的异常！");
            }
            logger.info("[whenComplete.task1] task stop");
            return "[whenComplete]" + ret;
        }, executorService).whenComplete((result, throwable) -> {
            if (throwable != null) {
                logger.info(String.format("报错返回，异常信息：%s", throwable.getMessage()));
            } else {
                logger.info(String.format("正确返回，返回结果：%s", result));
            }
        });
        logger.info("[whenComplete] stop task, result: {}", completableFuture.get());
    }

    /**
     * 捕获异常并返回指定值
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void exceptionally() throws ExecutionException, InterruptedException {
        logger.info("[exceptionally] start task");
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            logger.info("[exceptionally.task1] now thread: {}", Thread.currentThread().getName());
            int ret = random.nextInt(10);
            logger.info("[exceptionally.task1] random: {}", ret);
            if (ret % 2 == 0) {
                throw new UnsupportedOperationException("模拟了一个偶数的异常！");
            }
            logger.info("[exceptionally.task1] task stop");
            return "[exceptionally]" + ret;
        }, executorService).exceptionally(throwable -> {
            logger.info(String.format("报错返回，异常信息：%s", throwable.getMessage()));
            return "[exceptionally]-1";
        });
        logger.info("[exceptionally] stop task, result: {}", completableFuture.get());
    }

    @Override
    public void close() {
        if (executorService != null) {
            logger.info("executorService.shutdown");
            executorService.shutdown();
        }
    }
}