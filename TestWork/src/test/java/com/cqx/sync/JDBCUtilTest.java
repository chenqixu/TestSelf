package com.cqx.sync;

import com.cqx.sync.bean.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JDBCUtilTest {

    private static Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private JDBCUtil jdbcUtil;

    private DBBean mysqlConfig(String type) {
        DBBean srcdbBean = new DBBean();
        srcdbBean.setDbType(DBType.MYSQL);
        switch (type) {
            case "dev":
                srcdbBean.setTns("jdbc:mysql://10.1.8.200:3306/suyan_perf?useUnicode=true");
                srcdbBean.setUser_name("suyan");
                srcdbBean.setPass_word("suyan");
                break;
            case "local":
                srcdbBean.setTns("jdbc:mysql://127.0.0.1:3306/jutap?useUnicode=true");
                srcdbBean.setUser_name("udap");
                srcdbBean.setPass_word("udap");
                break;
        }
        return srcdbBean;
    }

    private DBBean oracleConfig(String type) {
        DBBean srcdbBean = new DBBean();
        srcdbBean.setDbType(DBType.ORACLE);
        switch (type) {
            case "dev":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.8.99:1521/orcl12cpdb1");
                srcdbBean.setUser_name("cctsys_dev");
                srcdbBean.setPass_word("TyM*2CnEm");
                break;
            case "local":
//                srcdbBean.setTns("jdbc:mysql://127.0.0.1:3306/jutap?useUnicode=true");
//                srcdbBean.setUser_name("udap");
//                srcdbBean.setPass_word("udap");
                break;
        }
        return srcdbBean;
    }

    @Before
    public void setUp() throws Exception {
        DBBean srcdbBean = oracleConfig("dev");
//        DBBean srcdbBean = mysqlConfig("local");
        jdbcUtil = new JDBCUtil(srcdbBean);
    }

    @After
    public void tearDown() throws Exception {
        jdbcUtil.closeAll();
        jdbcUtil.closeDataSource();
    }

    @Test
    public void executeQuery() throws Exception {
        String sql = "select max(collect_time) as collect_time from t_job_stat";
        List<List<QueryResult>> queryResults = jdbcUtil.executeQuery(sql);
        System.out.println(queryResults);
        jdbcUtil.closeAll();
    }

    @Test
    public void generateBeanByTabeName() throws Exception {
        String src_table_name = "sm2_rsmgr_t_scheduler_stat";
        src_table_name = "cfg_etl_time_rule";
        String src_fields = "time_rule,conv_file_head,insert_time,task_template_id";
//        BeanUtil src_beanutil = jdbcUtil.generateBeanByTabeName(src_table_name);
        BeanUtil src_beanutil = jdbcUtil.generateBeanByTabeNameAndFields(src_fields, src_table_name);
        System.out.println(src_beanutil.getFieldsType());
        String sql = "select " + src_fields + " from " + src_table_name;
        List<List<QueryResult>> queryResults = jdbcUtil.executeQuery(sql);
        for (List<QueryResult> queryResultList : queryResults) {
            System.out.println(queryResultList);
        }
        String dst_table_name = "cfg_etl_time_rule1";
        String dst_fields = "time_rule,conv_file_head,insert_time,task_template_id";
        String clean_sql = "truncate table " + dst_table_name;
        jdbcUtil.executeUpdate(clean_sql);
        BeanUtil dst_beanutil = jdbcUtil.generateBeanByTabeNameAndFields(dst_fields, dst_table_name);
        String insert_sql = "insert into " + dst_table_name + "(" + dst_fields + ") values(?,?,?,?)";
        jdbcUtil.executeBatch(insert_sql, queryResults, dst_beanutil.getFieldsType());
//        List<?> results = jdbcUtil.executeQuery(sql, cfg_etl_time_rule.getObjClass());
//        System.out.println(results);
//        jdbcUtil.executeBatch("insert into cfg_etl_time_rule1(time_rule,conv_file_head,insert_time,task_template_id) values(?,?,?,?)", results, cfg_etl_time_rule.getObjClass(), cfg_etl_time_rule.getFieldsMap());
    }

    @Test
    public void executeBatch() throws Exception {
        String fields = ":sync_name,:sync_time";
        String sql = "insert into sm2_rsmgr_sync_conf(sync_name,sync_time) values(" + fields + ")";
        List<SyncConf> beans = new ArrayList<>();
        SyncConf syncConf = new SyncConf();
        syncConf.setSYNC_NAME("test");
        syncConf.setSYNC_TIME(new java.sql.Timestamp(new Date().getTime()));
        beans.add(syncConf);
        jdbcUtil.executeBatch(sql, beans, SyncConf.class, fields);
    }

    @Test
    public void executeQuery1() throws Exception {
//        CfgEtlTimeRule cfgEtlTimeRule = new CfgEtlTimeRule();
//        cfgEtlTimeRule.setTime_rule("%001D");
//        List<CfgEtlTimeRule> resultList = jdbcUtil.executeQuery("select * from cfg_etl_time_rule where time_rule=:time_rule",
//                cfgEtlTimeRule, CfgEtlTimeRule.class);
//        for (CfgEtlTimeRule cfgEtlTimeRule1 : resultList) {
//            System.out.println(cfgEtlTimeRule1);
//        }
        RsmgrCluster cluster = new RsmgrCluster();
        cluster.setType_id("h%");
        List<RsmgrCluster> resultList = jdbcUtil.executeQuery("select * from sm2_rsmgr_cluster where type_id like :type_id"
                , cluster, RsmgrCluster.class);
        for (RsmgrCluster rsmgrCluster : resultList) {
            System.out.println(rsmgrCluster);
        }
    }

    @Test
    public void getParam() {
        String sql = "select * from sm2_rsmgr_cluster where type_id like %:type_id% and resource_id=:resource_id";
        //解析sql，找到所有的:xx，比如：select a from b where a=:a and b=:b and c>=:c and (d=:d)
        String[] params = (sql + " ").split(":", -1);
        List<ParamKey> paramList = new ArrayList<>();
        for (int i = 1; i < params.length; i++) {
            //把前面2个字符加入
            String _front = params[i - 1];
            String firstChar = _front.substring(_front.length() - 1);
            String _tmp = params[i];
            //找到空格、括号等等就返回
            String key = jdbcUtil.getParam(_tmp, ")", 0);
            //获取key后面那个字符
            String behindChar = _tmp.replace(key, "").substring(0, 1);
            ParamKey paramKey = new ParamKey(key, firstChar, behindChar);
            paramList.add(paramKey);
            logger.info("Front：【{}】，Behind：【{}】，Now：【{}】，Deal：【{}】", firstChar, behindChar, params[i], key);
            if (paramKey.isFrontLike() || paramKey.isBehindLike()) {
                sql = sql.replaceAll("%", "");
            }
            sql = sql.replace(":" + key, "?");
        }
        logger.info("sql：{}", sql);
        logger.info("paramList：{}", paramList);
    }

}