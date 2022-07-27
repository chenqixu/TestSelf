package com.cqx.common.utils.redis;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * redis驱动
 *
 * @author chenqixu
 */
public class RedisDriver implements Driver {
    private static final String URL_PREFIX = "jdbc:redis://";

    // Register ourselves with the DriverManager
    static {
        try {
            DriverManager.registerDriver(new RedisDriver());
        } catch (SQLException ex) {
            throw new RuntimeException("Can't register driver", ex);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        // 先校验一下TNS正确性
        // jdbc:redis://10.1.4.185:6380
        if (url.startsWith(URL_PREFIX)) {
            return new RedisConnection(url.replace(URL_PREFIX, ""));
        } else {
            return null;
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
