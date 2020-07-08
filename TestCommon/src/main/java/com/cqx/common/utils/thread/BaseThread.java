package com.cqx.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseThread
 * <pre>
 *     如果调用等待，在有异常的情况下，支持自动重做
 * </pre>
 *
 * @author chenqixu
 */
public abstract class BaseThread implements Runnable {

    /**
     * 共用日志，使用getClass()保证都是当前类输出
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Thread thread;
    private Thread.State state = Thread.State.NEW;
    private boolean isError = false;//默认没有错误，不需要重跑
    private String errorMsg = "";
    private int errorRetryCnt = 0;
    private int errorRetryMax = 3;

    /**
     * 任务逻辑
     */
    public abstract void run();

    /**
     * 启动任务
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            logger.info("First run. Thread（{}）.", thread);
        } else {
            if (getState().equals(Thread.State.TERMINATED)) {
                Thread old = thread;
                thread = new Thread(this);
                thread.start();
                logger.info("The previous Thread（{}） is End，Now start running the new one（{}）.",
                        old, thread);
            } else {
                logger.info("Thread（{}） State is：{}，Not allowed to run.", thread, getState());
            }
        }
    }

    /**
     * 重做任务
     */
    public void reStart() {
        if (isError && thread != null
                && getState().equals(Thread.State.TERMINATED)
                && errorRetryCnt < errorRetryMax) {
            logger.info("Thread（{}）has some error（{}），now get ready to run again.", thread, getErrorMsg());
            resetError();//重置错误信息
            start();
        } else {
            if (!isError) logger.info("Can't run again. Because there are no mistakes.");
            else if (thread == null) logger.info("Can't run again. Because the thread is not running yet.");
            else if (!getState().equals(Thread.State.TERMINATED))
                logger.info("Can't run again. Because the thread is not finished.");
            else if (errorRetryCnt >= errorRetryMax)
                logger.info("Can't run again. The current number of retries（{}） is greater than or equal to the maximum threshold（{}）.", errorRetryCnt, errorRetryMax);
            else logger.info("Can't run again. For other reasons.");
        }
    }

    /**
     * 等待任务完成并自动重试，一直到没有错误
     *
     * @throws InterruptedException
     */
    public void waitForAndReStart() throws InterruptedException {
        if (thread != null) {
            if (!getState().equals(Thread.State.TERMINATED)) {
                thread.join();
                if (isError()) {
                    reStart();
                    waitForAndReStart();
                }
            }
        }
    }

    /**
     * 等待任务完成
     *
     * @throws InterruptedException
     */
    public void waitFor() throws InterruptedException {
        if (thread != null) {
            if (!getState().equals(Thread.State.TERMINATED)) {
                thread.join();
            }
        }
    }

    /**
     * 获取任务线程状态
     *
     * @return
     */
    public Thread.State getState() {
        if (thread != null) state = thread.getState();
        return state;
    }

    /**
     * 任务线程是否完成
     *
     * @return
     */
    public boolean isTerminated() {
        return getState().equals(Thread.State.TERMINATED);
    }

    /**
     * 任务是否有错
     *
     * @return
     */
    public boolean isError() {
        return isError;
    }

    /**
     * 设置任务错误信息
     *
     * @param errorMsg
     */
    public void setError(String errorMsg) {
        this.isError = true;
        this.errorMsg = errorMsg;
    }

    /**
     * 获取任务错误说明
     *
     * @return
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 重置任务错误信息
     */
    public void resetError() {
        isError = false;
        errorMsg = "";
        errorRetryCnt++;
    }

    /**
     * 设置最大重试次数
     *
     * @param errorRetryMax
     */
    public void setErrorRetryMax(int errorRetryMax) {
        this.errorRetryMax = errorRetryMax;
    }
}
