package com.cqx.common.utils.redis;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.client.RedisPipeline;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 高版本Redis6.2.7
 *
 * @author chenqixu
 */
public class RedisFactory627Test {
    private static final Logger logger = LoggerFactory.getLogger(RedisFactory627Test.class);
    private RedisClient redisClient;
    private ExecutorService executorService = new ThreadPoolExecutor(5
            , 5, 60L, TimeUnit.SECONDS
            , new LinkedBlockingQueue<Runnable>(500)
            , Executors.defaultThreadFactory()
            , new ThreadPoolExecutor.DiscardOldestPolicy());

    @Before
    public void setUp() throws Exception {
        redisClient = RedisFactory.builder()
                .setIp_ports("10.1.8.200:10010,10.1.8.201:10010,10.1.8.202:10010")
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setPipeline(true)
                .setPassword("by7JqR_k")
                .setMax_wait_millis(10000)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        if (redisClient != null) redisClient.close();
        if (executorService != null) {
            logger.info("executorService.shutdown");
            executorService.shutdown();
        }
    }

    /**
     * 认证测试
     */
    @Test
    public void checkAuth() {
        try (Jedis jedis = new Jedis("10.1.8.200", 10010)) {
            String clusterNodes = jedis.clusterNodes();
            logger.info("1.clusterNodes: {}", clusterNodes);
        } catch (JedisDataException e) {
            if (e.getMessage().contains("NOAUTH")) {
                try (Jedis jedis = new Jedis("10.1.8.200", 10010)) {
                    jedis.auth("by7JqR_k");
                    String clusterNodes = jedis.clusterNodes();
                    String[] line = clusterNodes.split("\n", -1);
                    for (String info : line) {
                        if (info.trim().length() > 0) {
                            String[] infos = info.split(" ", -1);
                            if (infos.length > 1) {
                                String[] hostAndPort = infos[1].split("@", -1);
                                logger.info("{}", hostAndPort[0]);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 管道获取
     */
    @Test
    public void pipelineGet() {
        String key = "pos:202301281030:";
        TimeCostUtil tc = new TimeCostUtil();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            tc.start();
            int getCnt = 0;
            for (int i = 0; i < 100000; i++) {
                redisPipeline.request_get(key + i);
                if (i % 500 == 0) {
                    List<Object> rets = redisPipeline.get();
                    getCnt += rets.size();
                }
            }
            // 末尾的get
            List<Object> rets = redisPipeline.get();
            getCnt += rets.size();
            logger.info("getCnt: {}, cost: {} ms", getCnt, tc.stopAndGet());
        }
    }

    /**
     * 管道设置
     */
    @Test
    public void pipelineSet() {
        String key = "pos:202301281030:";
        TimeCostUtil tc = new TimeCostUtil();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            tc.start();
            for (int i = 0; i < 100000; i++) {
                redisPipeline.set(key + i, i + "");
            }
            // 最后剩余强制flush
            redisPipeline.commit();
            logger.info("cost: {} ms", tc.stopAndGet());
        }
    }

    /**
     * 管道删除
     */
    @Test
    public void pipelineDel() {
        String key = "pos:202301281030:";
        TimeCostUtil tc = new TimeCostUtil();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            tc.start();
            for (int i = 0; i < 100000; i++) {
                redisPipeline.del(key + i);
            }
            // 最后剩余强制flush
            redisPipeline.commit();
            logger.info("cost: {} ms", tc.stopAndGet());
        }
    }

    /**
     * 造数据到redis
     */
    @Test
    public void hashModAndGet() {
        List<CompletableFuture<Long>> taskList = new ArrayList<>();
        int allCnt = 20000000;
        int mod = 500;
        int singleCnt = allCnt / mod;
        // %s表示mod
        String key = "pos:202301281030:%s";
        AtomicInteger successCnt = new AtomicInteger();
        for (int index = 1; index <= mod; index++) {
            int endIndex = index * singleCnt;
            int startIndex = endIndex - singleCnt;
            logger.info("[{}-{}]", startIndex, endIndex);
            CompletableFuture<Long> task = CompletableFuture.supplyAsync(() -> {
                TimeCostUtil tc = new TimeCostUtil();
                try (RedisClient _redisClient = RedisFactory.builder()
                        .setIp_ports("10.1.8.200:10010,10.1.8.201:10010,10.1.8.202:10010")
                        .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                        .setPipeline(true)
                        .setPassword("by7JqR_k")
                        .setMax_wait_millis(10000)
                        .build();
                     RedisPipeline redisPipeline = _redisClient.openPipeline()) {
                    tc.start();
                    for (Integer i = startIndex; i < endIndex; i++) {
                        // 计算key的mod
                        int keyMod = i.hashCode() % mod;
                        redisPipeline.hset(String.format(key, keyMod), i + "", "0");
                    }
                    // 最后剩余强制flush
                    redisPipeline.commit();
                    logger.info("[{}-{}] cost: {} ms", startIndex, endIndex, tc.stopAndGet());
                    successCnt.getAndIncrement();
                }
                return tc.stopAndGet();
            }, executorService);
            taskList.add(task);
        }
        CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
        logger.info("successCnt: {}", successCnt.get());
    }

    /**
     * redis数据清理
     */
    @Test
    public void hashModAndClean() {
        String path = "d:\\tmp\\data\\redis\\";
        List<CompletableFuture<Integer>> taskList = new ArrayList<>();
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        try (RedisClient _redisClient = RedisFactory.builder()
                .setIp_ports("10.1.8.200:10010,10.1.8.201:10010,10.1.8.202:10010")
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setPassword("by7JqR_k")
                .setMax_wait_millis(10000)
                .build()) {
            String key = "pos:202301281030:*";
            int keySize = 0;
            AtomicInteger allValueSize = new AtomicInteger();
            for (String _key : _redisClient.keys(key)) {
                keySize++;
                CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
                    boolean isConn = false;
                    while (!isConn) {
                        try (RedisClient __redisClient = RedisFactory.builder()
                                .setIp_ports("10.1.8.200:10010,10.1.8.201:10010,10.1.8.202:10010")
                                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                                .setPassword("by7JqR_k")
                                .setMax_wait_millis(10000)
                                .build()) {
                            FileUtil fu = new FileUtil();
                            try {
                                // 写数据到文件
//                                fu.createFile(path + _key.replaceAll(":", "_"));
                                Map<String, String> allValue = __redisClient.hgetAll(_key);
                                for (Map.Entry<String, String> entry : allValue.entrySet()) {
                                    fu.write(entry.getKey() + "|" + entry.getValue());
                                    fu.newline();
                                }
                                allValueSize.addAndGet(allValue.size());
                                logger.info("key: {}, size: {}", _key, allValue.size());
                                isConn = true;
                                // 从redis删除数据
                                _redisClient.del(_key);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                fu.closeWrite();
                            }
                        } catch (Exception e) {
                            logger.warn(e.getMessage(), e);
                        }
                        if (!isConn) {
                            SleepUtil.sleepSecond(2);
                        }
                    }
                    return 0;
                }, executorService);
                taskList.add(task);
            }
            CompletableFuture.allOf(taskList.toArray(new CompletableFuture[0])).join();
            logger.info("keySize: {}, allValueSize: {}, cost: {} ms", keySize, allValueSize, tc.stopAndGet());
        }
    }

    @Test
    public void pipelineHSet() {
        String key = "h1";
        TimeCostUtil tc = new TimeCostUtil();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            tc.start();
            for (int i = 0; i < 1000000; i++) {
                redisPipeline.hset(key, i + "", i + "");
            }
            // 最后剩余强制flush
            redisPipeline.commit();
            logger.info("cost: {} ms", tc.stopAndGet());
        }
    }

    @Test
    public void pipelineHDel() {
        String key = "h1";
        TimeCostUtil tc = new TimeCostUtil();
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            tc.start();
            for (int i = 0; i < 1000000; i++) {
                redisPipeline.hdel(key, i + "");
            }
            // 最后剩余强制flush
            redisPipeline.commit();
            logger.info("cost: {} ms", tc.stopAndGet());
        }
    }
}
