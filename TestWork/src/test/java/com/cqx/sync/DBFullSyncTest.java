package com.cqx.sync;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DBFullSyncTest {

    private DBFullSync dbFullSync;

    @Before
    public void setUp() throws Exception {
        dbFullSync = new DBFullSync();
        Map<String, Object> params = generateParams();
        dbFullSync.init(params);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() throws Exception {
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
}