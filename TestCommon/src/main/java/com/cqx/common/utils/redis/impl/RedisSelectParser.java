package com.cqx.common.utils.redis.impl;

import com.cqx.common.exception.other.RedisSqlParserException;
import com.cqx.common.utils.redis.RedisClient;
import com.cqx.common.utils.redis.RedisConst;
import com.cqx.common.utils.redis.RedisResultSet;
import com.cqx.common.utils.redis.RedisRowData;
import com.cqx.common.utils.redis.bean.RedisSqlParserBean;

import java.sql.SQLException;
import java.util.Map;

/**
 * select策略
 *
 * @author chenqixu
 */
public class RedisSelectParser implements IRedisParser {

    private String sql;
    private RedisSqlParserException redisSqlParserException;
    private RedisSqlParserBean redisSqlParserBean;
    private RedisClient rc;
    private RedisResultSet redisResultSet;

    public RedisSelectParser() {
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
        if (sql.endsWith(RedisConst.KEY_END)) {
            sql = sql.substring(0, sql.length() - 1);
        }
        String[] arr = sql.split(RedisConst.KEY_TABLE);
        redisSqlParserException.throwSplitException(arr, 2, "sql语句不正确，没有from关键字！");
        String[] selects = arr[0].split(RedisConst.KEY_SELECT);
        redisSqlParserException.throwSplitException(selects, 2, "sql语句不正确，没有select关键字！");
        String select = selects[1].trim();
        String from = arr[1];
        String[] wheres = from.split(RedisConst.KEY_WHERE);
        String table = null;
        String where = null;
        if (wheres.length == 2) {
            table = wheres[0].trim();
            where = wheres[1].trim();
            parserWhere(where);
        } else {
            table = from.trim();
        }
        parserTable(table);
        parserField(select);
    }

    private void parserTable(String table) throws SQLException {
        String[] tables = table.split(RedisConst.KEY_TABLESPLIT);
        redisSqlParserException.throwSplitException(tables, 2, "表名写法不正确，没有使用###关键字！");
        String table_type = tables[0];
        String table_name = tables[1];
        redisSqlParserBean.setTable_type(table_type);
        redisSqlParserBean.setTable_name(table_name);
    }

    private void parserField(String select) {
        /**
         * string：key,value
         * hash：key,field,value
         */
        if (select.trim().equals("*")) {
            redisSqlParserBean.setHashFiled(true);
            switch (redisSqlParserBean.getTable_type()) {
                case RedisConst.KEY_HASHTABLE:
                    redisResultSet = new RedisResultSet(3);
                    break;
                case RedisConst.KEY_STRINGTABLE:
                    redisResultSet = new RedisResultSet(2);
                    break;
                default:
                    break;
            }
        } else {
            String[] fields = select.split(RedisConst.KEY_FIELDS);
            redisSqlParserBean.setTable_fileds(fields);
            redisSqlParserBean.setHashFiled(false);
            redisResultSet = new RedisResultSet(fields.length);
        }
    }

    private void parserWhere(String where) throws SQLException {
        String[] where_fields = where.split(RedisConst.KEY_WHERE_FIELD);
        redisSqlParserException.throwSplitException(where_fields, 2, "条件过滤写法不正确，没有使用" + RedisConst.KEY_WHERE_FIELD + "关键字！");
        redisSqlParserBean.setCondition(where_fields[1]);
    }

    @Override
    public void deal() throws SQLException {
        switch (redisSqlParserBean.getTable_type()) {
            /**
             * hash
             */
            case RedisConst.KEY_HASHTABLE:
                if (redisSqlParserBean.getCondition() != null) {
                    String value = rc.hget(redisSqlParserBean.getTable_name(), redisSqlParserBean.getCondition());
                    RedisRowData redisRowData = new RedisRowData(redisResultSet.getMetaData().getColumnCount());
                    if (redisSqlParserBean.getHashFiled().isKey())
                        redisRowData.setValue(redisSqlParserBean.getTable_name());
                    if (redisSqlParserBean.getHashFiled().isField())
                        redisRowData.setValue(redisSqlParserBean.getCondition());
                    if (redisSqlParserBean.getHashFiled().isValue())
                        redisRowData.setValue(value);
                    redisResultSet.addRow(redisRowData);
                } else {
                    Map<String, String> map = rc.hgetAll(redisSqlParserBean.getTable_name());
                    for (Map.Entry<String, String> tmp : map.entrySet()) {
                        RedisRowData redisRowData = new RedisRowData(redisResultSet.getMetaData().getColumnCount());
                        if (redisSqlParserBean.getHashFiled().isKey())
                            redisRowData.setValue(redisSqlParserBean.getTable_name());
                        if (redisSqlParserBean.getHashFiled().isField())
                            redisRowData.setValue(tmp.getKey());
                        if (redisSqlParserBean.getHashFiled().isValue())
                            redisRowData.setValue(tmp.getValue());
                        redisResultSet.addRow(redisRowData);
                    }
                }
                break;
            /**
             * 字符串
             */
            case RedisConst.KEY_STRINGTABLE:
                String value = rc.get(redisSqlParserBean.getTable_name());
                RedisRowData redisRowData = new RedisRowData(redisResultSet.getMetaData().getColumnCount());
                if (redisSqlParserBean.getHashFiled().isKey())
                    redisRowData.setValue(redisSqlParserBean.getTable_name());
                if (redisSqlParserBean.getHashFiled().isValue())
                    redisRowData.setValue(value);
                redisResultSet.addRow(redisRowData);
                break;
            default:
                break;
        }
    }

    @Override
    public RedisResultSet getRedisResultSet() {
        return redisResultSet;
    }
}
