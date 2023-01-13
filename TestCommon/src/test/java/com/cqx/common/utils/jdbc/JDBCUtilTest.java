package com.cqx.common.utils.jdbc;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.hdfs.HdfsBean;
import com.cqx.common.utils.hdfs.HdfsTool;
import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultBean;
import com.cqx.common.utils.system.ArrayUtil;
import com.cqx.common.utils.system.ByteUtil;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

public class JDBCUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
    private static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    private static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";
    private IJDBCUtil jdbcUtil;
    private String jdbcBean;
    private ParamsParserUtil paramsParserUtil;

    @Before
    public void setUp() throws Exception {
        Map params = getParam("jdbc.yaml");
        paramsParserUtil = new ParamsParserUtil(params);
        // 从JVM参数中获取，使用方式：-Djdbc.bean=mysql79Bean
        jdbcBean = System.getProperty("jdbc.bean");
        DBBean dbBean;
        if (jdbcBean != null && jdbcBean.trim().length() > 0) {
            logger.info("获取到-Djdbc.bean={}", jdbcBean);
            dbBean = paramsParserUtil.getBeanMap().get(jdbcBean);
        } else {
//            dbBean = paramsParserUtil.getBeanMap().get("localmysqlBean");
//            dbBean = paramsParserUtil.getBeanMap().get("mysql79Bean");
            dbBean = paramsParserUtil.getBeanMap().get("fjhblog242Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("hadoopPostgreSql");
//            dbBean = paramsParserUtil.getBeanMap().get("oracle242Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("oracle12c_cctsys_dev_Bean");
//            dbBean = paramsParserUtil.getBeanMap().get("localAdbBean");
//            dbBean = paramsParserUtil.getBeanMap().get("adbBean");
//            dbBean = paramsParserUtil.getBeanMap().get("localoracleBean");
//            dbBean.setPool(false);
        }
//        jdbcUtil = new JDBCRetryUtil(dbBean, 30000, 30);
        jdbcUtil = new JDBCUtil(dbBean);
//        jdbcUtil = new JDBCUtil(dbBean, 1, 1, 1);
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
    public void insertJavaBeanClobTest() throws Exception {
        String fields = ":id,:name,:time,:description";
        String sql = "insert into cqx_test5 values(" + fields + ")";
        List<CqxTest5Bean> beans = new ArrayList<>();
        CqxTest5Bean bean1 = new CqxTest5Bean();
        bean1.setId(1234567890L);
        bean1.setName("test2");
        bean1.setTime(new java.sql.Date(new Date().getTime()));
        String description = "test2_clob_1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append(description);
        }
        logger.info("description.len: {}", sb.length());
        bean1.setDescription(sb.toString());
        beans.add(bean1);
        int ret = jdbcUtil.executeBatch(sql, beans, CqxTest5Bean.class, fields);
        logger.info("ret: {}", ret);

        List<CqxTest5Bean> queryList = jdbcUtil.executeQuery("select description from cqx_test5", CqxTest5Bean.class);
        for (CqxTest5Bean cqxTest5Bean : queryList) {
            logger.info("query：{}", cqxTest5Bean.getDescription());
        }
    }

    @Test
    public void postgreSqlTest() throws SQLException {
        jdbcUtil.executeQuery("select 1 from dual");
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
                    .buildQR("f_varchar", "java.lang.String", "org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:82) at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:53) at com.sun.proxy.$Proxy402.selectMainSite(Unknown Source) at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) at java.lang.reflect.Method.invoke(Method.java:498) at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:333) at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:190) at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:92) at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:213) at com.sun.proxy.$Proxy403.selectMainSite(Unknown Source) at com.ztesoft.local.sales.survey.service.impl.MvSiteInfoServiceImpl.selectMainSite(MvSiteInfoServiceImpl.java:143) at com.ztesoft.local.sales.survey.service.impl.MvSiteInfoServiceImpl$$FastClassBySpringCGLIB$$5daf860b.invoke() at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204) at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:721) at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157) at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:92) at ")
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
        String[] fields_array = ArrayUtil.arrayRemove(tab_fields_array, pks_array);

        String fields = ArrayUtil.arrayToStr(fields_array, ",");

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
        // 封装的信息收集方法
        jdbcUtil.gatherTableStats("WEB", "TOOLUI_TASK");
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

    /**
     * 会话测试
     */
    @Test
    public void sessionTest() throws Exception {
        List<String> sqls = new ArrayList<>();
        if (jdbcBean.equals("oracle242bishowBean")) {
            //============================
            // JDBC配置
            // -Djdbc.bean=oracle242bishowBean
            //============================
            // 会话测试1
            logger.info("==会话测试1，默认==");
            sqls.add("insert into cqx_test3(sum_date,load_time) values(20220708,sysdate)");
            sqls.add("select count(1) from cqx_test3");
            sqls.add("select sum_date,load_time from cqx_test3");
            sqls.add("delete from cqx_test3");
            jdbcUtil.execute(sqls);

            // 会话测试2
            logger.info("==会话测试2，使用alter session set time_zone方式==");
            sqls.add("select sessiontimezone from dual");
            sqls.add("ALTER SESSION SET TIME_ZONE='+08:00'");
            sqls.add("select to_char(to_timestamp_tz('1986-05-04 00:00:00.000000+08','YYYY-MM-DD hh24:mi:ss.FFTZH'), 'YYYY-MM-DD hh24:mi:ss.FFTZH:TZM') from dual");
            sqls.add("select sessiontimezone from dual");
            jdbcUtil.execute(sqls);
        } else if (jdbcBean.equals("oracle11g_xdload_Bean")) {
            //============================
            // JDBC配置
            // -Djdbc.bean=oracle11g_xdload_Bean
            //============================
            // 外部表测试1
            logger.info("==外部表测试1，使用alter session set time_zone方式==");
//            sqls.add("ALTER SESSION SET TIME_ZONE='Asia/Shanghai'");
            sqls.add("ALTER SESSION SET TIME_ZONE='+08:00'");
//            sqls.add("ALTER SESSION SET NLS_LANGUAGE='SIMPLIFIED CHINESE'");
//            sqls.add("ALTER SESSION SET NLS_TERRITORY='CHINA'");
            sqls.add("select count(*) from ET_GROUP_DUTY_CUSTOMER_JH");
            jdbcUtil.execute(sqls);
        }
        //============================
        // 任意oracle即可
        String userTimezone = System.getProperty("user.timezone");
        if (userTimezone != null && userTimezone.length() > 0) {
            // 时区测试1
            // -Duser.timezone=UTC+08:00
            // System.setProperty("user.timezone", "UTC+08:00");没有效果，因为加载顺序问题
            logger.info("==时区测试1，使用-Duser.timezone==");
            TimeZone tc = TimeZone.getDefault();
            logger.info("tc: {}, user.timezone: {}", tc, userTimezone);
            sqls.add("select to_char(to_timestamp_tz('1986-05-04 00:00:00.0','YYYY-MM-DD hh24:mi:ss.FFTZH'), 'YYYY-MM-DD hh24:mi:ss.FFTZH:TZM') from dual");
            jdbcUtil.execute(sqls);
        } else {
            // 时区测试2
            logger.info("==时区测试2，使用java代码中加载时区方式==");
            TimeZone tc = TimeZone.getTimeZone("GMT+08:00");
            TimeZone.setDefault(tc);
            logger.info("tc {}, user.timezone: {}", tc, userTimezone);
            sqls.add("select to_char(to_timestamp_tz('1986-05-04 00:00:00.0','YYYY-MM-DD hh24:mi:ss.FFTZH'), 'YYYY-MM-DD hh24:mi:ss.FFTZH:TZM') from dual");
            jdbcUtil.execute(sqls);
        }
    }

    @Test
    public void hexToStr() {
        String str = new String(ByteUtil.hexStringToBytes("17b4c94d7dfbbcf023ed720251c896e0aa787a070c0a2e0b0102010151020000817f01020000000000000001010103013100000000010707787a070c101a0100021fe801020102000622010100010a0000000702c10208010604da2f4ee2020f9801030000000001130001121253494d504c4946494544204348494e4553450110000105054348494e41010900010202a3a400000105054348494e410101000102022e2c0102000108085a4853313647424b010a00010909475245474f5249414e010c0001090944442d4d4f4e2d525201070001121253494d504c4946494544204348494e45534501080001060642494e415259010b00010e0e48482e4d492e535358464620414d01390001181844442d4d4f4e2d52522048482e4d492e535358464620414d013a0001121248482e4d492e535358464620414d20545a52013b00011c1c44442d4d4f4e2d52522048482e4d492e535358464620414d20545a52013c00010202a3a401340001060642494e41525901320001040442595445013d0001050546414c5345013e00010b0b800083e8bd3c3c8000000001a3000401010104010102057b0000010300030000000000000000000000000100010100000000214f52412d30313430333a20e69caae689bee588b0e4bbbbe4bd95e695b0e68dae0a"));
        logger.info(str);
    }

    @Test
    public void timezoneTest() throws Exception {
        // 绝对时间（AbsoluteTime）
        // [概念] 指向绝对时间线上的一个确定的时刻，不受所在地的影响
        // [概念举例] UTC时间，Unix时间戳，1970-01-01T00:00:00Z
        // [JAVA] Instant ZonedDateTime OffsetDateTime

        // 本地时间（LocalDateTime）
        // [概念] 本地时间仅仅是指的关于年月日、时分秒等信息的一个描述，并不包含所在的时区
        // [概念举例] 2020年8月24日 03:00
        // [JAVA] LocalDateTime

        // 时区偏移量（Offset）
        // 全球分为24个时区，每个时区和零时区相差了数个小时，也就是这里所说的时区偏移量Offset，比如东八区和零时区有+08:00的偏移量。
        // "北京时间2020年8月24日 03:00" 本质上是一个绝对时间，表示成UTC时间是2020-08-24T03:00:00+08:00，其中的2020-08-24T03:00:00是本地时间，而+08:00表示的是时区偏移量。
        // 绝对时间 = 本地时间 & 时区偏移量
        // 时区偏移量 = 地区 & 规则 （Offset = Zone & Rules）
        // 这里的规则（Rules）可能是一个变化的值，如果我们单纯地认为中国的时区偏移量是8个小时，就出错了，
        // 事实是，中国采用的不一定总是东八区时间，也可能是东九区时间，出现这个情况因为夏令时的存在（夏令时即DST时间，
        // 1992年之后中国已经没有再实行过夏令时了），当实行夏令时的时候，中国标准时间的时区偏移量就是+09:00。
        // 因此，一个地区的时区偏移量是多少，是由当地的政策决定的，可能会随着季节而发生变化，这就是上面所说的“规则”（Rules）。

        // "Asia/Shanghai"和"+08:00"是不同的时区描述，"Asia/Shanghai"只是描述了地区，而"+08:00"描述了准确的时区偏移量，不能将它们看作等价，比如在实行夏令时的时候，这两种描述得到的本地时间就是不同的。

        //=========================
        // 测试
        //=========================
        // Step#1：北京地区
        // 地区：
        ZoneId zoneOfBeijing = ZoneId.of("Asia/Shanghai");
        // 本地时间：2020-08-24 03:00
        LocalDateTime localDateTimeOfBeijing = LocalDateTime.of(2020, 8, 24, 3, 0);
        // 绝对时间：ZonedDateTime表示
        ZonedDateTime zonedDateTimeOfBeijing = ZonedDateTime.of(localDateTimeOfBeijing, zoneOfBeijing);

        // Step#1.1：上海地区
        // 地区：
        ZoneId zoneOfShanghai = ZoneId.of("Asia/Shanghai");
        // 本地时间：1986-05-04 15:00
        LocalDateTime localDateTimeOfShanghai = LocalDateTime.of(1986, 5, 4, 15, 0);
        // 绝对时间：ZonedDateTime表示
        ZonedDateTime zonedDateTimeOfShanghai = ZonedDateTime.of(localDateTimeOfShanghai, zoneOfShanghai);

        // Step#2：绝对时间转化成Instant类型（时间戳类型）
        Instant absoluteInstant = zonedDateTimeOfBeijing.toInstant();

        // Step#3：伦敦地区
        // 地区：
        ZoneId zoneOfLondon = ZoneId.of("Europe/London");
        // 绝对时间：将Instant绝对时间转化成伦敦地区的ZonedDateTime
        ZonedDateTime zonedDateTimeOfLondon = ZonedDateTime.ofInstant(absoluteInstant, zoneOfLondon);
        // 本地时间：
        LocalDateTime localDateTimeOfLondon = zonedDateTimeOfLondon.toLocalDateTime();

        logger.info("北京本地时间：{},", localDateTimeOfBeijing);
        logger.info("北京时区偏移量：{}", zonedDateTimeOfBeijing.getOffset());

        logger.info("上海本地时间：{}", localDateTimeOfShanghai);
        logger.info("上海时区偏移量：{}", zonedDateTimeOfShanghai.getOffset());

        logger.info("伦敦本地时间：{}", localDateTimeOfLondon);
        logger.info("伦敦时区偏移量：{}", zonedDateTimeOfLondon.getOffset());
        /*
         北京本地时间：2020-08-24T03:00
         北京时区偏移量：+08:00
         伦敦本地时间：2020-08-23T20:00
         伦敦时区偏移量：+01:00
         */
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

    @Test
    public void replaceDollar() {
        String _tmpValue = "aa$$$bb";
        _tmpValue = _tmpValue.replaceAll(Matcher.quoteReplacement("$$"), Matcher.quoteReplacement("\\$\\$"));
        logger.info(_tmpValue);
        if (_tmpValue.contains("$$")) {
            _tmpValue = _tmpValue.replaceAll(Matcher.quoteReplacement("$$"), Matcher.quoteReplacement("\\$\\$"));
            logger.info(_tmpValue);
        }
    }

    @Test
    public void hiveTest() throws Exception {
        // -Djdbc.bean=hiveHuaWeiBean
        HdfsBean hdfsBean = paramsParserUtil.getHdfsBeanMap().get("hacluster");
        hdfsBean.setHadoop_conf(getResourcePath(hdfsBean.getHadoop_conf()));
        hdfsBean.setKeytab(getResourcePath(hdfsBean.getKeytab()));
        hdfsBean.setKrb5(getResourcePath(hdfsBean.getKrb5()));
        hdfsBean.setJaas(getResourcePath(hdfsBean.getJaas()));
//        HdfsTool.zookeeperInitKerberos(hdfsBean.getHadoop_conf()
//                , hdfsBean.getPrincipal().replace("@HADOOP.COM", "")
//                , hdfsBean);
        HdfsTool.hiveInitKerberos(hdfsBean);

//        String connectionInfo = "10.1.12.79:24002,10.1.12.78:24002,10.1.12.75:24002";
//        ZookeeperTools zookeeperTools = ZookeeperTools.getInstance();
//        zookeeperTools.init(connectionInfo);
//        List<String> list = zookeeperTools.listForPath("/");
//        StringUtil.printList(list);
//        zookeeperTools.close();

        for (List<QueryResult> queryResults : jdbcUtil.executeQuery("select * from test1 limit 1")) {
            logger.info("1、{}", queryResults);
        }
        for (List<QueryResult> queryResults : jdbcUtil.executeQuery("show tables")) {
            logger.info("2、{}", queryResults);
        }
    }

    @Test
    public void hiveTxTest() throws Exception {
        // -Djdbc.bean=hiveHuaWeiBean
        HdfsBean hdfsBean = paramsParserUtil.getHdfsBeanMap().get("hacluster");
        hdfsBean.setHadoop_conf(getResourcePath(hdfsBean.getHadoop_conf()));
        hdfsBean.setKeytab(getResourcePath(hdfsBean.getKeytab()));
        hdfsBean.setKrb5(getResourcePath(hdfsBean.getKrb5()));
        hdfsBean.setJaas(getResourcePath(hdfsBean.getJaas()));
        HdfsTool.hiveInitKerberos(hdfsBean);

        List<String> sqls = new ArrayList<>();
        sqls.add("set hive.support.concurrency=true;");
        sqls.add("select 1");
        jdbcUtil.execute(sqls, new IJDBCUtilCall.ICallBack() {
            @Override
            public void call(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    logger.info("{}", rs.getObject(1));
                }
            }
        });
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