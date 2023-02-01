package com.cqx.work.stream;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试说明：<br>
 * 调整corePoolSize、maximumPoolSize、keepAliveTime、capacity、策略<br>
 * 1、corePoolSize &lt maxmumPoolSzie<br>
 * 2、corePoolSize &gt maxmumPoolSize<br>
 * 3、keepAliveTime &lt 单个任务运行时长<br>
 * 4、keepAliveTime &gt 单个任务运行时长<br>
 * 5、capacity &lt 总任务个数<br>
 * 6、capacity &gt 总任务个数<br>
 * 7、策略：AbortPolicy<br>
 * 策略：CallerRunsPolicy<br>
 * 策略：DiscardOldestPolicy<br>
 * 策略：DiscardPolicy
 *
 * @author chenqixu
 */
public class ExecutorServiceDemo implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceDemo.class);
    /**
     * 任务休眠时间
     */
    private final int TASK_SLEEP_TIME = 5;
    /**
     * 任务个数
     */
    private final int TASK_SIZE = 10;
    /**
     * <pre>
     *     corePoolSize–池中要保留的线程数，即使它们处于空闲状态，除非设置了allowCoreThreadTimeOut
     *     maximumPoolSize–池中允许的最大线程数
     *     keepAliveTime–当线程数大于核心时，这是多余的空闲线程在终止前等待新任务的最长时间。
     *     unit–keepAliveTime参数的时间单位
     *     workQueue–用于在执行任务之前保存任务的队列。此队列将仅保存execute方法提交的Runnable任务。
     *     threadFactory–执行器创建新线程时要使用的工厂
     *     handler–当由于达到线程边界和队列容量而导致执行受阻时使用的处理程序
     * </pre>
     */
    private ExecutorService executorService = new ThreadPoolExecutor(5
            , 5, 15L, TimeUnit.SECONDS
            , new LinkedBlockingQueue<Runnable>(10)
            , Executors.defaultThreadFactory()
            , new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 1、corePoolSize &lt maxmumPoolSzie<br>
     * 预计结果：每次会运行maxmumPoolSzie个任务<br>
     * 实际结果：并发是corePoolSize，所有任务都运行完成，和预计不符
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo1() throws ExecutionException, InterruptedException {
        logger.info("====[测试：corePoolSize < maxmumPoolSzie]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 30L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < TASK_SIZE; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 2、corePoolSize &gt maxmumPoolSize<br>
     * 预计结果：未知<br>
     * 实际结果：任务无法运行
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo2() throws ExecutionException, InterruptedException {
        logger.info("====[测试：corePoolSize < maxmumPoolSzie]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 1, 30L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < TASK_SIZE; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 3、keepAliveTime &lt 单个任务运行时长<br>
     * 预计结果：未知<br>
     * 实际结果：并发是corePoolSize，所有任务都运行完成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo3() throws ExecutionException, InterruptedException {
        logger.info("====[测试：keepAliveTime < 单个任务运行时长]====");
        ThreadPoolExecutor executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 1L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < TASK_SIZE; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            while (executorService.getQueue().size() > 0) {
                logger.info("[executorService] CompletedTaskCount={}, ActiveCount={}" +
                                ", Queue={}, Queue.size={}, TaskCount={}, CorePoolSize={}, PoolSize={}" +
                                ", LargestPoolSize={}, MaximumPoolSize={}, RejectedExecutionHandler={}"
                        , executorService.getCompletedTaskCount()
                        , executorService.getActiveCount()
                        , executorService.getQueue()
                        , executorService.getQueue().size()
                        , executorService.getTaskCount()
                        , executorService.getCorePoolSize()
                        , executorService.getPoolSize()
                        , executorService.getLargestPoolSize()
                        , executorService.getMaximumPoolSize()
                        , executorService.getRejectedExecutionHandler()
                );
                SleepUtil.sleepMilliSecond(500);
            }
//            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 4、keepAliveTime &gt 单个任务运行时长<br>
     * 预计结果：<br>
     * 实际结果：并发是corePoolSize，所有任务都运行完成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo4() throws ExecutionException, InterruptedException {
        logger.info("====[测试：keepAliveTime > 单个任务运行时长]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < TASK_SIZE; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 5、capacity &lt 总任务个数<br>
     * 预计结果：<br>
     * 实际结果：并发是maximumPoolSize，如果任务过多，中间的任务会被抛掉<br>
     * 这个时候不能allOf.join，只能判断队列大小
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo5() throws ExecutionException, InterruptedException {
        logger.info("====[测试：capacity < 总任务个数]====");
        ThreadPoolExecutor executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(5)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < 50; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            while (executorService.getQueue().size() > 0) {
                logger.info("[executorService] CompletedTaskCount={}, ActiveCount={}" +
                                ", Queue={}, Queue.size={}, TaskCount={}, CorePoolSize={}, PoolSize={}" +
                                ", LargestPoolSize={}, MaximumPoolSize={}, RejectedExecutionHandler={}"
                        , executorService.getCompletedTaskCount()
                        , executorService.getActiveCount()
                        , executorService.getQueue()
                        , executorService.getQueue().size()
                        , executorService.getTaskCount()
                        , executorService.getCorePoolSize()
                        , executorService.getPoolSize()
                        , executorService.getLargestPoolSize()
                        , executorService.getMaximumPoolSize()
                        , executorService.getRejectedExecutionHandler()
                );
                SleepUtil.sleepMilliSecond(500);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 6、capacity &gt 总任务个数<br>
     * 预计结果：<br>
     * 实际结果：并发是corePoolSize，所有任务都运行完成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo6() throws ExecutionException, InterruptedException {
        logger.info("====[测试：capacity > 总任务个数]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(15)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardOldestPolicy());
            for (int i = 0; i < TASK_SIZE; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 7、策略：AbortPolicy<br>
     * 预计结果：<br>
     * 实际结果：
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo7AbortPolicy() throws ExecutionException, InterruptedException {
        logger.info("====[测试：策略：AbortPolicy]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.AbortPolicy());
            for (int i = 0; i < 50; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 7、策略：CallerRunsPolicy<br>
     * 预计结果：<br>
     * 实际结果：会多一个主线程一起执行任务，所有任务都会运行完成
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo7CallerRunsPolicy() throws ExecutionException, InterruptedException {
        logger.info("====[测试：策略：CallerRunsPolicy]====");
        ExecutorService executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.CallerRunsPolicy());
            for (int i = 0; i < 50; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }

    /**
     * 测试说明：<br>
     * 7、策略：DiscardPolicy<br>
     * 预计结果：<br>
     * 实际结果：因为任务数大于缓存的队列，所以大于的部分都被抛弃了，这里也不能用join
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void demo7DiscardPolicy() throws ExecutionException, InterruptedException {
        logger.info("====[测试：策略：DiscardPolicy]====");
        ThreadPoolExecutor executorService = null;
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        try {
            executorService = new ThreadPoolExecutor(3
                    , 5, 15L, TimeUnit.SECONDS
                    , new LinkedBlockingQueue<Runnable>(10)
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.DiscardPolicy());
            AtomicInteger startCnt = new AtomicInteger(0);
            AtomicInteger stopCnt = new AtomicInteger(0);
            for (int i = 0; i < 50; i++) {
                int finalI = i;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    startCnt.incrementAndGet();
                    logger.info("start {}", finalI);
                    SleepUtil.sleepSecond(TASK_SLEEP_TIME);
                    logger.info("stop {}", finalI);
                    stopCnt.incrementAndGet();
                    return finalI;
                }, executorService);
                taskList.add(task);
            }
            while (executorService.getQueue().size() > 0 || startCnt.get() != stopCnt.get()) {
                logger.info("[executorService] CompletedTaskCount={}, ActiveCount={}" +
                                ", Queue={}, Queue.size={}, TaskCount={}, CorePoolSize={}, PoolSize={}" +
                                ", LargestPoolSize={}, MaximumPoolSize={}, RejectedExecutionHandler={}, startCnt={}, stopCnt={}"
                        , executorService.getCompletedTaskCount()
                        , executorService.getActiveCount()
                        , executorService.getQueue()
                        , executorService.getQueue().size()
                        , executorService.getTaskCount()
                        , executorService.getCorePoolSize()
                        , executorService.getPoolSize()
                        , executorService.getLargestPoolSize()
                        , executorService.getMaximumPoolSize()
                        , executorService.getRejectedExecutionHandler()
                        , startCnt.get()
                        , stopCnt.get()
                );
                SleepUtil.sleepMilliSecond(500);
            }
            logger.info("startCnt={}, stopCnt={}", startCnt.get(), stopCnt.get());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (executorService != null) executorService.shutdown();
        }
    }
}
