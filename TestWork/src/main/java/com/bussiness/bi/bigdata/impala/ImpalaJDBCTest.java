package com.bussiness.bi.bigdata.impala;

import java.sql.*;

/**
 * 用户测试impala-jdbc连接
 */
public class ImpalaJDBCTest {
    static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
    static String CONNECTION_URL = "jdbc:impala://10.1.8.75:21050/default;";
    com.cloudera.impala.jdbc41.Driver a;

    //	org.apache.hive.service.cli.thrift.TExecuteStatementReq t;
    public static void main(String[] args) throws Exception {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(CONNECTION_URL);
            ps = con.prepareStatement("select * from hb_userlog_query limit 1");
            rs = ps.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            //标题打印
            for (int i = 1; i < columnCount; i++) {
                System.out.print(resultSetMetaData.getColumnLabel(i) + "|");
            }
            System.out.println();
            //内容打印
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getObject(i) + "，");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }
}
