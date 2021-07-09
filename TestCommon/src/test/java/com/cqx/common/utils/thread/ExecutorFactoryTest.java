package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ExecutorFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorFactoryTest.class);

    @Test
    public void submit() throws ExecutionException, InterruptedException {
        ExecutorFactory executorFactory = ExecutorFactory.newInstance(2);
        executorFactory.submit(new BaseCallable() {
            Random random = new Random();
            int i = 0;

            @Override
            public void exec() throws Exception {
                i++;
                logger.info("{} {}", this, i);
                if (i > 20) throw new RuntimeException("test");
                SleepUtil.sleepMilliSecond(random.nextInt(100));
            }
        });
        executorFactory.submit(new BaseCallable() {
            Random random = new Random();
            int i = 0;

            @Override
            public void exec() throws Exception {
                i++;
                logger.info("{} {}", this, i);
                SleepUtil.sleepMilliSecond(random.nextInt(100));
            }
        });
        SleepUtil.sleepSecond(2);
        executorFactory.stop();
        executorFactory.join();
    }
}