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
}