package com.cqx.common.utils.redis;

import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.client.RedisPipeline;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * 高版本Redis6.2.7
 *
 * @author chenqixu
 */
public class RedisFactory627Test {
    private static final Logger logger = LoggerFactory.getLogger(RedisFactory627Test.class);
    private RedisClient redisClient;

    @Before
    public void setUp() throws Exception {
        redisClient = RedisFactory.builder()
                .setIp_ports("10.1.8.200:10010,10.1.8.201:10010,10.1.8.202:10010")
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setPipeline(true)
                .setPassword("by7JqR_k")
                .build();
    }

    @After
    public void tearDown() throws Exception {
        if (redisClient != null) redisClient.close();
    }

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

    @Test
    public void pipelineGet() {
        try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
            redisPipeline.request_get("1");
        }
    }
}
