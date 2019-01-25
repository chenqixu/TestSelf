package com.newland.bi.bigdata.redis;

import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Map;

public interface RedisClient {
	static final int DEFUALT_MAX_IDLE = 10;
	static final int DEFUALT_MAX_TOTAL = 100;
	static final int DEFUALT_MAX_WAIT_MILLIS = 3000;
	public String set(String key, String value);
	public boolean setnx(String key, String value);
	public boolean setnx(String key, String value, Integer seconds);
	public String get(String key);
	public Long del(String key);
	public Long hdel(String key, String field);
	public Long hset(String key, String field, String value);
	public Long hsetnx(String key, String field, String value);
	public String hget(String key, String field);
	public Map<String, String> hgetAll(String key);
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);
	public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params);
	public void close();
}
