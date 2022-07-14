package com.cqx.calcite.example;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;

import java.sql.*;
import java.util.Properties;

/**
 * AbstractCalciteDemo
 *
 * @author chenqixu
 */
public abstract class AbstractCalciteDemo {

    /**
     * 初始化，加载驱动类
     *
     * @throws ClassNotFoundException 驱动类找不到
     */
    protected void init() throws ClassNotFoundException {
        Class.forName("org.apache.calcite.jdbc.Driver");
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
        return DriverManager.getConnection("jdbc:calcite:", info);
    }

    /**
     * 把表定义写入根路径下的指定模式
     *
     * @param conn        连接
     * @param schemaName  模式名
     * @param tableTarget 表定义
     * @throws SQLException sql异常
     */
    protected void addSchema(Connection conn, String schemaName, Object tableTarget) throws SQLException {
        // 连接拆包
        CalciteConnection calciteConnection = conn.unwrap(CalciteConnection.class);
        // 获取根路径的Schema
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        // 把表定义写入根路径下的指定模式
        Schema schema = new ReflectiveSchema(tableTarget);
        rootSchema.add(schemaName, schema);
    }

    /**
     * 结果打印
     *
     * @param resultSet 结果集
     * @throws SQLException sql异常
     */
    protected void getResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    Object val = resultSet.getObject(i);
                    sb.append(String.format("[%s：%s] ", columnName, val));
                }
                System.out.println(sb.toString());
            }
        } else {
            System.err.println("没有查询到结果！");
        }
    }

    /**
     * 资源释放
     *
     * @param resultSet 结果集
     * @param statement statement
     * @throws SQLException sql异常
     */
    protected void closeRS(ResultSet resultSet, Statement statement) throws SQLException {
        // 关闭resultSet
        if (resultSet != null) resultSet.close();
        // 关闭statement
        if (statement != null) statement.close();
    }
}
