package com.newland.bi.bigdata.redis;

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
	public void close();
}
