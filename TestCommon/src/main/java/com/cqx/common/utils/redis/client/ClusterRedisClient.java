package com.cqx.common.utils.redis.client;

import com.cqx.common.utils.redis.RedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterRedisClient extends RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(ClusterRedisClient.class);
    private JedisCluster cluster;
    private Set<HostAndPort> HostAndPort_set = new HashSet<>();

    public ClusterRedisClient(RedisFactory.Builder builder) {
        addHostAndPort(builder);
        if (builder.isPipeline()) {
            cluster = new MyJedisCluster(HostAndPort_set);
            setPipeline(true);
        } else {
            cluster = new JedisCluster(HostAndPort_set);
        }
    }

    private void addHostAndPort(RedisFactory.Builder builder) {
        if (builder.getIp_ports() != null && builder.getIp_ports().trim().length() > 0) {
            for (String str : builder.getIp_ports().split(",")) {
                String[] arr = str.split(":");
                if (arr != null && arr.length == 2) {
                    String ip = arr[0];
                    int port = Integer.valueOf(arr[1]);
                    HostAndPort_set.add(new HostAndPort(ip, port));
                }
            }
        }
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return cluster.hscan(key, cursor);
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return cluster.hscan(key, cursor, params);
    }

    @Override
    public List<String> mget(String... keys) {
        return cluster.mget(keys);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return cluster.hmget(key, fields);
    }

    @Override
    public Set<String> keys(String pattern) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, JedisPool> entry : cluster.getClusterNodes().entrySet()) {
            Jedis jedis = entry.getValue().getResource();
            result.addAll(jedis.keys(pattern));
            releaseConnection(jedis);
        }
        return result;
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of "none",
     * "string", "list", "set". "none" is returned if the key does not exist. Time complexity: O(1)
     *
     * @param key
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
     * contains a String value "list" if the key contains a List value "set" if the key
     * contains a Set value "zset" if the key contains a Sorted Set value "hash" if the key
     * contains a Hash value
     */
    @Override
    public String type(String key) {
        return cluster.type(key);
    }

    @Override
    public String set(String key, String value) {
        return cluster.set(key, value);
    }

    @Override
    public boolean setnx(String key, String value) {
        return false;
    }

    @Override
    public boolean setnx(String key, String value, Integer seconds) {
        return false;
    }

    @Override
    public String get(String key) {
        return cluster.get(key);
    }

    @Override
    public Long del(String key) {
        return cluster.del(key);
    }

    @Override
    public Long hdel(String key, String field) {
        return cluster.hdel(key, field);
    }

    @Override
    public Long hset(String key, String field, String value) {
        return cluster.hset(key, field, value);
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return cluster.hsetnx(key, field, value);
    }

    @Override
    public String hget(String key, String field) {
        return cluster.hget(key, field);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return cluster.hgetAll(key);
    }

    @Override
    public Long incr(String key) {
        return cluster.incr(key);
    }

    @Override
    public Long hincrBy(String key, String field, Long value) {
        return cluster.hincrBy(key, field, value);
    }

    @Override
    public void flushDB() {
        for (Map.Entry<String, JedisPool> entry : cluster.getClusterNodes().entrySet()) {
            entry.getValue().getResource().flushDB();
        }
    }

    @Override
    protected RedisPipeline generatePipeline(int commit_num, int get_cache_num) {
        return new ClusterRedisPipeline(commit_num, get_cache_num);
    }

    @Override
    public void close() {
        // 释放所有为pipeline分配的Jedis连接
        if (isPipeline()) {
            if (cluster instanceof MyJedisCluster) {
                ((MyJedisCluster) cluster).reset();
            }
        }
        // 调用JedisCluster自身的close
        try {
            cluster.close();
        } catch (IOException e) {
            logger.error("close异常：" + e.getMessage(), e);
        }
    }

    private void releaseConnection(Jedis connection) {
        if (connection != null) {
            connection.close();
        }
    }

    class ClusterRedisPipeline extends RedisPipeline {
        Map<String, Pipeline> pipelinePool = new HashMap<>();
        AtomicInteger syncCount = new AtomicInteger(0);
        AtomicInteger syncAndReturnCount = new AtomicInteger(0);

        public ClusterRedisPipeline(int commit_num, int get_cache_num) {
            super(commit_num, get_cache_num);
        }

        private Pipeline getPipeline(String key) {
            if (isPipeline()) {
                if (cluster instanceof MyJedisCluster) {
                    MyJedisCluster myJedisCluster = (MyJedisCluster) cluster;
                    return myJedisCluster.callBack(new MyJedisCluster.MyJedisClusterCallBack<Pipeline>() {
                        @Override
                        public Pipeline call() {
                            int slot = myJedisCluster.getSlot(key);
                            String node = myJedisCluster.getNodeBySlot(slot);
                            Pipeline pipeline = pipelinePool.get(node);
                            if (pipeline == null) {
                                Jedis jedis = myJedisCluster.getConnectionFromSlot(slot);
                                if (jedis != null) {
                                    pipeline = jedis.pipelined();
                                    pipelinePool.put(node, pipeline);
                                    logger.debug("pipelinePool.put：{}", node);
                                } else {
                                    throw new NullPointerException("slot : " + slot + ", node：" + node + "，jedis is null !");
                                }
                            }
                            return pipeline;
                        }
                    });
                } else {
                    throw new UnsupportedOperationException("客户端初始化异常，非自定义集群！" + cluster.getClass());
                }
            } else {
                return null;
            }
        }

        @Override
        protected void open() {
            //null
        }

        @Override
        protected void renewCache() {
            if (isPipeline()) {
                if (cluster instanceof MyJedisCluster) {
                    ((MyJedisCluster) cluster).renewCache();
                }
            }
        }

        @Override
        protected void sync() throws JedisConnectionException {
            syncCount.set(0);
            int i = 0;
            for (Pipeline pipeline : pipelinePool.values()) {
                i++;
                try {
                    pipeline.sync();
                } catch (JedisConnectionException connectionException) {
                    syncCount.set(i);
                    throw connectionException;
                }
            }
        }

        @Override
        protected List<Object> syncAndReturnAll() throws JedisConnectionException {
            syncAndReturnCount.set(0);
            List<Object> objectList = new ArrayList<>();
            int i = 0;
            for (Pipeline pipeline : pipelinePool.values()) {
                i++;
                try {
                    objectList.addAll(pipeline.syncAndReturnAll());
                } catch (JedisConnectionException connectionException) {
                    syncAndReturnCount.set(i);
                    throw connectionException;
                }
            }
            return objectList;
        }

        @Override
        protected void set_inside(String key, String value) {
            if (isPipeline()) getPipeline(key).set(key, value);
        }

        @Override
        protected void del_inside(String key) {
            if (isPipeline()) getPipeline(key).del(key);
        }

        @Override
        protected void request_get_inside(String key) {
            if (isPipeline()) getPipeline(key).get(key);
        }

        @Override
        protected void hset_inside(String key, String field, String value) {
            if (isPipeline()) getPipeline(key).hset(key, field, value);
        }

        @Override
        protected void hdel_inside(String key, String field) {
            if (isPipeline()) getPipeline(key).hdel(key, field);
        }

        @Override
        protected void request_hget_inside(String key, String field) {
            if (isPipeline()) getPipeline(key).hget(key, field);
        }

        @Override
        protected void releasePipeline() {
            int sarc = syncAndReturnCount.get();
            int sc = syncCount.get();
            logger.debug("releasePipeline.count sarc：{}，sc：{}", sarc, sc);
            int i = 0;
            for (Pipeline pipeline : pipelinePool.values()) {
                i++;
                if ((sarc > 0 && i == sarc) || (sc > 0 && i == sc)) {
                    //跳过异常的pipeline
                    logger.warn("skip.pipeline.close：{}", i);
                } else {
                    try {
                        pipeline.close();
                    } catch (Exception e) {
                        logger.error("releasePipeline异常：" + e.getMessage(), e);
                    }
                }
            }
            pipelinePool.clear();
        }
    }
}
