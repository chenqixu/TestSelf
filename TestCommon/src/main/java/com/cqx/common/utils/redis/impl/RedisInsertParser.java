package com.cqx.common.utils.redis.impl;

import com.cqx.common.exception.other.RedisSqlParserException;
import com.cqx.common.utils.redis.RedisClient;
import com.cqx.common.utils.redis.RedisConst;
import com.cqx.common.utils.redis.bean.RedisSqlParserBean;

import java.sql.SQLException;

/**
 * insert策略
 *
 * @author chenqixu
 */
public class RedisInsertParser implements IRedisParser {

    private String sql;
    private RedisSqlParserException redisSqlParserException;
    private RedisSqlParserBean redisSqlParserBean;
    private RedisClient rc;
    private int redisResultSet = -1;

    public RedisInsertParser() {
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

    /**
     * <pre>
     *     先按 values('切割
     *     替换掉insert into
     *     获取key
     *     解析field和value
     * </pre>
     *
     * @throws SQLException
     */
    @Override
    public void splitSql() throws SQLException {
        if (sql.endsWith(RedisConst.KEY_END)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        String[] arr = sql.split(RedisConst.KEY_VALUES);
        redisSqlParserException.throwSplitException(arr, 2, "sql语句不正确，没有values关键字！");
        String table = arr[0].replace(RedisConst.KEY_INERT_INTO, "");
        String value = arr[1];
        parserTable(table.trim());
        parserValue(value.trim());
    }

    private void parserTable(String table) throws SQLException {
        String[] tables = table.split(RedisConst.KEY_TABLESPLIT);
        redisSqlParserException.throwSplitException(tables, 2, "表名写法不正确，没有使用###关键字！");
        String table_type = tables[0];
        String table_name = tables[1];
        redisSqlParserBean.setTable_type(table_type);
        redisSqlParserBean.setTable_name(table_name);
    }

    /**
     * <pre>
     *      替换掉')
     *      按','进行分割
     * </pre>
     *
     * @param value
     */
    private void parserValue(String value) throws SQLException {
        String tmp = value.replace(RedisConst.KEY_VALUES_END, "");
        String[] arr = tmp.split(RedisConst.KEY_VALUES_SPLIT);
        //根据table_type来判断，string数组长度为1，hash数组长度为2
        switch (redisSqlParserBean.getTable_type()) {
            case RedisConst.KEY_HASHTABLE:
                redisSqlParserException.throwSplitException(arr, 2, "语法不正确，hash的值必须有2个（field，vlaue）且不能为空值！");
                redisSqlParserBean.getInsertValue().setField(arr[0]);
                redisSqlParserBean.getInsertValue().setValue(arr[1]);
                break;
            case RedisConst.KEY_STRINGTABLE:
                redisSqlParserException.throwSplitException(arr, 1, "语法不正确，string的值必须有1个（vlaue）！");
                redisSqlParserBean.getInsertValue().setValue(arr[0]);
                break;
            default:
                break;
        }
    }

    @Override
    public void deal() throws SQLException {
        switch (redisSqlParserBean.getTable_type()) {
            case RedisConst.KEY_HASHTABLE:
                long result = rc.hset(redisSqlParserBean.getTable_name(),
                        redisSqlParserBean.getInsertValue().getField(),
                        redisSqlParserBean.getInsertValue().getValue());
                redisResultSet = (int) result;
                break;
            case RedisConst.KEY_STRINGTABLE:
                String resultCode = rc.set(redisSqlParserBean.getTable_name(),
                        redisSqlParserBean.getInsertValue().getValue());
                if (resultCode != null && resultCode.length() > 0) redisResultSet = 0;
                break;
            default:
                break;
        }
    }

    @Override
    public Integer getRedisResultSet() {
        return redisResultSet;
    }
}
