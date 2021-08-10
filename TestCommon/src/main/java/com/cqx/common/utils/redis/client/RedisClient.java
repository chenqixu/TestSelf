package com.cqx.common.utils.redis.client;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RedisClient {
    protected static final int DEFAULT_MAX_REDIRECTIONS = 5;
    protected static final int DEFAULT_PIPELINE_COMMIT = 500;
    protected static final int DEFAULT_GET_CHCHE_SIZE = 10000;
    protected static final int DEFAULT_MAX_IDLE = 8;
    protected static final int DEFAULT_MAX_TOTAL = 8;
    protected static final int DEFAULT_MAX_WAIT_MILLIS = 2000;
    protected RedisPipeline redisPipeline;
    protected boolean isPipeline = true;

    public abstract Set<String> keys(String pattern);

    public abstract String type(String key);

    public abstract String set(String key, String value);

    public abstract boolean setnx(String key, String value);

    public abstract boolean setnx(String key, String value, Integer seconds);

    public abstract String get(String key);

    public abstract Long del(String key);

    public abstract Long hdel(String key, String field);

    public abstract Long hset(String key, String field, String value);

    public abstract Long hsetnx(String key, String field, String value);

    public abstract String hget(String key, String field);

    public abstract Map<String, String> hgetAll(String key);

    public abstract ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

    public abstract ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params);

    public abstract List<String> mget(String... keys);

    public abstract List<String> hmget(String key, String... fields);

    public abstract void close();

    public abstract void flushDB();

    protected abstract RedisPipeline generatePipeline(int commit_num, int get_cache_num);

    public RedisPipeline openPipeline() {
        return openPipeline(DEFAULT_PIPELINE_COMMIT, DEFAULT_GET_CHCHE_SIZE);
    }

    public RedisPipeline openPipeline(int commit_num) {
        return openPipeline(commit_num, DEFAULT_GET_CHCHE_SIZE);
    }

    public RedisPipeline openPipeline(int commit_num, int get_cache_num) {
        if (redisPipeline == null) redisPipeline = generatePipeline(commit_num, get_cache_num);
        if (redisPipeline != null) redisPipeline.open();
        return redisPipeline;
    }

    public boolean isPipeline() {
        return isPipeline;
    }

    /**
     * 是否支持管道
     *
     * @param pipeline
     */
    public void setPipeline(boolean pipeline) {
        this.isPipeline = isPipeline;
    }
}
