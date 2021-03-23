package com.cqx.common.utils.redis.impl;

import com.cqx.common.exception.other.RedisSqlParserException;
import com.cqx.common.utils.redis.RedisClient;
import com.cqx.common.utils.redis.RedisResultSet;
import com.cqx.common.utils.redis.bean.RedisSqlParserBean;

import java.sql.SQLException;

/**
 * update策略
 *
 * @author chenqixu
 */
public class RedisUpdateParser implements IRedisParser {

    private String sql;
    private RedisSqlParserException redisSqlParserException;
    private RedisSqlParserBean redisSqlParserBean;
    private RedisClient rc;
    private RedisResultSet redisResultSet;

    public RedisUpdateParser() {
        redisSqlParserException = new RedisSqlParserException();
        redisSqlParserBean = new RedisSqlParserBean();
    }

    @Override
    public void setRc(RedisClient rc) {
        this.rc = rc;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public void splitSql() throws SQLException {

    }

    @Override
    public void deal() throws SQLException {

    }

    @Override
    public RedisResultSet getRedisResultSet() {
        return redisResultSet;
    }
}
