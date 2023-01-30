package com.cqx.common.utils.redis.client;

import com.cqx.common.utils.redis.RedisFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SinaleRedisClient extends RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(SinaleRedisClient.class);
    private Jedis jedis;

    public SinaleRedisClient(RedisFactory.Builder builder) {
        if (builder.getIp() == null && builder.getIp_ports() != null) {
            for (String str : builder.getIp_ports().split(",")) {
                String[] arr = str.split(":");
                if (arr != null && arr.length == 2) {
                    String ip = arr[0];
                    int port = Integer.valueOf(arr[1]);
                    jedis = new Jedis(ip, port);
                    break;
                }
            }
        } else if (builder.getIp() != null) {
            jedis = new Jedis(builder.getIp(), builder.getPort());
        } else {
            throw new NullPointerException("传入的ip、端口为空！");
        }
        if (jedis == null) {
            throw new NullPointerException("传入的配置不正确，请检查！");
        }
        String password = builder.getPassword();
        if (password != null && password.length() > 0) {
            jedis.auth(password);
        }
    }

    public Set<String> keys(String pattern) {
        return jedis.keys(pattern);
    }

    public String type(String key) {
        return jedis.type(key);
    }

    public void flushDB() {
        jedis.flushDB();
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
    public String setex(String key, int seconds, String value) {
        return jedis.setex(key, seconds, value);
    }

    @Override
    public Long expire(String key, int seconds) {
        return jedis.expire(key, seconds);
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
    public List<String> mget(String... keys) {
        return jedis.mget(keys);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return jedis.hmget(key, fields);
    }

    @Override
    public Long incr(String key) {
        return jedis.incr(key);
    }

    @Override
    public Long hincrBy(String key, String field, Long value) {
        return jedis.hincrBy(key, field, value);
    }

    @Override
    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }

    @Override
    public void close() {
        jedis.close();
    }

    @Override
    protected RedisPipeline generatePipeline(int commit_num, int get_cache_num) {
        return new SinaleRedisPipeline(commit_num, get_cache_num);
    }

    class SinaleRedisPipeline extends RedisPipeline {
        Pipeline pipe;

        public SinaleRedisPipeline(int commit_num, int get_cache_num) {
            super(commit_num, get_cache_num);
        }

        @Override
        protected void open() {
            pipe = jedis.pipelined();
        }

        @Override
        public void sync() {
            pipe.sync();
        }

        @Override
        protected List<Object> syncAndReturnAll() {
            return pipe.syncAndReturnAll();
        }

        @Override
        protected void set_inside(String key, String value) {
            pipe.set(key, value);
        }

        @Override
        protected void setex_inside(String key, int seconds, String value) {
            pipe.setex(key, seconds, value);
        }

        @Override
        protected void del_inside(String key) {
            pipe.del(key);
        }

        @Override
        protected void request_get_inside(String key) {
            pipe.get(key);
        }

        @Override
        protected void hset_inside(String key, String field, String value) {
            pipe.hset(key, field, value);
        }

        @Override
        protected void hdel_inside(String key, String field) {
            pipe.hdel(key, field);
        }

        @Override
        protected void request_hget_inside(String key, String field) {
            pipe.hget(key, field);
        }

        @Override
        protected void releasePipeline() {
            try {
                pipe.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
