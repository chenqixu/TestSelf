package com.newland.bi.jkreport.scan;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class JKReportScanTest {

    private JKReportScan jkReportScan;

    @Before
    public void setUp() throws Exception {
        jkReportScan = new JKReportScan();
        jkReportScan.init();
    }

    @After
    public void tearDown() throws Exception {
        jkReportScan.close();
    }

    @Test
    public void scan() throws IOException {
        String scan_path = "hdfs://master75/user/bdoc/20/services/hdfs/17/yz/bigdata/if_upload_hb_netlog/[date:yyyyMMddHHmm]/[type]/[content]";
//        scan_path = "hdfs://master75/user/bdoc/20/services/hdfs/17/yz/bigdata/if_upload_hb_netlog/[date:yyyyMMddHHmm]/nat/[content]";
        jkReportScan.scan(scan_path);
//        java.sql.Timestamp.valueOf("");
//        java.sql.Date.valueOf("");
//        java.sql.Time.valueOf("");
    }
}