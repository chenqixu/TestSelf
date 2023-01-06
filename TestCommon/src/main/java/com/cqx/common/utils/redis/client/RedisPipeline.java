package com.cqx.common.utils.redis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <h2>redis管道工具类</h2>
 * <p>
 * <h3>示范：</h3>
 * <pre>
 *     // 构造
 *     RedisClient redisClient = RedisFactory.builder()
 *         // 设置ip:端口
 *         .setIp_ports("10.1.8.200:10000,10.1.8.201:10000,10.1.8.202:10000")
 *         // 设置集群模式
 *         .setMode(RedisFactory.CLUSTER_MODE_TYPE)
 *         // 主动开启管道
 *         .setPipeline(true)
 *         .build();
 *
 *     // 默认是500条记录提交一次
 *     try (RedisPipeline redisPipeline = redisClient.openPipeline()) {
 *         循环 {
 *             redisPipeline.setex(key, seconds, value);
 *         }
 *     }
 *     // 或者手工指定，值不宜太大，可能导致性能下降或数据丢失
 *     // 第二个参数是get的缓存个数
 *     try (RedisPipeline redisPipeline = redisClient.openPipeline(1000, 5000))
 * </pre>
 *
 * @author chenqixu
 */
public abstract class RedisPipeline implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RedisPipeline.class);
    private int commit_num;
    private int commit_get_num;
    private int max_attempts;
    private Map<String, PipelineCount> countMap = new HashMap<>();
    private LinkedBlockingQueue<Object> getCache;

    public RedisPipeline(int commit_num, int get_cache_num) {
        this.commit_num = commit_num;
        this.commit_get_num = (commit_num > get_cache_num) ? get_cache_num : commit_num;
        this.getCache = new LinkedBlockingQueue<>(get_cache_num);
        this.max_attempts = RedisClient.DEFAULT_MAX_REDIRECTIONS;
    }

    protected abstract void open();

    /**
     * 重置连接，当某个master异常的时候能够重新分配<br>
     * 单点不需要重载，集群必须重载
     */
    protected void renewCache() {
    }

    protected abstract void sync();

    protected abstract List<Object> syncAndReturnAll();

    protected abstract void set_inside(String key, String value);

    public void set(String key, String value) {
        set_inside(key, value);
        autoCommit("set");
    }

    protected abstract void setex_inside(String key, int seconds, String value);

    public void setex(String key, int seconds, String value) {
        setex_inside(key, seconds, value);
        autoCommit("setex");
    }

    protected abstract void del_inside(String key);

    public void del(String key) {
        del_inside(key);
        autoCommit("del");
    }

    protected abstract void request_get_inside(String key);

    public void request_get(String key) {
        request_get_inside(key);
        autoCommitAndGet(false);
    }

    public List<Object> get() {
        if (getCache.size() == 0) autoCommitAndGet(true);
        List<Object> copy = new ArrayList<>(getCache);
        getCache.clear();
        return copy;
    }

    protected abstract void hset_inside(String key, String field, String value);

    public void hset(String key, String field, String value) {
        hset_inside(key, field, value);
        autoCommit("hset");
    }

    protected abstract void hdel_inside(String key, String field);

    public void hdel(String key, String field) {
        hdel_inside(key, field);
        autoCommit("hdel");
    }

    protected abstract void request_hget_inside(String key, String field);

    public void request_hget(String key, String field) {
        request_hget_inside(key, field);
        autoCommitAndGet(false);
    }

    private void autoCommit(String key) {
        PipelineCount pipelineCount = getPipelineCount(key);
        if (pipelineCount.incrementAndGet() % commit_num == 0) {
            commit(max_attempts);
            pipelineCount.clear();
        }
    }

    private void autoCommitAndGet(boolean flush) {
        PipelineCount pipelineCount = getPipelineCount("get");
        if (flush) {
            flushGetCache(max_attempts);// 刷数据到getCache
            pipelineCount.clear();
        } else if (pipelineCount.incrementAndGet() % commit_get_num == 0) {
            flushGetCache(max_attempts);// 刷数据到getCache
            pipelineCount.clear();
        }
    }

    /**
     * 刷数据到getCache
     */
    private void flushGetCache(int attempts) {
        try {
            for (Object obj : syncAndReturnAll()) {
                //达到队列上限会抛数据
                if (obj != null) getCache.offer(obj);
            }
        } catch (JedisConnectionException connException) {
            logger.warn("attempts：{}，connException：{}", attempts, connException.getMessage());
            //连接异常，可能是master节点挂了，需要重新分配

            if (attempts <= 1) {
                //release current connection before recursion
                logger.warn("attempts：{}，releasePipeline……", attempts);
                releasePipeline();

                //We need this because if node is not reachable anymore - we need to finally initiate slots renewing,
                //or we can stuck with cluster state without one node in opposite case.
                logger.warn("attempts：{}，renewCache……", attempts);
                renewCache();
                throw connException;
            }
            //递归，直到没有JedisConnectionException异常，或者
            flushGetCache(attempts - 1);
        }
    }

    /**
     * 同步
     */
    public void commit() {
        commit(max_attempts);
    }

    /**
     * 同步
     *
     * @param attempts
     */
    private void commit(int attempts) {
        try {
            sync();
        } catch (JedisConnectionException connException) {
            logger.warn("attempts：{}，connException：{}", attempts, connException.getMessage());
            //连接异常，可能是master节点挂了，需要重新分配

            if (attempts <= 1) {
                //release current connection before recursion
                logger.warn("attempts：{}，releasePipeline……", attempts);
                releasePipeline();

                //We need this because if node is not reachable anymore - we need to finally initiate slots renewing,
                //or we can stuck with cluster state without one node in opposite case.
                logger.warn("attempts：{}，renewCache……", attempts);
                renewCache();
                throw connException;
            }
            //递归，直到没有JedisConnectionException异常，或者
            commit(attempts - 1);
        }
    }

    protected abstract void releasePipeline();

    @Override
    public void close() {
        commit(max_attempts);
    }

    private PipelineCount getPipelineCount(String key) {
        PipelineCount pipelineCount = countMap.get(key);
        if (pipelineCount == null) {
            pipelineCount = new PipelineCount();
            countMap.put(key, pipelineCount);
        }
        return pipelineCount;
    }

    class PipelineCount {
        int count;

        int get() {
            return count;
        }

        void increment() {
            count++;
        }

        int incrementAndGet() {
            return ++count;
        }

        void clear() {
            count = 0;
        }
    }
}