package com.cqx.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BaseRunable
 *
 * @author chenqixu
 */
public abstract class BaseRunable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunable.class);
    private AtomicBoolean runFlag = new AtomicBoolean(true);
    private boolean throwException = false;// 默认不抛异常

    private boolean isStop() {
        return runFlag.get();
    }

    public void stop() {
        runFlag.set(false);
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    @Override
    public void run() {
        logger.debug("{} start.", this);
        while (isStop()) {
            try {
                exec();
            } catch (Exception e) {
                if (throwException) {
                    throw new RuntimeException(e);
                } else {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        try {
            lastExec();
        } catch (Exception e) {
            if (throwException) {
                throw new RuntimeException(e);
            } else {
                logger.error(e.getMessage(), e);
            }
        }
        logger.debug("{} stop.", this);
    }

    public abstract void exec() throws Exception;

    public void lastExec() throws Exception {
    }
}
