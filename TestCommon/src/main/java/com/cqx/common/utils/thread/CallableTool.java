package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * CallableTool
 *
 * @author chenqixu
 */
public class CallableTool<T> {
    private static final Logger logger = LoggerFactory.getLogger(CallableTool.class);
    private ExecutorService executor;
    // 所有正在执行的任务
    private Map<Future<T>, ICallableTool<T>> iCallableToolMap = new ConcurrentHashMap<>();
    // 存放结果的队列
    private BlockingQueue<T> resultQueue = new LinkedBlockingQueue<>();

    public CallableTool(int parallel_num) {
        executor = Executors.newFixedThreadPool(parallel_num);
    }

    /**
     * 提交任务
     *
     * @param callable
     */
    public void submitCallable(ICallableTool<T> callable) {
        Future<T> future = executor.submit(callable);
        iCallableToolMap.put(future, callable);
        logger.info("提交任务：{}，当前待运行任务数：{}", callable.getTaskName(), iCallableToolMap.size());
    }

    /**
     * 等待所有任务完成
     */
    public void await() {
        do {
            Iterator<Map.Entry<Future<T>, ICallableTool<T>>> it = iCallableToolMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Future<T>, ICallableTool<T>> entry = it.next();
                Future<T> future = entry.getKey();
                ICallableTool<T> iCallableTool = entry.getValue();
                if (future.isDone()) {
                    T t = null;
                    try {
                        // 获取运行结果
                        t = future.get();
                        // 结果放入结果队列
                        resultQueue.put(t);
                        logger.info("任务{}执行完成，结果={}", iCallableTool.getTaskName(), t);
                    } catch (InterruptedException | ExecutionException | CancellationException e) {
                        if (CancellationException.class.isAssignableFrom(e.getClass())) {
                            logger.error("任务{}异常，未执行完成，异常信息={}，重新提交"
                                    , iCallableTool.getTaskName(), e.getMessage());
                        } else {
                            // 中止当前任务，并重做
                            logger.error(String.format("%s任务有问题，捕获到未知异常%s，强制取消任务"
                                    , iCallableTool.getTaskName(), e.getMessage()), e);
                            // 如果在运行，就强制中断
                            future.cancel(true);
                            // 休眠一会
                            SleepUtil.sleepSecond(5);
                        }
                        // 需要先移除异常，再重新提交
                        it.remove();
                        it = iCallableToolMap.entrySet().iterator();
                        // 重置，让任务的状态变成未运行，错误计数器为0
                        iCallableTool.reset();
                        // 被取消的任务重新提交
                        submitCallable(iCallableTool);
                    }
                    // 任务正常运行完成，无异常，需要从监控列表中移除
                    if (t != null) {
                        it.remove();
                        it = iCallableToolMap.entrySet().iterator();
                        logger.info("当前剩余任务数：{}", iCallableToolMap.size());
                    }
                } else if (future.isCancelled()) {
                    // 如果是调用cancel，不会走到这里，因为同时也done了
                    logger.warn("任务{}被取消", iCallableTool.getTaskName());
                } else {
                    // 心跳检测异常
                    if (iCallableTool.isRun() && iCallableTool.heartIsError()) {
                        // 中止当前任务，并重做
                        logger.warn("{}任务有问题，心跳异常，取消任务", iCallableTool.getTaskName());
                        // 如果在运行，就强制中断
                        future.cancel(true);
                    }
                    SleepUtil.sleepMilliSecond(500);
                }
            }
        } while (iCallableToolMap.size() != 0);
    }

    public void stop() {
        executor.shutdown();
        // shutdown并不会join到主线程中
        while (!executor.isTerminated()) {
            SleepUtil.sleepMilliSecond(50);
        }
        logger.info("停止并发线程池");
    }

    /**
     * 消费结果
     *
     * @return
     */
    public T pollResult() {
        return resultQueue.poll();
    }
}
