package com.cqx.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BaseCallableV1
 *
 * @author chenqixu
 */
public abstract class BaseCallableV1 implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(BaseCallableV1.class);
    private AtomicBoolean runFlag = new AtomicBoolean(true);

    public boolean isStop() {
        return runFlag.get();
    }

    public void stop() {
        runFlag.set(false);
    }

    @Override
    public Integer call() throws Exception {
        logger.debug("{} start.", this);
        while (isStop()) {
            try {
                exec();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
        try {
            lastExec();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        logger.debug("{} stop.", this);
        return 0;
    }

    public abstract void exec() throws Exception;

    public void lastExec() throws Exception {
    }
}
