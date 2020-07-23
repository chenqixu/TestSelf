package com.cqx.common.utils.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;

/**
 * DBUtil
 *
 * @author chenqixu
 */
public class DBUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private JDBCUtil jdbcUtil;
    private String table_name;
    private String db_read_code;
    private String content_split;
    private List<QueryResult> table_meta_data;

    public DBUtil(DBBean dbBean) {
        jdbcUtil = new JDBCUtil(dbBean);
    }

    public void init() throws SQLException, UnsupportedEncodingException {
        table_meta_data = jdbcUtil.getTableMetaData(table_name);
        for (QueryResult queryResult : table_meta_data) {
            logger.info("{}", queryResult);
        }
        List<List<QueryResult>> query = jdbcUtil.executeQuery("select KEY_ID,DIM_ID,KEY_DESC,PARENT_KEY from " + table_name);
        for (List<QueryResult> queryResults : query) {
            for (QueryResult queryResult : queryResults) {
                Object value = queryResult.getValue();
                String value_class = value != null ? value.getClass().getName() : "null";
                if (value instanceof java.lang.String) {
                    String _value = (String) value;
                    int byte_len = _value.getBytes().length;
                    int string_len = _value.length();
                    byte[] gbk_byte = _value.getBytes("GBK");
                    int gbk_byte_len = gbk_byte.length;
                    System.out.print(String.format("%s %s [%s]%s [GBK]%s，",
                            _value, value_class, Charset.defaultCharset(), byte_len, gbk_byte_len));
                } else {
                    System.out.print(String.format("%s %s，", value, value_class));
                }
            }
            System.out.println();
        }
    }

    public void tableFieldsCheck(List<String> data) {

    }

    /**
     * 资源释放
     */
    public void release() {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
}
