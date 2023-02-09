package com.cqx.sync;

import com.bussiness.bi.bigdata.time.TimeCostUtil;
import com.cqx.common.utils.compress.zip.ZipUtils;
import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.jdbc.*;
import com.cqx.common.utils.jdbc.lob.DefaultLobHandler;
import com.cqx.common.utils.jdbc.lob.LobHandler;
import com.cqx.common.utils.kafka.SchemaUtil;
import com.cqx.common.utils.sftp.SftpConnection;
import com.cqx.common.utils.sftp.SftpUtil;
import com.cqx.common.utils.xml.XMLParser;
import com.cqx.common.utils.xml.XMLParserElement;
import com.cqx.sync.bean.*;
import org.apache.avro.Schema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class JDBCUtilTest {

    private static Logger logger = LoggerFactory.getLogger(JDBCUtilTest.class);
    private JDBCUtil jdbcUtil;
    private String jdbcBean;

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
            case "flink":
                srcdbBean.setTns("jdbc:mysql://10.1.8.200:3306/flink?useUnicode=true");
                srcdbBean.setUser_name("flink");
                srcdbBean.setPass_word("flink");
                break;
        }
        return srcdbBean;
    }

    private DBBean oracleConfig(String type) {
        DBBean srcdbBean = new DBBean();
//        srcdbBean.setPool(false);
        srcdbBean.setDbType(DBType.ORACLE);
        switch (type) {
            case "dev":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.8.99:1521/orcl12cpdb1");
                srcdbBean.setUser_name("cctsys_dev");
                srcdbBean.setPass_word("TyM*2CnEm");
                break;
            case "local":
                srcdbBean.setTns("jdbc:mysql://127.0.0.1:3306/jutap?useUnicode=true");
                srcdbBean.setUser_name("udap");
                srcdbBean.setPass_word("udap");
                break;
            case "jutap":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.0.242:1521:ywxx");
                srcdbBean.setUser_name("jutap_dev");
                srcdbBean.setPass_word("J%pSvi#o$7");
                break;
            case "jutap_tenant":
                srcdbBean.setTns("jdbc:oracle:thin:@10.46.158.219:1521:ywxx");
                srcdbBean.setUser_name("jutap_tenant");
                srcdbBean.setPass_word("jutap");
                break;
            case "frtbase_dblink":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521/orapri");
                srcdbBean.setUser_name("zyh");
                srcdbBean.setPass_word("zyh");
                break;
            case "web":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.0.242:1521:ywxx");
                srcdbBean.setUser_name("web");
                srcdbBean.setPass_word("T%vdNV#i$2");
                break;
            case "bishow":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.0.242:1521:ywxx");
                srcdbBean.setUser_name("bishow");
                srcdbBean.setPass_word("C%MuhN#q$4");
                break;
            case "receng":
                srcdbBean.setTns("jdbc:oracle:thin:@10.1.8.99:1521/orcl12cpdb1");
                srcdbBean.setUser_name("receng_dev");
                srcdbBean.setPass_word("receng_dev");
                break;
        }
        return srcdbBean;
    }

    private DBBean postgresqlConfig(String type) {
        DBBean srcdbBean = new DBBean();
        srcdbBean.setPool(false);
        srcdbBean.setDbType(DBType.POSTGRESQL);
        switch (type) {
            case "dev":
                srcdbBean.setTns("jdbc:postgresql://10.1.8.206:5432/sentry");
                srcdbBean.setUser_name("sentry");
                srcdbBean.setPass_word("sentry");
                break;
        }
        return srcdbBean;
    }

    @Before
    public void setUp() throws Exception {
        DBBean srcdbBean;
        // 从JVM参数中获取，使用方式：-Djdbc.bean=mysql79Bean
        final String INFO = "[格式]name@type, type in [oracle, mysql, postgresql]";
        jdbcBean = System.getProperty("jdbc.bean");
        if (jdbcBean != null && jdbcBean.trim().length() > 0) {
            logger.info("获取到-Djdbc.bean={}", jdbcBean);
            String[] _jdbcBeanArray = jdbcBean.split("@", -1);
            String jdbcName;
            String dbType;
            if (_jdbcBeanArray.length == 2) {
                jdbcName = _jdbcBeanArray[0];
                dbType = _jdbcBeanArray[1];
                logger.info("-Djdbc.bean其中jdbcName={}, dbType={}", jdbcName, dbType);
                switch (dbType) {
                    case "oracle":
                        srcdbBean = oracleConfig(jdbcName);
                        break;
                    case "mysql":
                        srcdbBean = mysqlConfig(jdbcName);
                        break;
                    case "postgresql":
                        srcdbBean = postgresqlConfig(jdbcName);
                        break;
                    default:
                        throw new NullPointerException("-Djdbc.bean中的dbType格式不正确！" + INFO);
                }
            } else {
                throw new NullPointerException("-Djdbc.bean格式不正确！" + INFO);
            }
        } else {
            throw new NullPointerException("需要设置-Djdbc.bean, " + INFO);
        }
//        srcdbBean = oracleConfig("jutap_tenant");
//        srcdbBean = oracleConfig("frtbase_dblink");
//        srcdbBean = oracleConfig("jutap");
//        srcdbBean = oracleConfig("dev");
//        srcdbBean = oracleConfig("web");
//        srcdbBean = oracleConfig("bishow");
//        srcdbBean = oracleConfig("receng");
//        srcdbBean = mysqlConfig("local");
//        srcdbBean = mysqlConfig("flink");
//        srcdbBean = postgresqlConfig("dev");
        jdbcUtil = new JDBCUtil(srcdbBean);
    }

    @After
    public void tearDown() throws Exception {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    @Test
    public void executeQuery() throws Exception {
        String sql = "select max(collect_time) as collect_time from t_job_stat";
        List<List<QueryResult>> queryResults = jdbcUtil.executeQuery(sql);
        System.out.println(queryResults);
        jdbcUtil.close();
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
    public void executeBatchFlink() throws Exception {
        String fields = ":sum_time,:parallelism,:sum_cnt";
        String sql = "insert into mc_throughput(sum_time,parallelism,sum_cnt) values(?,?,?)";
        List<ThroughputBean> beans = new ArrayList<>();
        ThroughputBean throughputBean = new ThroughputBean();
        throughputBean.setParallelism(1);
        throughputBean.setSum_cnt(1000L);
        logger.info("throughputBean : {}", throughputBean.getSum_time());
        beans.add(throughputBean);
        jdbcUtil.executeBatch(sql, beans, ThroughputBean.class, fields);
    }

    /**
     * select * from cqx_test1;
     * drop table cqx_test1;
     * create table cqx_test1(id varchar2(200));
     *
     * @throws Exception
     */
    @Test
    public void executeBatchException() throws Exception {
        String fields = ":id";
        String sql = "insert into cqx_test1(id) values(" + fields + ")";
        List<CqxTest1> beans = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 2001; i++) {
            int v = random.nextInt(200);
            beans.add(new CqxTest1(v + ""));
        }
//        jdbcUtil.setBatchNum(10);
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
//        jdbcUtil.executeBatch(sql, beans, CqxTest1.class, fields);
        jdbcUtil.executeBatchRetry(sql, beans, CqxTest1.class, fields);
        timeCostUtil.stop();
        logger.info("cost：{}", timeCostUtil.getCost());
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
        sql = "select to_date(:start_time,'yyyy-mm-dd hh24:mi:ss') from dual";
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

    @Test
    public void getParam1() {
        String sql = "select * from sm2_rsmgr_cluster where type_id like %:type_id% and resource_id=:resource_id";
        sql = "select to_date(:start_time,'yyyy-mm-dd hh24:mi:ss') from dual";
        //解析sql，找到所有的:xx，比如：select a from b where a=:a and b=:b and c>=:c and (d=:d)
        String[] params = (sql + " ").split(":", -1);
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i < params.length; i++) {
            String _tmp = params[i];
            //找到空格、括号等等就返回
            String key = jdbcUtil.getParam(_tmp, ")", 0);
            logger.info(String.format("before：%s：【%s】", params[i], key));
            if (jdbcUtil.paramEndWith(key)) {
                paramList.add(key);
            } else {
                key = null;
            }
            logger.info(String.format("deal：%s：【%s】", params[i], key));
            if (key != null) sql = sql.replace(":" + key, "?");
        }
        logger.info("sql：{}", sql);
        logger.info("paramList：{}", paramList);
    }

    @Test
    public void queryBlob() throws Exception {
        final LobHandler lobHandler = new DefaultLobHandler();
        jdbcUtil.executeQuery(
                "select fstream from extern_flowtask_cfg where id='102604273664'"
                , new IJDBCUtilCall.ICallBack() {
                    @Override
                    public void call(ResultSet rs) throws SQLException {
                        InputStream inputStream = null;
                        try {
                            inputStream = lobHandler.getBlobAsBinaryStream(rs, 1);
                            ZipUtils zipUtils = new ZipUtils();
                            Map<String, String> paramFileMap = zipUtils.unZip(inputStream);
                            logger.info("paramFileMap：{}", paramFileMap.keySet());
                            inputStream.reset();
                            String xml = zipUtils.unZip(inputStream, "node");
                            logger.info("xmlData：{}", xml);
                            XMLParser xmlParser = new XMLParser();
                            xmlParser.setXmlData(xml);
                            xmlParser.init();
                            List<XMLParserElement> actionList = xmlParser.parseRootChildElement("action");
                            List<XMLParserElement> dogList = xmlParser.getChildElement(actionList, "dog");
                            for (XMLParserElement xmlParserElement : dogList) {
                                logger.info("dogXml：{}", xmlParserElement.toXml());
                            }
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
        );
    }

    @Test
    public void queryClob() throws Exception {
        final LobHandler lobHandler = new DefaultLobHandler();
        jdbcUtil.executeQuery(
                "select avsc from nmc_schema where schema_name='SCHEMA_USER_ADDITIONAL_INFO'"
                , new IJDBCUtilCall.ICallBack() {
                    @Override
                    public void call(ResultSet rs) throws SQLException {
                        SchemaUtil schemaUtil = new SchemaUtil(null);
                        String db_avsc = lobHandler.getClobAsString(rs, 1);
                        Schema db_schema = schemaUtil.getSchemaByString(db_avsc);
                        logger.info("db_schema：{}", db_schema);
                        String sftp_avsc = sftpGetAVSC();
                        Schema sftp_schema = schemaUtil.getSchemaByString(sftp_avsc);
                        logger.info("sftp_schema：{}", sftp_schema);
                        logger.info("equals：{}", db_schema.equals(sftp_schema));
                    }
                }
        );
    }

    @Test
    public void getTableMetaData() throws SQLException {
        //元数据
        for (QueryResult queryResult : jdbcUtil.getTableMetaData("test_1")) {
            logger.info("元数据 {}", queryResult);
        }
        //查询
        for (List<QueryResult> queryResults : jdbcUtil.executeQuery("select * from test_1")) {
            for (QueryResult queryResult : queryResults) {
                logger.info("查询 {}", queryResult);
            }
        }
    }

    @Test
    public void getDblinkTableMetaData() throws SQLException {
        //元数据
        for (QueryResult queryResult : jdbcUtil.getTableMetaData("FRTBASE.LOCATE_MART_ROTATE_TAB@to_frtbase")) {
            logger.info("元数据 {}", queryResult);
        }
    }

    @Test
    public void selectPostgresql() throws Exception {
        List<List<QueryResult>> qs = jdbcUtil.executeQuery("select * from rec_mkt_mutual_relation limit 1");
        for (List<QueryResult> q : qs) {
            for (QueryResult qr : q) {
                logger.info("{}", qr);
            }
        }
    }

    private String sftpGetAVSC() {
        StringBuilder sb = new StringBuilder();
        FtpParamCfg ftpParamCfg = new FtpParamCfg("10.1.8.203", 22, "edc_base", "fLyxp1s*");
        try (SftpConnection sftpConnection = SftpUtil.getSftpConnection(ftpParamCfg)
             ; InputStream inputStream = SftpUtil.ftpFileDownload(sftpConnection
                , "/bi/user/cqx/data/avsc/"
                , "FRTBASE.TB_SER_OGG_USER_ADDI_INFO.avsc")) {
            //设置SFTP下载缓冲区
            byte[] buffer = new byte[2048];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, c));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return sb.toString();
    }
}