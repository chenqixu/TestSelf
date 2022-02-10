package com.cqx.common.utils.jdbc;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultBean;
import com.cqx.common.utils.system.ArraysUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseRunableThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.core.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JDBCUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private IJDBCUtil jdbcUtil;

    @Before
    public void setUp() throws Exception {
        Map params = getParam("jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
        // 从JVM参数中获取，使用方式：-Djdbc.bean=mysql79Bean
        String jdbcBean = System.getProperty("jdbc.bean");
        DBBean dbBean;
        if (jdbcBean != null && jdbcBean.trim().length() > 0) {
            logger.info("获取到-Djdbc.bean={}", jdbcBean);
            dbBean = paramsParserUtil.getBeanMap().get(jdbcBean);
        } else {
//            dbBean = paramsParserUtil.getBeanMap().get("localmysqlBean");
            dbBean = paramsParserUtil.getBeanMap().get("mysql79Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("hadoopPostgreSql");
//            dbBean = paramsParserUtil.getBeanMap().get("oracle242Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("oracle12c_cctsys_dev_Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("localAdbBean");
//            dbBean = paramsParserUtil.getBeanMap().get("adbBean");
//            dbBean = paramsParserUtil.getBeanMap().get("localoracleBean");
//            dbBean.setPool(false);
        }
//        jdbcUtil = new JDBCRetryUtil(dbBean, 30000, 30);
//        jdbcUtil = new JDBCUtil(dbBean);
        jdbcUtil = new JDBCUtil(dbBean, 1, 1, 1);
    }

    @After
    public void tearDown() throws Exception {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    @Test
    public void executeQuery() throws SQLException {
        int error_cnt = 0;
        int evaluation_cnt = 1;
        TimeCostUtil exec = new TimeCostUtil();
        List<List<QueryResult>> results = null;
        for (int i = 0; i < 20; i++) {
            try {
                exec.start();
                if (evaluation_cnt % 4 == 0) {
                    logger.info("reset error_cnt");
                    error_cnt = 0;
                    evaluation_cnt = 1;
                }
                if (error_cnt > 0) {
                    logger.info("skip，because error_cnt：{}", error_cnt);
                    evaluation_cnt++;
                } else {
                    results = jdbcUtil.executeQuery("select user_id from sm2_user");
                }
                exec.stop();
                logger.info("result.size：{}，cost：{}", (results != null ? results.size() : 0), exec.stopAndGet());
            } catch (Exception e) {
                exec.stop();
                logger.warn("ERROR：{}，cost：{}", e.getMessage(), exec.stopAndGet());
                error_cnt++;
            }
            SleepUtil.sleepMilliSecond(500);
        }
    }

    @Test
    public void executeQueryRetry() {
        ConcurrentQuery1 c1 = new ConcurrentQuery1();
        ConcurrentQuery2 c2 = new ConcurrentQuery2();
        c1.start();
        c2.start();
        SleepUtil.sleepSecond(60);
        c1.stop();
        c2.stop();
    }

    @Test
    public void queryTest1() throws SQLException {
        TimeCostUtil exec = new TimeCostUtil();
        exec.start();
        List<List<QueryResult>> results = jdbcUtil.executeQuery("select user_id from sm2_user");
        exec.stop();
        logger.info("{} result.size：{}，cost：{}", Thread.currentThread().getName(),
                (results != null ? results.size() : 0), exec.stopAndGet());
    }

    private void queryTest2() throws SQLException {
        TimeCostUtil exec = new TimeCostUtil();
        exec.start();
        jdbcUtil.executeQuery("select user_id from sm2_user", new IJDBCUtilCall.ICallBack() {
            @Override
            public void call(ResultSet rs) throws SQLException {
                //null
            }
        });
        exec.stop();
        logger.info("{} cost：{}", Thread.currentThread().getName(), exec.stopAndGet());
    }

    @Test
    public void executeBatchClob() throws Exception {
        String sql = "update nmc_schema set avsc=? where schema_name=?";
        QueryResultFactory qrf = QueryResultFactory.getInstance()
                .buildQR("avsc", "java.sql.Clob", "123")
                .buildQR("schema_name", "java.lang.String", "SCHEMA_USER_ADDITIONAL_INFO")
                .toList();
        int ret = jdbcUtil.executeBatch(sql, qrf.getData(), qrf.getDstFieldsType(), true);
        logger.info("sql：{}，ret：{}", sql, ret);
    }

    @Test
    public void postgreSqlTest() throws SQLException {
        jdbcUtil.executeQuery("select 1");
    }

    @Test
    public void postgresqlDeclareTest() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" select count(1) into hasval from rl_status where status=0;")
                .append(" if hasval=0 THEN")
                .append(" insert into rl_status(status) values(0);")
                .append(" else")
                .append(" update rl_status set status=hasval;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        int ret = jdbcUtil.executeUpdate(sb.toString());
        logger.info("ret：{}", ret);
    }

    /**
     * 插入测试，MERGE_INTO_ONLY模式
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsInsertMERGE_INTO_ONLYTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_varchar", "java.lang.String", "test1")
                    .buildQR("f_boolean", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .buildQR("f_varchar", "java.lang.String", "test2")
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("i");
            op_types.add("i");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};
            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, false, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 插入测试，MERGE_INTO_ONLY模式，IQueryResultBean
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsInsertMERGE_INTO_ONLY_IQRTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<IQueryResultBean> qrBeanData = QueryResultFactory.getInstance(MergeBean.class)
                    .buildQR("f_varchar", "java.lang.String", "test1")
                    .buildQR("f_boolean", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toQRBeanList("i")
                    .buildQR("f_varchar", "java.lang.String", "test2")
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toQRBeanList("i")
                    .getQRBeanData();

            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};
            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(qrBeanData, table, fields, fields_type
                    , pks, pks_type, false, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 插入测试，MERGE_INTO_UPDATE模式
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsInsertMERGE_INTO_UPDATETest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_varchar", "java.lang.String", "test1")
                    .buildQR("f_boolean", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .buildQR("f_varchar", "java.lang.String", "test2")
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("i");
            op_types.add("i");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};
            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, false, MergeEnum.MERGE_INTO_UPDATE);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 更新测试
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsUpdateTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_varchar_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("u");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 更新测试，IQueryResultBean
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsUpdate_IQRTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<IQueryResultBean> qrBeanData = QueryResultFactory.getInstance(MergeBean.class)
                    .buildQR("f_varchar_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toQRBeanList("u")
                    .getQRBeanData();

            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(qrBeanData, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 主键更新测试
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsUpdatePksTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_varchar_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_boolean", "java.lang.Boolean", true)
                    .buildQR("f_timestamp_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 124L)
                    .toList()
                    .getData();

            List<List<QueryResult>> oldPks = QueryResultFactory.getInstance()
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("u");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY, oldPks);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 主键更新测试，IQueryResultBean
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsUpdatePks_IQRTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<IQueryResultBean> qrBeanData = QueryResultFactory.getInstance(MergeBean.class)
                    .buildQR("f_varchar_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean_isMissing", "java.lang.Boolean", false)
                    .buildQR("f_boolean", "java.lang.Boolean", true)
                    .buildQR("f_timestamp_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time_isMissing", "java.lang.Boolean", true)
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 124L)
                    .buildOldPks("f_varchar", "java.lang.String", "test")
                    .buildOldPks("f_boolean", "java.lang.Boolean", false)
                    .buildOldPks("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildOldPks("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildOldPks("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildOldPks("f_decimal", "long", 591500319216463L)
                    .buildOldPks("f_pk", "long", 123L)
                    .toQRBeanList("u")
                    .getQRBeanData();

            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(qrBeanData, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 删除测试
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsDeleteTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 124L)
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("d");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    /**
     * 删除测试，IQueryResultBean
     *
     * @throws Exception
     */
    @Test
    public void executeBatchSqlsDelete_IQRTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<IQueryResultBean> qrBeanData = QueryResultFactory.getInstance(MergeBean.class)
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 124L)
                    .toQRBeanList("d")
                    .getQRBeanData();

            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};

            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(qrBeanData, table, fields, fields_type
                    , pks, pks_type, true, MergeEnum.MERGE_INTO_ONLY);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }

    @Test
    public void parseDollarQuotesTest() throws ParseException {
        int offset = Parser.parseDollarQuotes("do $$ declare begin SELECT $$;$$ WHERE $x$?$x$=$_0$?$_0$ AND $$?$$=?; end; $$".toCharArray(), 3);
        logger.info("offset：{}", offset);

        String task_id = "101067676513@2021062415000000";
        String[] task_id_array = task_id.split("@", -1);

        String task_template_id = task_id_array[0];
        String time_seq = task_id_array[1];
        String seq = time_seq.substring(14);
        String time = time_seq.substring(0, 14);

        logger.info("task_template_id：{}", task_template_id);
        logger.info("time_seq：{}", time_seq);
        logger.info("seq：{}", seq);
        logger.info("time：{}", time);
        logger.info("getTime：{}", Utils.getTime(time));
    }

    @Test
    public void queryAndMergeInto() throws Exception {
        // 把tab_fields根据pks，分出fields
        // 查询的时候使用fields+pks的方式
        String table = "qry_sell_operator";
        String tab_fields = "org_id,sell_id,task_id";
        String pks = "sell_id,task_id";

        String[] tab_fields_array = tab_fields.split(",", -1);
        String[] pks_array = pks.split(",", -1);
        String[] fields_array = ArraysUtil.arrayRemove(tab_fields_array, pks_array);

        String fields = ArraysUtil.arrayToStr(fields_array, ",");

        String query = "select %s,%s from %s";
        List<List<QueryResult>> queryResults = jdbcUtil.executeQuery(String.format(query, fields, pks, table));

        List<MergeBean> iQueryResultBeanList = new ArrayList<>();
        for (List<QueryResult> queryResults1 : queryResults) {
            MergeBean mergeBean = new MergeBean();
            mergeBean.setOp_type("i");
            for (QueryResult queryResult : queryResults1) {
                if (queryResult.getValue().toString().equals("0")) {
                    queryResult.setValue(1L);
                }
            }
            mergeBean.setQueryResults(queryResults1);
            iQueryResultBeanList.add(mergeBean);
        }
        String[] fields_type = {};
        String[] pks_type = {};
        boolean ismissing = true;
        MergeEnum isMergeInfo = MergeEnum.MERGE_INTO_ONLY;

        List<QueryResult> metaData = jdbcUtil.getTableMetaData(table);
        Map<String, String> metaMap = new HashMap<>();
        for (QueryResult md : metaData) {
            metaMap.put(md.getColumnName(), md.getColumnClassName());
        }
        logger.info("getTableMetaData：{}", metaData);
        logger.info("metaMap：{}", metaMap);
        List<String> fields_type_list = new ArrayList<>();
        for (String _field : fields_array) {
            fields_type_list.add(metaMap.get(_field));
        }
        fields_type = fields_type_list.toArray(fields_type);
        logger.info("fields_type：{}", Arrays.asList(fields_type));

        List<String> pks_type_list = new ArrayList<>();
        for (String _pks : pks_array) {
            pks_type_list.add(metaMap.get(_pks));
        }
        pks_type = pks_type_list.toArray(pks_type);
        logger.info("pks_type：{}", Arrays.asList(pks_type));

        List<Integer> rets = jdbcUtil.executeBatch(iQueryResultBeanList, table
                , fields_array, fields_type, pks_array, pks_type, ismissing, isMergeInfo);
        logger.info("rets：{}", rets);
    }

    @Test
    public void metaDataTest() throws Exception {
        String table_name = "zyh";
        String[] fields_array = {"queue_used_orgid", "queue_used_orgname", "collect_time"};
        // getDstTableMetaData
        LinkedHashMap<String, String> metaDataMap = jdbcUtil.getDstTableMetaData(table_name);
        logger.info("metaDataMap：{}", metaDataMap);
        // getFieldsTypeAsArray
        logger.info("getFieldsTypeAsArray：{}", Arrays.asList(jdbcUtil.getFieldsTypeAsArray(metaDataMap, fields_array)));
        // getFieldsTypeAsList
        logger.info("getFieldsTypeAsList：{}", jdbcUtil.getFieldsTypeAsList(metaDataMap, fields_array));
        // getDefaultFieldsTypeAsArray
        logger.info("getDefaultFieldsTypeAsArray：{}", Arrays.asList(jdbcUtil.getDefaultFieldsTypeAsArray(table_name)));
        // getDefaultFieldsTypeAsList
        logger.info("getDefaultFieldsTypeAsList：{}", jdbcUtil.getDefaultFieldsTypeAsList(table_name));
    }

    @Test
    public void executeCall() throws SQLException {
        String sql = "call DBMS_STATS.GATHER_TABLE_STATS('BISHOW','ALARM_FBSCJ_MONITOR',degree=>64)";
        sql = "begin DBMS_STATS.GATHER_TABLE_STATS('WEB','TOOLUI_TASK',degree => 64) ; end;";
//        boolean ret = jdbcUtil.executeCall(sql);
        int ret = jdbcUtil.executeUpdate(sql);
        logger.info("sql：{}，ret：{}", sql, ret);
    }

    @Test
    public void multiExecute() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        MultiQuery f = new MultiQuery("1", "f", "2021082823");
        MultiQuery m = new MultiQuery("1", "m", "2021082823");
        MultiQuery s = new MultiQuery("1", "s", "2021082823");
        MultiQuery c = new MultiQuery("1", "c", "2021082823");
        MultiBatch mb = new MultiBatch(10000);
        executor.submit(mb);
        for (int i = 0; i < 10; i++) {
            executor.submit(f);
            executor.submit(m);
            executor.submit(s);
            executor.submit(c);
        }
        SleepUtil.sleepMilliSecond(6000);
    }

    @Test
    public void interfaceExtendTest() {
        infTest(new Q2() {
            @Override
            public String getType() {
                return "q2";
            }

            @Override
            public String getData() {
                return "q2Data";
            }
        });
        infTest(new Q1() {
            @Override
            public String getData() {
                return "q1Data";
            }
        });
    }

    private void infTest(Q1 q1) {
        if (q1 instanceof Q2) {
            logger.info("getType：{}", ((Q2) q1).getType());
        } else {
            logger.info("getData：{}", q1.getData());
        }
//        Q2 q2 = (Q2) q1;
//        logger.info("getData：{}，getType：{}", q2.getData(), q2.getType());
    }

    @Test
    public void test79Mysql() throws SQLException {
        jdbcUtil.executeQuery("select 1");
    }

    interface Q1 {
        String getData();
    }

    interface Q2 extends Q1 {
        String getType();
    }

    class ConcurrentQuery1 extends BaseRunableThread {
        @Override
        protected void runnableExec() throws Exception {
            queryTest1();
            SleepUtil.sleepMilliSecond(500);
        }
    }

    class ConcurrentQuery2 extends BaseRunableThread {
        @Override
        protected void runnableExec() throws Exception {
            queryTest2();
            SleepUtil.sleepMilliSecond(500);
        }
    }

    class MultiBatch implements Runnable {
        int max_cnt = 10000;

        MultiBatch(int max_cnt) {
            this.max_cnt = max_cnt;
        }

        @Override
        public void run() {
            int cnt = 0;
            List<String> sqlList = new ArrayList<>();
            String insertSql = "insert into multi_test_list(task_id,file_name) values(1,'abcd.txt')";
            sqlList.add(insertSql);
            while (cnt < max_cnt) {
                try {
                    jdbcUtil.executeBatch(sqlList);
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
                cnt++;
            }
        }
    }

    class MultiQuery implements Callable<MultiTestBean> {
        String task_id;
        String task_type;
        String task_cycle;
        TimeCostUtil tc = new TimeCostUtil();

        MultiQuery(String task_id, String task_type, String task_cycle) {
            this.task_id = task_id;
            this.task_type = task_type;
            this.task_cycle = task_cycle;
        }

        @Override
        public MultiTestBean call() throws Exception {
            // 先update
            String updateSql = "update multi_test set file_size=file_size+10 "
                    + " where task_id='"
                    + task_id + "' and task_type='"
                    + task_type + "' and task_cycle='"
                    + task_cycle + "'";
            jdbcUtil.executeUpdate(updateSql);
            // 在查询
            tc.start();
            String sql = "select task_id,task_name,task_type,task_cycle,file_size from multi_test "
                    + " where task_id='"
                    + task_id + "' and task_type='"
                    + task_type + "' and task_cycle='"
                    + task_cycle + "'";
            List<MultiTestBean> multiTestBeanList = jdbcUtil.executeQuery(sql, MultiTestBean.class);
            MultiTestBean result = (multiTestBeanList != null && multiTestBeanList.size() > 0) ? multiTestBeanList.get(0) : null;
            logger.info("cost：{}，{}", tc.stopAndGet(), result);
            return result;
        }
    }
}