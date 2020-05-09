package com.cqx.sync;

import com.cqx.common.utils.file.FileMangerCenter;
import com.cqx.common.utils.jdbc.*;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 查询并保存
 *
 * @author chenqixu
 */
public class QueryAndSave {
    private static final MyLogger logger = MyLoggerFactory.getLogger(QueryAndSave.class);

    static {
        DBFormatUtil.setOracleDbType();
    }

    private JDBCUtil jdbcUtil;
    private FileMangerCenter fileMangerCenter;

    public QueryAndSave(DBBean dbBean) {
        jdbcUtil = new JDBCUtil(dbBean);
    }

    public void exec(Map<String, String> params) throws SQLException, IOException {
        //数据保存路径
        String data_save_path = params.get("data_save_path");
        //表名
        String table_name = params.get("table_name");
        //查询字段
        String query_fileds = params.get("query_fields");
        //条件字段
        String where = params.get("where");
        //上一次查询时间
        String query_time = params.get("query_time");
        //查询间隔
        String interval = params.get("interval");
        //内容分割
        String split_str = params.get("split_str");
        fileMangerCenter = new FileMangerCenter(data_save_path + table_name);
        fileMangerCenter.initWriter();
        StringBuilder sb = new StringBuilder();
        sb.append("select ")
                .append(query_fileds)
                .append(" from ")
                .append(table_name)
                .append(" where ")
                .append(where)
                .append(" between ")
                .append(DBFormatUtil.to_date(query_time, DBFormatEnum.YYYYMMDDHH24MISS))
                .append(" and ")
                .append(DBFormatUtil.to_date(query_time, DBFormatEnum.YYYYMMDDHH24MISS))
                .append(" + ")
                .append(interval);
        logger.info("sql：{}", sb.toString());
        List<List<QueryResult>> queryResultList = jdbcUtil.executeQuery(sb.toString());
        for (List<QueryResult> queryResults : queryResultList) {
            StringBuilder content = new StringBuilder();
            for (QueryResult queryResult : queryResults) {
                content.append(queryResult.getValue()).append(split_str);
            }
            fileMangerCenter.write(content.toString() + "\r\n");
            logger.info("{}", content.toString());
        }
    }

    /**
     * 资源释放
     */
    public void release() throws IOException {
        if (jdbcUtil != null) jdbcUtil.close();
        if (fileMangerCenter != null) fileMangerCenter.close();
    }
}
