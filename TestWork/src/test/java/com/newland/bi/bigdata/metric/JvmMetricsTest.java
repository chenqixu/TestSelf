package com.newland.bi.bigdata.metric;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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