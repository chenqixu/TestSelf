package com.cqx.sync;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DBIncrementSyncTest {

    private DBIncrementSync dbIncrementSync;

    @Before
    public void setUp() throws Exception {
        dbIncrementSync = new DBIncrementSync();
        Map<String, Object> params = generateParams();
        dbIncrementSync.init(params);
    }

    @Test
    public void run() throws Exception {
        dbIncrementSync.run();
//        System.out.println(DBType.valueOf("MYSQL").getDriver());
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
        // t_scheduler_stat
//        params.put("sync_name", "t_scheduler_stat");
//        params.put("src_tab_name", "v_t_scheduler_stat");
//        params.put("dst_tab_name", "sm2_rsmgr_t_scheduler_stat");
//        params.put("src_fields", "queue_name,min_memory,min_vcores,max_memory,max_vcores,used_memory,used_vcores,collect_time");
//        params.put("dst_fields", "queue_name,min_memory,min_vcores,max_memory,max_vcores,used_memory,used_vcores,collect_time");
        // t_job_stat
//        params.put("sync_name", "t_job_stat");
//        params.put("src_tab_name", "t_job_stat");
//        params.put("dst_tab_name", "sm2_rsmgr_t_job_stat");
//        params.put("src_fields", "job_type,job_id,job_script,user,cpu_average_milliseconds,cpu_cores,cpu_max_milliseconds,memory_mb_multiply_ms,start_time,finish_time,collect_time,hdfs_read_bytes,state");
//        params.put("dst_fields", "job_type,job_id,job_script,job_user,cpu_average_milliseconds,cpu_cores,cpu_max_milliseconds,memory_mb_multiply_ms,start_time,finish_time,collect_time,hdfs_read_bytes,state");
        // zyh
//        params.put("sync_name", "zyh");
//        params.put("src_tab_name", "zyh");
//        params.put("dst_tab_name", "sm2_rsmgr_zyh");
//        params.put("src_fields", "queue_id,queue_desc,queue_name,queue_owner_orgid,queue_realname,queue_owner_id,queue_owner_name,queue_owner_orgdesc,queue_owner_orgname,queue_used_id,queue_used_name,queue_used_orgid,queue_used_orgdesc,queue_used_orgname,collect_time");
//        params.put("dst_fields", "queue_id,queue_desc,queue_name,queue_owner_orgid,queue_realname,queue_owner_id,queue_owner_name,queue_owner_orgdesc,queue_owner_orgname,queue_used_id,queue_used_name,queue_used_orgid,queue_used_orgdesc,queue_used_orgname,collect_time");
        // v_t_scheduler_stat1
        params.put("sync_name", "v_t_scheduler_stat");
        params.put("src_tab_name", "v_t_scheduler_stat1");
        params.put("dst_tab_name", "sm2_rsmgr_v_t_scheduler_stat");
        params.put("src_fields", "queue_id,queue_desc,queue_name,queue_owner_orgid,queue_realname,queue_owner_id,queue_owner_name,queue_owner_orgdesc,queue_owner_orgname,queue_used_id,queue_used_name,queue_used_orgid,queue_used_orgdesc,queue_used_orgname,min_memory,min_vcores,max_memory,max_vcores,used_memory,used_vcores,collect_time");
        params.put("dst_fields", "queue_id,queue_desc,queue_name,queue_owner_orgid,queue_realname,queue_owner_id,queue_owner_name,queue_owner_orgdesc,queue_owner_orgname,queue_used_id,queue_used_name,queue_used_orgid,queue_used_orgdesc,queue_used_orgname,min_memory,min_vcores,max_memory,max_vcores,used_memory,used_vcores,collect_time");
        return params;
    }
}