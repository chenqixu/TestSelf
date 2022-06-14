package com.bussiness.bi.bigdata.net;

import com.bussiness.bi.bigdata.net.DpiSocketClientStrategy;
import com.bussiness.bi.bigdata.utils.SleepUtils;
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

    @Test
    public void choiceMod() {
        String[] port_arr = {"10111", "10112", "10113"};
        dpiSocketClientStrategy.choiceMod(4, port_arr);
    }

    @Test
    public void choiceSequentialAllocation() {
        String[] port_arr = {"10111", "10112", "10113"};
        dpiSocketClientStrategy.choiceSequentialAllocation(4, port_arr);
    }
}