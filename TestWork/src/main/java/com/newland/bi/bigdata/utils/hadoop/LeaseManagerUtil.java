package com.newland.bi.bigdata.utils.hadoop;

import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;
import org.apache.hadoop.util.Time;

/**
 * 租约管理工具
 *
 * @author chenqixu
 */
public class LeaseManagerUtil {

    private static Logger logger = LogInfoFactory.getInstance(LeaseManagerUtil.class);
    org.apache.hadoop.hdfs.server.namenode.FSNamesystem a;

    public void test() {
        long t1 = Time.monotonicNow();
        logger.info("t1：{}", t1);
    }

    public static void main(String[] args) {
        LeaseManagerUtil leaseManagerUtil = new LeaseManagerUtil();
        leaseManagerUtil.test();
    }
}
