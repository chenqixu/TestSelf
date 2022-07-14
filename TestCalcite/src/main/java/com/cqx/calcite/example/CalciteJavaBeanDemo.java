package com.cqx.calcite.example;

import com.cqx.calcite.bean.signal.five.FiveSignalSchema;
import com.cqx.calcite.bean.signal.five.N1N2;
import org.apache.calcite.jdbc.CalciteConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * calcite java bean 示例
 *
 * @author chenqixu
 */
public class CalciteJavaBeanDemo extends AbstractCalciteDemo {

    private FiveSignalSchema fiveSignalSchema = new FiveSignalSchema();

    public static void main(String[] args) throws Exception {
        CalciteJavaBeanDemo calciteJavaBean = new CalciteJavaBeanDemo();
        calciteJavaBean.init();
        String sql_n1n2 = "select msisdn from bigdata.n1n2 where msisdn between 1440609800000 and 1440609999999";
        // 查询
        calciteJavaBean.query(sql_n1n2);
        N1N2[] n1n2 = {
                new N1N2(1440609800000L),
                new N1N2(1440709800000L),
                new N1N2(1440809800001L),
                new N1N2(1440909999999L)
        };

        // 新增数据
        calciteJavaBean.newData(n1n2);
        // 查询
        calciteJavaBean.query(sql_n1n2);

        String sql_n5 = "select dnn,snssai_sst from bigdata.n5 where (" +
                "(dnn is not null and dnn not like 'CMNET%') " +
                "and (dnn is not null and dnn not like 'CMWAP%') " +
                "and (dnn is not null and dnn not like 'IMS%') " +
                "and (dnn is not null and dnn not like 'CMDTJ%')) or snssai_sst=128";
        // 查询
        calciteJavaBean.query(sql_n5);
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @throws SQLException 查询异常
     */
    private void query(String sql) throws SQLException {
        System.out.println(String.format("query：%s", sql));
        ResultSet resultSet = null;
        Statement statement = null;
        // 获取连接
        try (Connection conn = getConn()) {
            // 把表定义写入根路径下的指定模式
            addSchema(conn, "bigdata", fiveSignalSchema);
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
     * 设置新数据
     *
     * @param data 数据
     */
    private void newData(N1N2[] data) {
        fiveSignalSchema.setN1n2(data);
    }
}
