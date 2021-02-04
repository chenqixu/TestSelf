package com.cqx.common.utils.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseRunableThread
 *
 * @author chenqixu
 */
public abstract class BaseRunableThread {
    private static final Logger logger = LoggerFactory.getLogger(BaseRunableThread.class);
    private Thread thread;
    private Exec exec;

    public void start() {
        beforeStart();
        if (exec == null) exec = new Exec();
        if (thread == null) thread = new Thread(exec);
        if (thread.getState().equals(Thread.State.NEW)) thread.start();
        else logger.info("thread.state is not NEW，now thread.state：{}", thread.getState());
        afterStart();
    }

    public void stop() {
        beforeStop();
        if (exec != null) exec.stop();
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        afterStop();
    }

    protected abstract void runnableExec() throws Exception;

    protected void runnableLastExec() throws Exception {
    }

    protected void beforeStart() {
    }

    protected void beforeStop() {
    }

    protected void afterStart() {
    }

    protected void afterStop() {
    }

    private class Exec extends BaseRunable {

        @Override
        public void exec() throws Exception {
            runnableExec();
        }

        @Override
        public void lastExec() throws Exception {
            runnableLastExec();
        }
    }
}
