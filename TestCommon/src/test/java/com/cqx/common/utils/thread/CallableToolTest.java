package com.cqx.common.utils.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class CallableToolTest {
    private static final Logger logger = LoggerFactory.getLogger(CallableToolTest.class);
    private CallableTool<Integer> callableTool;

    @Before
    public void setUp() throws Exception {
        callableTool = new CallableTool<>(2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void submitCallable() {
        for (int i = 0; i < 5; i++) {
            callableTool.submitCallable(new ICallableTool<Integer>(i + "") {
                @Override
                public Integer icall() throws Exception {
                    int sleep;
                    do {
                        // 正常业务
                        Random random = new Random();
                        sleep = random.nextInt(3000);
                        logger.info("sleep：{}", sleep);
//                    if (sleep > 600) {
//                        logger.warn("throw Exception.");
//                        throw new NullPointerException("test" + sleep);
//                    }
                        Thread.sleep(sleep);
                    } while (sleep < 2900);
                    return sleep;
                }
            });
        }
        callableTool.await();
        callableTool.stop();
    }
}