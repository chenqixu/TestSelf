package com.bussiness.bi.bigdata.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IExecutorsRun
 *
 * @author chenqixu
 */
public abstract class IExecutorsRun<T> {

    private static Logger logger = LoggerFactory.getLogger(IExecutorsRun.class);

    public void run(T t) throws Exception {
    }

    public void run() throws Exception {
    }

    public void isInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {// 任务被取消
            logger.warn("IExecutorsRun： 你取消我干啥？");
            throw new InterruptedException("task is Interrupted.");
        }
    }

}
