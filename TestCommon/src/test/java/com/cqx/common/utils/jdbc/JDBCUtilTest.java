package com.cqx.common.utils.jdbc;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
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

public class JDBCUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private IJDBCUtil jdbcUtil;

    @Before
    public void setUp() throws Exception {
        Map params = getParam("jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("localmysqlBean");
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("hadoopPostgreSql");
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("oracle242Bean");
//        DBBean dbBean = paramsParserUtil.getBeanMap().get("localAdbBean");
        DBBean dbBean = paramsParserUtil.getBeanMap().get("adbBean");
        dbBean.setPool(false);
//        jdbcUtil = new JDBCRetryUtil(dbBean, 30000, 30);
        jdbcUtil = new JDBCUtil(dbBean);
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

    private void queryTest1() throws SQLException {
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
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, false, MergeEnum.MERGE_INTO_ONLY);
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
        String table_name = "qry_sell_task";
        String[] fields_array = {"sell_id", "sell_place", "sell_count", "create_time"};
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

    class MergeBean implements IJDBCUtilCall.IQueryResultBean {
        private String op_type;
        private List<QueryResult> queryResults;

        @Override
        public String getOp_type() {
            return op_type;
        }

        public void setOp_type(String op_type) {
            this.op_type = op_type;
        }

        @Override
        public List<QueryResult> getQueryResults() {
            return queryResults;
        }

        public void setQueryResults(List<QueryResult> queryResults) {
            this.queryResults = queryResults;
        }
    }
}