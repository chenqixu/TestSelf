package com.cqx.common.utils.redis;

import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.client.RedisPipeline;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class RedisFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisFactoryTest.class);
    private RedisClient redisClient;
    private int num = 1000;
    private int add = 1;
    private boolean isShowResult = true;

    @Before
    public void setUp() throws Exception {
        redisClient = RedisFactory.builder()
//                .setIp("10.1.8.200")
//                .setPort(10009)
//                .setMode(RedisFactory.SINGLE_MODE_TYPE)
                .setIp_ports("10.1.8.200:10000,10.1.8.201:10000,10.1.8.202:10000")
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        if (redisClient != null) redisClient.close();
    }

    @Test
    public void linkedTest() {
        //测试
        LinkedBlockingQueue<Integer> getCache = new LinkedBlockingQueue<>(2);
        for (int i = 0; i < 5; i++) {
            boolean result = getCache.offer(i);
            logger.info("i：{}，result：{}", i, result);
        }
        List<Object> copy = new ArrayList<>(getCache);
        getCache.clear();
        logger.info("getCache：{}，copy：{}", getCache, copy);
    }

    @Test
    public void set() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        while (add <= num) {
            redisClient.set(add + "", add++ + "");
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        isShowResult = false;//查询结果不显示
        //查询
        redisQuery();
    }

    @Test
    public void del() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        while (add <= num) {
            redisClient.del(add++ + "");
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        //查询
        redisQuery();
    }

    @Test
    public void get() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        List<String> get = new ArrayList<>();
        while (add <= num) {
            get.add(redisClient.get(add++ + ""));
        }
        logger.info("cost：{}，get：{}", costUtil.stopAndGet(), get);
    }

    @Test
    public void redisQuery() {
        int cnt = 0;
        for (String key : redisClient.keys("*")) {
            if (isShowResult) {
                String type = redisClient.type(key);
                switch (type) {
                    case "none":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "string":
                        logger.info("key：{}，type：{}，value：{}", key, type, redisClient.get(key));
                        break;
                    case "set":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "zset":
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                    case "hash":
                        logger.info("key：{}，type：{}，value：{}", key, type, redisClient.hgetAll(key));
                        break;
                    default:
                        logger.warn("key：{}，type：{}", key, type);
                        break;
                }
            }
            cnt++;
        }
        logger.info("cnt：{}", cnt);
    }

    @Test
    public void pipelineGet() {
        List<Object> objectList;
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline(500, 100)) {
            while (add <= num) {
                redisPipeline.request(add + "");
                if (add % 49 == 0) {
                    objectList = redisPipeline.get();
                    logger.info("add：{}，size：{}，objectList：{}", add, objectList.size(), objectList);
                }
                add++;
            }
            objectList = redisPipeline.get();
            logger.info("size：{}，objectList：{}", objectList.size(), objectList);
        }
        logger.info("cost：{}", costUtil.stopAndGet());
    }

    @Test
    public void pipelineSet() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.set(add + "", add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        isShowResult = false;//查询结果不显示
        //查询
        redisQuery();
    }

    @Test
    public void pipelineDel() {
        TimeCostUtil costUtil = new TimeCostUtil();
        costUtil.start();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            while (add <= num) {
                redisPipeline.del(add++ + "");
            }
        }
        logger.info("cost：{}", costUtil.stopAndGet());
        //查询
        redisQuery();
    }

    @Test
    public void flushDB() {
//        redisClient.flushDB();
    }

    @Test
    public void testServerDown() {
        int max = 5;
        for (int i = 0; i < max; i++) {
            try {
                pipelineGet();
//                get();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                logger.warn("===error , sleep 2");
                SleepUtil.sleepSecond(2);
            }
            if (i != (max - 1)) {
                logger.warn("===wait 3");
                SleepUtil.sleepSecond(3);
            }
            add = 1;
        }
    }
}