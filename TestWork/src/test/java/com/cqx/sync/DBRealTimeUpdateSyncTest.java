package com.cqx.sync;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DBRealTimeUpdateSyncTest {

    private DBRealTimeUpdateSync dbRealTimeUpdateSync;

    @Before
    public void setUp() throws Exception {
        dbRealTimeUpdateSync = new DBRealTimeUpdateSync();
        dbRealTimeUpdateSync.init(generateParams());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() throws Exception {
        dbRealTimeUpdateSync.run();
    }

    private Map<String, Object> generateParams() {
        Map<String, Object> params = new HashMap<>();
        DBBean srcdbBean = new DBBean();
        srcdbBean.setDbType(DBType.MYSQL);
        srcdbBean.setTns("jdbc:mysql://127.0.0.1:3306/utap?useUnicode=true");
        srcdbBean.setUser_name("udap");
        srcdbBean.setPass_word("udap");
        params.put("srcdbBean", srcdbBean);
        DBBean dstdbBean = new DBBean();
//        dstdbBean.setDbType(DBType.ORACLE);
//        dstdbBean.setTns("jdbc:oracle:thin:@10.1.8.99:1521/orcl12cpdb1");
//        dstdbBean.setUser_name("cctsys_dev");
//        dstdbBean.setPass_word("cctsys_dev");
        dstdbBean.setDbType(DBType.MYSQL);
        dstdbBean.setTns("jdbc:mysql://127.0.0.1:3306/utap?useUnicode=true");
        dstdbBean.setUser_name("udap");
        dstdbBean.setPass_word("udap");
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