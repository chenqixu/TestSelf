package com.newland.bi.bigdata.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Map;

public class SinaleRedisClient implements RedisClient {
    private Jedis jedis;

    public SinaleRedisClient(RedisFactory.Builder builder) {
        jedis = new Jedis(builder.getIp(), builder.getPort());
    }

    @Override
    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    public boolean setnx(String key, String value) {
        return setnx(key, value, null);
    }

    @Override
    public boolean setnx(String key, String value, Integer seconds) {
        long ret = jedis.setnx(key, value);
        System.out.println("[setnx ret]" + ret);
        if (ret == 0) {// 原先逻辑没有设置成功，就把记录失效seconds
//		if(ret == 1 && seconds != null){// 我的逻辑，如果设置成功，就把记录失效seconds
            long eret = jedis.expire(key, seconds);
            System.out.println("[expire ret]" + eret);
        }
        return ret == 1;
    }

    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    @Override
    public Long del(String key) {
        return jedis.del(key);
    }

    @Override
    public Long hdel(String key, String field) {
        return jedis.hdel(key, field);
    }

    @Override
    public Long hset(String key, String field, String value) {
        return jedis.hset(key, field, value);
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return jedis.hsetnx(key, field, value);
    }

    @Override
    public String hget(String key, String field) {
        return jedis.hget(key, field);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return jedis.hscan(key, cursor);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return jedis.hscan(key, cursor, params);
    }

    @Override
    public void close() {
        jedis.close();
    }
}
