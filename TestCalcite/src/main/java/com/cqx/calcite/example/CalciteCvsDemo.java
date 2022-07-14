package com.cqx.calcite.example;

import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * CalciteCvsDemo
 *
 * @author chenqixu
 */
public class CalciteCvsDemo extends AbstractCalciteDemo {

    public static void main(String[] args) throws SQLException {
        CalciteCvsDemo calciteCvsDemo = new CalciteCvsDemo();
        calciteCvsDemo.test("select msisdn from bigdata.n1n2 where msisdn='1440809800001'");
        calciteCvsDemo.test("select dnn,snssai_sst from bigdata.n5 where snssai_sst<128");
    }

    private void test(String sql) throws SQLException {
        System.out.println(String.format("sql: %s", sql));
        ResultSet resultSet = null;
        Statement statement = null;
        try (Connection conn = getConn()) {
            // 创建statement
            statement = conn.createStatement();
            // 执行查询
            resultSet = statement.executeQuery(sql);
            // 结果打印
            getResultSet(resultSet);
        } finally {
            // 资源释放
            closeRS(resultSet, statement);
        }
    }

    /**
     * 获得连接
     *
     * @return 连接
     * @throws SQLException 连接异常
     */
    protected Connection getConn() throws SQLException {
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");

        Object obj = new Object();
        URL url = obj.getClass().getResource("/csv_model.json");
        info.put("model", url.getPath().replaceFirst("/", ""));

        return DriverManager.getConnection("jdbc:calcite:", info);
    }
}
