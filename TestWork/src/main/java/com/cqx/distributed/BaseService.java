package com.cqx.distributed;

/**
 * 基础服务
 *
 * @author chenqixu
 */
public abstract class BaseService implements Runnable {
    protected volatile boolean isStop = false;

    protected boolean isRun() {
        return !isStop;
    }

    protected void stop() {
        this.isStop = true;
    }
}
