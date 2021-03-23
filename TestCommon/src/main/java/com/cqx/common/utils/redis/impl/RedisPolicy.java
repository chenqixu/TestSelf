package com.cqx.common.utils.redis.impl;

/**
 * Redis策略
 *
 * @author chenqixu
 */
public enum RedisPolicy {
    SELECT("select", new RedisSelectParser()),
    INSERT("insert", new RedisInsertParser()),
    UPDATE("update", new RedisUpdateParser()),
    DELETE("delete", new RedisDeleteParser()),
    ;

    private final String code;
    private final IRedisParser iRedisParser;

    private RedisPolicy(String code, IRedisParser iRedisParser) {
        this.code = code;
        this.iRedisParser = iRedisParser;
    }

    public String getCode() {
        return this.code;
    }

    public IRedisParser getiRedisParser() {
        return this.iRedisParser;
    }
}
