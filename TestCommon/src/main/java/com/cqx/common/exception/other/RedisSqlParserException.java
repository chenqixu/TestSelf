package com.cqx.common.exception.other;

import java.sql.SQLException;

/**
 * redis-sql解析异常
 *
 * @author chenqixu
 */
public class RedisSqlParserException {
    public void throwSplitException(String[] arr, int len, String reason) throws SQLException {
        String SQLState = "err";
        int vendorCode = -1;
        if (arr.length < len) throw new SQLException(reason, SQLState, vendorCode);
    }
}
