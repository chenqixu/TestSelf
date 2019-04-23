package com.cqx.pool.thread;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyExecutorTest {

    private static Logger logger = LoggerFactory.getLogger(MyExecutorTest.class);
    private MyExecutor myExecutor;
    private int bound = 1000;
    private int parallel_num = 3;
    private volatile boolean flag = false;

    @Before
    public void setUp() {
        myExecutor = new MyExecutor(parallel_num);
    }

    @Test
    public void startRunnable() throws InterruptedException {
        myExecutor.startDeal();
        myExecutor.init(bound);
        myExecutor.startRunnable();
        Thread.sleep(3000);
        logger.info("cost：{}", myExecutor.endDeal());
    }

    @Test
    public void startCallable() throws InterruptedException {
        myExecutor.startDeal();
        myExecutor.init(bound);
        myExecutor.startCallable();
        logger.info("cost：{}", myExecutor.endDeal());
    }

    private boolean isEnd() {
        return flag;
    }

    private void setEnd() {
        flag = true;
    }

    @Test
    public void testLoop() throws Exception {
        ICostUtil runICostUtil = myExecutor.buildTimeCostUtil();
        runICostUtil.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setEnd();
            }
        }).start();
        // 是否有退出文件
        while (!isEnd()) {
            Thread.sleep(1000);
            if (runICostUtil.tag(1000)) {
                logger.info("run……");
            }
            logger.info("null run……");
        }
        runICostUtil.end();
    }
}