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

    private boolean isStop() {
        return runFlag.get();
    }

    public void stop() {
        runFlag.set(false);
    }

    @Override
    public void run() {
        logger.info("{} start.", this);
        while (isStop()) {
            try {
                exec();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            lastExec();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("{} stop.", this);
    }

    public abstract void exec() throws Exception;

    public void lastExec() throws Exception {
    }
}
