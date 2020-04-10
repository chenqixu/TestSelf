package com.cqx.sync;

import com.cqx.sync.bean.DBBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DBFullSyncTest {

    private DBFullSync dbFullSync;
    private OracleToMysql oracleToMysql;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() throws Exception {
        dbFullSync = new DBFullSync();
        Map<String, Object> params = generateParams();
        dbFullSync.init(params);
        dbFullSync.run();
    }

    private Map<String, Object> generateParams() {
        Map<String, Object> params = new HashMap<>();
        DBBean srcdbBean = new DBBean();
        srcdbBean.setDbType(DBType.MYSQL);
        srcdbBean.setTns("jdbc:mysql://10.1.8.200:3306/suyan_perf?useUnicode=true");
        srcdbBean.setUser_name("suyan");
        srcdbBean.setPass_word("suyan");
        params.put("srcdbBean", srcdbBean);
        DBBean dstdbBean = new DBBean();
        dstdbBean.setDbType(DBType.ORACLE);
        dstdbBean.setTns("jdbc:oracle:thin:@10.1.8.99:1521/orcl12cpdb1");
        dstdbBean.setUser_name("cctsys_dev");
        dstdbBean.setPass_word("cctsys_dev");
        params.put("dstdbBean", dstdbBean);
        // op_org_storage_res
        params.put("src_tab_name", "op_org_storage_res");
        params.put("dst_tab_name", "sm2_rsmgr_op_org_storage_res_fjedcprohd");
        params.put("src_fields", "org_id,amount");
        params.put("dst_fields", "org_id,amount");
        // op_org_computing_res
//        params.put("src_tab_name", "op_org_computing_res");
//        params.put("dst_tab_name", "sm2_rsmgr_op_org_computing_res_fjedcprohd");
//        params.put("src_fields", "org_id,amount,max_amount");
//        params.put("dst_fields", "org_id,amount,max_amount");
        return params;
    }

    @Test
    public void addressquerySync() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("edc_addressquery");
        dbBean.setPass_word("edc_addressquery");
        dbBean.setPool(false);
        oracleToMysql = new OracleToMysql(dbBean);
        try {
            Map<String, String> map = oracleToMysql.getAllTableAndFields();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String tableName = entry.getKey();
                String fields = entry.getValue();
                String dst_fields = fields;
                //mysql关键字，需要特殊处理
                if (dst_fields.contains("CONDITION")) {
                    dst_fields = dst_fields.replace("CONDITION", "`CONDITION`");
                }
                dbFullSync = new DBFullSync();
                Map<String, Object> params = generateAddressqueryParams(tableName, fields, tableName, dst_fields);
                dbFullSync.init(params);
                dbFullSync.run();
            }
        } finally {
            oracleToMysql.release();
        }
    }

    @Test
    public void addressquerySm2DIMSync() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("edc_addressquery");
        dbBean.setPass_word("edc_addressquery");
        dbBean.setPool(false);
        oracleToMysql = new OracleToMysql(dbBean);
        try {
            String tableName = "SM2_DIM";
            String fields = oracleToMysql.getTableFields(tableName);
            dbFullSync = new DBFullSync();
            Map<String, Object> params = generateAddressqueryParams(tableName, fields, tableName, fields);
            dbFullSync.init(params);
            dbFullSync.run();
        } finally {
            oracleToMysql.release();
        }
    }

    /**
     * 参数拼接
     *
     * @param src_tableName
     * @param src_fields
     * @param dst_tableName
     * @param dst_fields
     * @return
     */
    private Map<String, Object> generateAddressqueryParams(String src_tableName, String src_fields,
                                                           String dst_tableName, String dst_fields) {
        Map<String, Object> params = new HashMap<>();
        //源
        DBBean srcdbBean = new DBBean();
        srcdbBean.setDbType(DBType.ORACLE);
        srcdbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        srcdbBean.setUser_name("edc_addressquery");
        srcdbBean.setPass_word("edc_addressquery");
        params.put("srcdbBean", srcdbBean);
        //目标
        DBBean dstdbBean = new DBBean();
        dstdbBean.setDbType(DBType.MYSQL);
        dstdbBean.setTns("jdbc:mysql://10.1.8.200:3306/addressquery?useUnicode=true&characterEncoding=utf-8");
        dstdbBean.setUser_name("edc_addressquery");
        dstdbBean.setPass_word("edc_addressquery");
        params.put("dstdbBean", dstdbBean);
        //表名、字段
        params.put("src_tab_name", src_tableName);
        params.put("dst_tab_name", dst_tableName);
        params.put("src_fields", src_fields);
        params.put("dst_fields", dst_fields);
        return params;
    }
}