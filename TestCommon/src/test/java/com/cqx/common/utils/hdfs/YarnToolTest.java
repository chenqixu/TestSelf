package com.cqx.common.utils.hdfs;

import com.cqx.common.utils.io.MyByteArrayOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;

public class YarnToolTest {
    private YarnTool yarnTool;

    @Before
    public void setUp() throws Exception {
        HdfsBean hdfsBean = new HdfsBean();
        hdfsBean.setHadoop_conf("d:\\tmp\\etc\\hadoop\\confhw\\");
        hdfsBean.setAuth_type("kerberos");
        hdfsBean.setKeytab("d:\\tmp\\etc\\keytab\\yz_newland.keytab");
        hdfsBean.setKrb5("d:\\tmp\\etc\\keytab\\krb5.conf");
        hdfsBean.setPrincipal("yz_newland@HADOOP.COM");

        yarnTool = new YarnTool(hdfsBean);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void queryJobLog() throws Exception {
        yarnTool.queryJobLog("application_1667339730322_20836");
    }

    @Test
    public void getAllJob() throws Exception {
        yarnTool.getAllJob();
    }

    @Test
    public void getRunningJob() throws Exception {
        yarnTool.getRunningJob();
    }

    @Test
    public void streamTest() throws IOException {
        PrintStream ps = MyByteArrayOutputStream.buildPrintStream(value -> {
            System.out.println(value);
        });
        ps.println("123");
    }
}