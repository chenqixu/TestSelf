package com.cqx.common.utils.redis.client;

import com.cqx.common.utils.redis.RedisFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterRedisClient extends RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(ClusterRedisClient.class);
    private JedisCluster cluster;
    private Set<HostAndPort> HostAndPort_set = new HashSet<>();

    public ClusterRedisClient(RedisFactory.Builder builder) {
        addHostAndPort(builder);
        if (builder.isPipeline()) {
            if (builder.getPassword() != null && builder.getPassword().length() > 0) {
                cluster = new MyJedisCluster(HostAndPort_set, builder.getPassword(), builder.getMax_wait_millis());
            } else {
                cluster = new MyJedisCluster(HostAndPort_set, builder.getMax_wait_millis());
            }
            setPipeline(true);
        } else {
            if (builder.getPassword() != null && builder.getPassword().length() > 0) {
                cluster = new JedisCluster(HostAndPort_set, builder.getMax_wait_millis(), builder.getMax_wait_millis()
                        , DEFAULT_MAX_REDIRECTIONS, builder.getPassword(), new GenericObjectPoolConfig());
            } else {
                cluster = new JedisCluster(HostAndPort_set, builder.getMax_wait_millis());
            }
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
    public String setex(String key, int seconds, String value) {
        return cluster.setex(key, seconds, value);
    }

    @Override
    public Long expire(String key, int seconds) {
        return cluster.expire(key, seconds);
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
    public void scan(ScanParams params, IScan iScan) {
        cluster.getClusterNodes().values().forEach(pool -> {
            boolean done = true;
            String cur = ScanParams.SCAN_POINTER_START;
            try (Jedis jedisNode = pool.getResource()) {
                // 判断是否是主节点
                String currentNode = jedisNode.getClient().getHost() + ":" + jedisNode.getClient().getPort();
                String[] clusterNodes = jedisNode.clusterNodes().split("\n", -1);
                for (String _cn : clusterNodes) {
                    if (_cn != null && _cn.contains("master") && _cn.contains(currentNode)) {
                        logger.debug("currentNode={}, is_master={}", currentNode, _cn);
                        done = false;
                        break;
                    }
                }
                // 如果是主节点才循环扫描
                while (!done) {
                    ScanResult<String> resp = jedisNode.scan(cur, params);
                    iScan.scanGet(resp);
                    cur = resp.getStringCursor();
                    if (cur.equals(ScanParams.SCAN_POINTER_START)) {
                        done = true;
                    }
                }
            }
        });
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
    public Set<String> smembers(String key) {
        return cluster.smembers(key);
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

    /**
     * 释放连接
     *
     * @param connection
     */
    private void releaseConnection(Jedis connection) {
        if (connection != null) {
            connection.close();
        }
    }

    class ClusterRedisPipeline extends RedisPipeline {
        ConcurrentHashMap<String, Pipeline> pipelinePool = new ConcurrentHashMap<>();
        AtomicInteger syncCount = new AtomicInteger(0);
        AtomicInteger syncAndReturnCount = new AtomicInteger(0);
        ConcurrentHashMap<Pipeline, ConcurrentHashMap<String, Response<String>>> responsePipelineMap = new ConcurrentHashMap<>();

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
                throw new NullPointerException("初始化的时候未指定为管道模式，管道不可用！");
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
        protected void setex_inside(String key, int seconds, String value) {
            if (isPipeline()) getPipeline(key).setex(key, seconds, value);
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

        /**
         * 需要额外的存储异步的返回对象，以便在管道进行sync后获取返回值
         *
         * @param key
         * @param value
         */
        protected void set_inside_hasresponse(String key, String value) {
            if (isPipeline()) {
                Pipeline pipeline = getPipeline(key);
                Response<String> response = pipeline.set(key, value);
                ConcurrentHashMap<String, Response<String>> _response = responsePipelineMap.get(pipeline);
                if (_response == null) {
                    _response = new ConcurrentHashMap<>();
                    responsePipelineMap.put(pipeline, _response);
                }
                _response.put(key, response);
            }
        }

        /**
         * 在管道进行sync后获取返回值
         */
        protected void sync_hasresponse() {
            syncCount.set(0);
            int i = 0;
            for (Pipeline pipeline : pipelinePool.values()) {
                i++;
                try {
                    pipeline.sync();
                    // 循环结果，对非OK的结果进行告警
                    ConcurrentHashMap<String, Response<String>> _response = responsePipelineMap.get(pipeline);
                    if (_response != null) {
                        for (Map.Entry<String, Response<String>> entry : _response.entrySet()) {
                            if (!"OK".equals(entry.getValue().get())) {
                                logger.warn("response_is_not_OK，key: {}, response: {}", entry.getKey(), entry.getValue().get());
                            }
                        }
                        _response.clear();
                    }
                } catch (JedisConnectionException connectionException) {
                    syncCount.set(i);
                    throw connectionException;
                }
            }
        }
    }
}
