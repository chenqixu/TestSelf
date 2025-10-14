package com.cqx.common.utils.redis.client;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RedisClient implements Closeable {
    protected static final int DEFAULT_MAX_REDIRECTIONS = 5;
    protected static final int DEFAULT_PIPELINE_COMMIT = 500;
    protected static final int DEFAULT_GET_CHCHE_SIZE = 10000;
    protected static final int DEFAULT_MAX_IDLE = 8;
    protected static final int DEFAULT_MAX_TOTAL = 8;
    public static final int DEFAULT_MAX_WAIT_MILLIS = 2000;
    protected RedisPipeline redisPipeline;
    protected boolean isPipeline = true;

    public abstract Set<String> keys(String pattern);

    public abstract String type(String key);

    public abstract String hmset(String key, Map<String, String> parmas);

    public abstract String set(String key, String value);

    public abstract String set(byte[] key, byte[] value);

    public abstract boolean setnx(String key, String value);

    public abstract boolean setnx(String key, String value, Integer seconds);

    public abstract String setex(String key, int seconds, String value);

    public abstract Long expire(String key, int seconds);

    public abstract String get(String key);

    public abstract byte[] get(byte[] key);

    public abstract Long del(String key);

    public abstract void scan(ScanParams params, IScan iScan);

    public abstract Long hdel(String key, String field);

    public abstract Long hset(String key, String field, String value);

    public abstract Long hsetnx(String key, String field, String value);

    public abstract String hget(String key, String field);

    public abstract Map<String, String> hgetAll(String key);

    public abstract ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

    public abstract ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params);

    public abstract Long hlen(String key);

    public abstract List<String> mget(String... keys);

    public abstract List<String> hmget(String key, String... fields);

    public abstract Long incr(String key);

    public abstract Long hincrBy(String key, String field, Long value);

    public abstract Set<String> smembers(String key);

    public abstract Object eval(String script, int keyCount, String... params);

    public abstract Object eval(String script, List<String> keys, List<String> args);

    /**
     * 集群版本才有key
     *
     * @param script
     * @param key
     * @return
     */
    public abstract Object eval(String script, String key);

    /**
     * 集群版本才有key
     *
     * @param script
     * @param key
     * @return
     */
    public abstract Object evalsha(String script, String key);

    public abstract Object evalsha(String sha1, List<String> keys, List<String> args);

    public abstract Object evalsha(String sha1, int keyCount, String... params);

    /**
     * 集群版本才有key
     *
     * @param sha1
     * @param key
     * @return
     */
    public abstract Boolean scriptExists(String sha1, String key);

    /**
     * 集群版本才有key
     *
     * @param key
     * @param sha1
     * @return
     */
    public abstract List<Boolean> scriptExists(String key, String... sha1);

    /**
     * 集群版本才有key
     *
     * @param script
     * @param key
     * @return
     */
    public abstract String scriptLoad(String script, String key);

    /**
     * 增加其他批量操作方法-mset String、msetnx String、mget byte[]、mset byte[]、msetnx byte[]
     */
    public abstract String mset(String... keysvalues);

    public abstract Long msetnx(String... keysvalues);

    public abstract List<byte[]> mget(byte[]... keys);

    public abstract String mset(byte[]... keysvalues);

    public abstract Long msetnx(byte[]... keysvalues);

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

    public interface IScan {
        void scanGet(ScanResult<String> resp);
    }
}
