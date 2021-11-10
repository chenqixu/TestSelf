package com.cqx.download.yaoqi.work;

/**
 * BaseWork
 *
 * @author chenqixu
 */
public abstract class BaseWork extends Thread {
    private final Object lock = new Object();
    private boolean status = false;

    public abstract void run();

    protected void complete() {
        synchronized (lock) {
            status = true;
        }
    }

    public synchronized boolean isComplete() {
        return status;
    }
}
