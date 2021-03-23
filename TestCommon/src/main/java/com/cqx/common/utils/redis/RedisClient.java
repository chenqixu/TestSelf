package com.cqx.common.utils.redis;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Map;

public interface RedisClient {
    int DEFUALT_MAX_IDLE = 10;
    int DEFUALT_MAX_TOTAL = 100;
    int DEFUALT_MAX_WAIT_MILLIS = 3000;

    String set(String key, String value);

    boolean setnx(String key, String value);

    boolean setnx(String key, String value, Integer seconds);

    String get(String key);

    Long del(String key);

    Long hdel(String key, String field);

    Long hset(String key, String field, String value);

    Long hsetnx(String key, String field, String value);

    String hget(String key, String field);

    Map<String, String> hgetAll(String key);

    ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

    ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params);

    void close();

    interface RedisPipeline {
        void open();

        void set(String key, String value);

        void sync();
    }
}
