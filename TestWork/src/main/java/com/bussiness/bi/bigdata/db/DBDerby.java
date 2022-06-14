package com.bussiness.bi.bigdata.db;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.jdbc.QueryResult;

import java.util.List;

/**
 * DBDerby
 *
 * @author chenqixu
 */
public class DBDerby {

    public static void main(String[] args) throws Exception {
        String dbName = "d:\\tmp\\data\\derby\\mydb"; // the name of the database
        String protocol = "jdbc:derby:" + dbName + ";create=true";
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.DERBY_LOCAL);
        dbBean.setPool(false);
        dbBean.setTns(protocol);
        try (JDBCUtil jdbcUtil = new JDBCUtil(dbBean)) {
            for (List<QueryResult> queryResultList : jdbcUtil.executeQuery("values 1")) {
                for (QueryResult queryResult : queryResultList) {
                    System.out.println(queryResult);
                }
            }
        }
    }
}
