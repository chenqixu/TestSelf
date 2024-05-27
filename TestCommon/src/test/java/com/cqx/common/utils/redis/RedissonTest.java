package com.cqx.common.utils.redis;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseCallableV1;
import com.cqx.common.utils.thread.ExecutorFactoryV1;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Redisson
 *
 * @author chenqixu
 */
public class RedissonTest {
    private static final Logger logger = LoggerFactory.getLogger(RedissonTest.class);
    private ExecutorFactoryV1 executorFactory;

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        Random rd = new Random(System.currentTimeMillis());
        int pallelNum = 5;
        this.executorFactory = ExecutorFactoryV1.newInstance(pallelNum);
        for (int i = 0; i < pallelNum; i++) {
            this.executorFactory.submit(new RMServer(rd.nextInt(10000)));
        }
        this.executorFactory.join();
        this.executorFactory.stop();
    }

    @Test
    public void test2() {
        logger.info("test2");
    }

    public class RMServer extends BaseCallableV1 {
        private int rdSleep;

        public RMServer(int rdSleep) {
            this.rdSleep = rdSleep;
        }

        @Override
        public void exec() throws Exception {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://192.168.65.128:6379");

            try {
                RedissonClient client = Redisson.create(config);
                RLock lock = client.getLock("lock1");

                try {
                    lock.lock();
                    logger.info("[{}]抢到锁, 休眠{}毫秒", this, rdSleep);
                    SleepUtil.sleepMilliSecond(rdSleep);
                } finally {
                    lock.unlock();
                    logger.info("[{}]解锁", this);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            stop();
        }
    }
}
