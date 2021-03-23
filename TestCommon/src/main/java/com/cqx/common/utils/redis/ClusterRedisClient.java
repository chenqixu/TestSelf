package com.cqx.common.utils.redis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterRedisClient implements RedisClient {
    private JedisPoolConfig config;
    private JedisCluster cluster = null;
    private Set<HostAndPort> HostAndPort_set = new HashSet<HostAndPort>();

    public ClusterRedisClient(RedisFactory.Builder builder) {
        config = new JedisPoolConfig();
        config.setMaxIdle(DEFUALT_MAX_IDLE);
        config.setMaxTotal(DEFUALT_MAX_TOTAL);
        config.setMaxWaitMillis(DEFUALT_MAX_WAIT_MILLIS);
        addHostAndPort(builder);
        cluster = new JedisCluster(HostAndPort_set, config);
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
    public void close() {
        try {
            cluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
