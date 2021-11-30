package com.cqx.common.utils.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ICallableTool
 *
 * @author chenqixu
 */
public abstract class ICallableTool<V> implements Callable {
    private long lastHeartTime = System.currentTimeMillis();
    private AtomicInteger errorNum = new AtomicInteger(0);
    private AtomicBoolean isRun = new AtomicBoolean(false);
    private String taskName;

    public ICallableTool(String taskName) {
        this.taskName = taskName;
    }

    /**
     * 心跳
     */
    public void heartbeat() {
        lastHeartTime = System.currentTimeMillis();
    }

    /**
     * 判断心跳是否异常
     *
     * @return
     */
    public boolean heartIsError() {
        // 超过300秒没有调用心跳
        boolean flag = (System.currentTimeMillis() - lastHeartTime) > (300 * 1000);
        // 没有调用心跳，计数器加1
        if (flag) errorNum.incrementAndGet();
        // 两次及两次以上心跳异常
        return errorNum.get() > 1;
    }

    @Override
    public V call() throws Exception {
        // 参数重置
        reset();
        // 运行状态设为true
        isRun.set(true);
        // 调用业务
        return icall();
    }

    /**
     * 需要实现的业务
     *
     * @return
     * @throws Exception
     */
    public abstract V icall() throws Exception;

    /**
     * 判断任务是否运行
     *
     * @return
     */
    public boolean isRun() {
        return isRun.get();
    }

    /**
     * 重置任务状态，运行为false，错误计数为0，更新心跳
     */
    public void reset() {
        isRun.set(false);
        errorNum.set(0);
        heartbeat();
    }

    /**
     * 获取任务名称
     *
     * @return
     */
    public String getTaskName() {
        return taskName + "-" + toString();
    }
}
