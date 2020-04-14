package com.cqx.sync;

import com.cqx.common.utils.jdbc.BeanUtil;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.jdbc.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * DBFullSync
 *
 * @author chenqixu
 */
public class DBFullSync implements IDBSync {

    private static final Logger logger = LoggerFactory.getLogger(DBFullSync.class);
    private Map<String, Object> params;
    private JDBCUtil srcJDBC;
    private JDBCUtil dstJDBC;
    private String src_tab_name;
    private String dst_tab_name;
    private String src_fields;
    private String dst_fields;
    private String[] dst_field_arr;
    private BeanUtil dst_beanUtil;

    @Override
    public void init(Map<String, Object> params) throws Exception {
        this.params = params;
        DBBean srcdbBean = (DBBean) params.get("srcdbBean");
        DBBean dstdbBean = (DBBean) params.get("dstdbBean");
        src_tab_name = (String) params.get("src_tab_name");
        dst_tab_name = (String) params.get("dst_tab_name");
        src_fields = (String) params.get("src_fields");
        dst_fields = (String) params.get("dst_fields");
        dst_field_arr = dst_fields.split(",", -1);
        srcJDBC = new JDBCUtil(srcdbBean);
        dstJDBC = new JDBCUtil(dstdbBean);
        // 源采集周期字段
        // 源采集周期字段时间格式
        // 目标采集周期字段
        // 目标采集周期字段时间格式
        logger.info("初始化参数：源端数据库：{}，目标端数据库：{}，源表：{}，目标表：{}，源字段：{}，目标字段：{}，目标字段拆分后的数组：{}",
                srcdbBean, dstdbBean, src_tab_name, dst_tab_name, src_fields, dst_fields, dst_field_arr);
        // 构造目标端javabean
        dst_beanUtil = dstJDBC.generateBeanByTabeNameAndFields(dst_fields, dst_tab_name);
        logger.info("构造目标端javabean：{}", dst_beanUtil.getObj());
    }

    /**
     * <pre>
     *     查询源表
     *     清空目标表
     *     把源表数据经过转换后插入目标表
     *     提交
     * </pre>
     */
    @Override
    public void run() throws Exception {
        try {
            // 清空目标端数据
            String clean_sql = "truncate table " + dst_tab_name;
            logger.info("清空目标端数据 clean_sql：{}", clean_sql);
            int clean_ret = dstJDBC.executeUpdate(clean_sql);
            logger.info("sync clean_ret：{}", clean_ret);
            if (clean_ret < 0) throw new Exception("清空目标端数据失败");
            // 从源端全量查询数据，同步到目标端
            String sql = "select " + src_fields + " from " + src_tab_name;
            logger.info("从源端全量查询数据 select_src_sql：{}", sql);
            List<List<QueryResult>> srcResultList = srcJDBC.executeQuery(sql);
            StringBuffer prepare = new StringBuffer();
            for (int i = 0; i < dst_field_arr.length; i++) {
                prepare.append("?,");
            }
            prepare.delete(prepare.length() - 1, prepare.length());
            String sync_sql = "insert into " + dst_tab_name + " (" + dst_fields + ") values(" + prepare.toString() + ")";
            logger.info("同步到目标端 sync_sql：{}", sync_sql);
            int ret = dstJDBC.executeBatch(sync_sql, srcResultList, dst_beanUtil.getFieldsType());
            if (ret < 0) throw new Exception("同步到目标端失败");
        } finally {
            srcJDBC.close();
            dstJDBC.close();
        }
    }
}
