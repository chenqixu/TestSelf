package com.bussiness.bi.bigdata.metric;

import com.bussiness.bi.bigdata.metric.JvmMetrics;
import org.junit.Before;
import org.junit.Test;

public class JvmMetricsTest {

    private JvmMetrics jvmMetrics;

    @Before
    public void setUp() throws Exception {
        jvmMetrics = JvmMetrics.getVmInfo();
    }

    @Test
    public void getVmInfo() {
        System.out.println(jvmMetrics.toString());
        jvmMetrics.getDelta();
    }
}