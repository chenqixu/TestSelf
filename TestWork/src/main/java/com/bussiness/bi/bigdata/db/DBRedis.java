package com.bussiness.bi.bigdata.db;

import java.sql.*;

/**
 * redis
 *
 * @author chenqixu
 */
public class DBRedis {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            String DriverClassName = "com.newland.bi.bigdata.redis.RedisDriver";
            String dbUrl = "10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385";
            String dbUsername = "";
            String dbPassword = "";
            // 加载数据库驱动类
            Class.forName(DriverClassName);
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("conn:" + conn);
            queryTest(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String queryTest(Connection db_conn) {
        // 返回值
        String value = "";
        // 查询sql
        String sql;
//        sql = "select * from string###foo";
//        sql = "select * from hash###06006005 where field=18250326632";
//        sql = "select * from hash###06006007 where field=ZXV10__V2.1.2";
        sql = "select * from hash###06006004";
        // 预编译sql语句声明
        Statement stmt = null;
        // 结果集
        ResultSet rs = null;
        // 结果元数据
        ResultSetMetaData rsmd = null;
        try {
            if (db_conn != null) {
                // 无参语句对象
                stmt = db_conn.createStatement();
                System.out.println("stmt##" + stmt);
                // 查询
                rs = stmt.executeQuery(sql);
                System.out.println("rs##" + rs);
                rsmd = rs.getMetaData();
                int rsColoumnCount = rsmd.getColumnCount();// 字段个数
                while (rs.next()) {
                    for (int i = 0; i < rsColoumnCount; i++) {
                        System.out.println(rs.getString(i + 1));
                    }
                }
            }
            // 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
            closeDB(null, stmt, rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
            closeDB(db_conn, stmt, rs);
        }
        return value;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeDb_conn(Connection db_conn) {
        closeDB(db_conn, null, null);
    }

    /**
     * 数据库操作完成后，关闭相关的连接资源
     */
    private static void closeDB(Connection conn, Statement stmt, ResultSet rs) {
        try {
            //关闭结果集
            if (rs != null) {
                rs.close();
                rs = null;
            }
            //关闭Statement
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            //关闭数据连接
            if (conn != null) {
                if (!conn.isClosed())
                    conn.close();
                conn = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
