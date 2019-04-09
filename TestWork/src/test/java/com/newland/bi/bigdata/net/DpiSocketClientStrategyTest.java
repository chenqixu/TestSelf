package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.Before;
import org.junit.Test;

public class DpiSocketClientStrategyTest {

    private DpiSocketClientStrategy dpiSocketClientStrategy;
    private String connectionInfo;

    @Before
    public void setUp() {
        connectionInfo = "192.168.230.128:2181";
        connectionInfo = "10.1.8.78:2182";
        dpiSocketClientStrategy = new DpiSocketClientStrategy(connectionInfo);
    }

    @Test
    public void tryLock() throws Exception {
        try {
            dpiSocketClientStrategy.tryLock();
            SleepUtils.sleepSecond(10);
        } finally {
            dpiSocketClientStrategy.close();
        }
    }
}