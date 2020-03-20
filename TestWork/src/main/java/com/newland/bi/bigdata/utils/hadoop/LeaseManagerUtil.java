package com.newland.bi.bigdata.utils.hadoop;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.apache.hadoop.util.Time;

/**
 * 租约管理工具
 *
 * @author chenqixu
 */
public class LeaseManagerUtil {

    private static MyLogger logger = MyLoggerFactory.getLogger(LeaseManagerUtil.class);
    org.apache.hadoop.hdfs.server.namenode.FSNamesystem a;

    public static void main(String[] args) {
        LeaseManagerUtil leaseManagerUtil = new LeaseManagerUtil();
        leaseManagerUtil.test();
    }

    public void test() {
        long t1 = Time.monotonicNow();
        logger.info("t1：{}", t1);
    }
}
