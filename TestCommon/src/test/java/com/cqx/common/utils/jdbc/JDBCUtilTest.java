package com.cqx.common.utils.jdbc;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseRunableThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JDBCUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private JDBCRetryUtil jdbcUtil;

    @Before
    public void setUp() throws Exception {
        Map params = getParam("jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("localmysqlBean");
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("hadoopPostgreSql");
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("oracle242Bean");
        DBBean dbBean = paramsParserUtil.getBeanMap().get("adbBean");
        dbBean.setPool(false);
        jdbcUtil = new JDBCRetryUtil(dbBean, 30000, 30);
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

    private void queryTest1() {
        TimeCostUtil exec = new TimeCostUtil();
        exec.start();
        List<List<QueryResult>> results = jdbcUtil.executeQuery("select user_id from sm2_user");
        exec.stop();
        logger.info("{} result.size：{}，cost：{}", Thread.currentThread().getName(),
                (results != null ? results.size() : 0), exec.stopAndGet());
    }

    private void queryTest2() {
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
    public void postgreSqlTest() {
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

    @Test
    public void executeBatchSqlsInsertTest() throws Exception {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("adbBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("f_varchar", "java.lang.String", "test")
                    .buildQR("f_boolean", "java.lang.Boolean", false)
                    .buildQR("f_timestamp", "java.sql.TimeStamp", new java.sql.Timestamp(new Date().getTime()))
                    .buildQR("f_date", "java.sql.Date", new java.sql.Date(new Date().getTime()))
                    .buildQR("f_time", "java.sql.Time", new java.sql.Time(new Date().getTime()))
                    .buildQR("f_decimal", "long", 591500319216463L)
                    .buildQR("f_pk", "long", 123L)
                    .toList()
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
            op_types.add("i");
            op_types.add("i");
            String table = "test1";
            String[] fields = {"f_varchar", "f_boolean", "f_timestamp", "f_date", "f_time"};
            String[] fields_type = {"java.lang.String", "boolean", "java.sql.Timestamp", "java.sql.Date", "java.sql.Time"};
            String[] pks = {"f_decimal", "f_pk"};
            String[] pks_type = {"long", "long"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type, pks, pks_type, false, true);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
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
}