package com.cqx.sync;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.sync.bean.DBBean;
import com.cqx.sync.bean.QueryResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OracleToMysql
 *
 * @author chenqixu
 */
public class OracleToMysql {

    private static final MyLogger logger = MyLoggerFactory.getLogger(OracleToMysql.class);
    private static final String TAB = "";//"\r\n";
    private JDBCUtil jdbcUtil;

    public OracleToMysql(DBBean dbBean) {
        jdbcUtil = new JDBCUtil(dbBean);
    }

    /**
     * 获取所有表名
     *
     * @throws SQLException
     */
    public void getAllTable() throws SQLException {
        List<List<QueryResult>> result = jdbcUtil.executeQuery("select table_name from tabs");
        for (List<QueryResult> queryResults : result) {
            for (QueryResult queryResult : queryResults) {
                descTable(queryResult.getValue().toString());
            }
        }
    }

    /**
     * 获取建表语句（MYSQL）
     *
     * @param tableName
     * @throws SQLException
     */
    public void descTable(String tableName) throws SQLException {
        List<QueryResult> desc = jdbcUtil.getTableMetaData(tableName);
        StringBuilder sb = new StringBuilder();
        sb.append(createTableBefore(tableName));
        for (int i = 0; i < desc.size(); i++) {
            if ((i + 1) == desc.size()) {//最后一条
                sb.append(createTableFileds(desc.get(i), true));
            } else {
                sb.append(createTableFileds(desc.get(i), false));
            }
        }
        sb.append(createTableEnd());
        logger.info("create sql：{}", sb.toString());
    }

    /**
     * 获取表字段，逗号分隔
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public String getTableFields(String tableName) throws SQLException {
        List<QueryResult> desc = jdbcUtil.getTableMetaData(tableName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < desc.size(); i++) {
            if ((i + 1) == desc.size()) {//最后一条
                sb.append(desc.get(i).getColumnName());
            } else {
                sb.append(desc.get(i).getColumnName()).append(",");
            }
        }
        logger.info("getTableFields：{}", sb.toString());
        return sb.toString();
    }

    /**
     * 获取所有表字段，Map<表名，表字段（逗号分隔）>
     *
     * @return
     * @throws SQLException
     */
    public Map<String, String> getAllTableAndFields() throws SQLException {
        Map<String, String> map = new HashMap<>();
        List<List<QueryResult>> result = jdbcUtil.executeQuery("select table_name from tabs");
        for (List<QueryResult> queryResults : result) {
            for (QueryResult queryResult : queryResults) {
                String tableName = queryResult.getValue().toString();
                map.put(tableName, getTableFields(tableName));
            }
        }
        return map;
    }

    /**
     * 资源释放
     */
    public void release() {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    /**
     * 建表前
     *
     * @param tableName
     * @return
     */
    private String createTableBefore(String tableName) {
        return "CREATE TABLE `" + tableName + "`(" + TAB;
    }

    /**
     * 建表后
     *
     * @return
     */
    private String createTableEnd() {
        return ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    }

    /**
     * 建表中
     *
     * @param queryResult
     * @param isEnd
     * @return
     */
    private String createTableFileds(QueryResult queryResult, boolean isEnd) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(queryResult.getColumnName()).append("` ");
        switch (queryResult.getColumnTypeName()) {
            case "NUMBER":
                sb.append("DECIMAL(").append(queryResult.getPrecision()).append(")");
                break;
            case "VARCHAR2":
                sb.append("VARCHAR(").append(queryResult.getPrecision()).append(")");
                break;
            case "DATE":
                sb.append("DATETIME");
                break;
            default:
                break;
        }
        String remarks = queryResult.getREMARKS();
        if (remarks != null && remarks.length() > 0) {
            remarks = remarks.replaceAll("(\\r\\n|\\n|\\n\\r)", ";");
            sb.append(" COMMENT '").append(remarks).append("'");
        }
        sb.append(" DEFAULT NULL");
        if (!isEnd) sb.append(",");
        sb.append(TAB);
        return sb.toString();
    }
}
