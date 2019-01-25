package com.newland.bi.bigdata.redis.impl;

import com.newland.bi.bigdata.redis.RedisClient;

import java.sql.SQLException;

/**
 * 解析接口
 *
 * @author chenqixu
 */
public interface IRedisParser {
    void setRc(RedisClient rc);

    void setSql(String sql);

    void splitSql() throws SQLException;

    void deal() throws SQLException;

    <T> T getRedisResultSet();
}
