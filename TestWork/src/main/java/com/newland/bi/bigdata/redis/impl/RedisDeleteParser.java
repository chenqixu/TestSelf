package com.newland.bi.bigdata.redis.impl;

import com.cqx.exception.RedisSqlParserException;
import com.newland.bi.bigdata.redis.RedisClient;
import com.newland.bi.bigdata.redis.RedisResultSet;
import com.newland.bi.bigdata.redis.bean.RedisSqlParserBean;

import java.sql.SQLException;

/**
 * delete策略
 *
 * @author chenqixu
 */
public class RedisDeleteParser implements IRedisParser {

    private String sql;
    private RedisSqlParserException redisSqlParserException;
    private RedisSqlParserBean redisSqlParserBean;
    private RedisClient rc;
    private RedisResultSet redisResultSet;

    public RedisDeleteParser() {
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
