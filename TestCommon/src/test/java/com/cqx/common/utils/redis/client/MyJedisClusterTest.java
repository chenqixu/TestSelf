package com.cqx.common.utils.redis.client;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.util.JedisClusterCRC16;

public class MyJedisClusterTest {
    private static final Logger logger = LoggerFactory.getLogger(MyJedisClusterTest.class);

    @Test
    public void getSlot() {
        for (int i = 0; i <= 16383; i++) {
            int slot = JedisClusterCRC16.getSlot(i + "");
            if (slot >= 13653) {
                logger.info("slot：{}，key：{}", slot, i);
                break;
            }
        }
    }
}