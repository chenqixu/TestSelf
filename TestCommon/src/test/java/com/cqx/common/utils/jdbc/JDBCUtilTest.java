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
        DBBean dbBean = paramsParserUtil.getBeanMap().get("hadoopPostgreSql");
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

    @Test
    public void postgreSqlTest() {
        jdbcUtil.executeQuery("select 1");
    }
}