package com.bussiness.bi.bigdata.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyAwait
 *
 * @author chenqixu
 */
public class MyAwait {

    private static Logger logger = LoggerFactory.getLogger(MyAwait.class);
    private final Object lock = new Object();
    protected volatile boolean pause = false;

    /**
     * 调用该方法实现线程的暂停
     */
    public void pauseThread() {
        pause = true;
    }

    /**
     * 调用该方法实现恢复线程的运行
     */
    public void resumeThread() {
        pause = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
     */
    protected void onPause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
