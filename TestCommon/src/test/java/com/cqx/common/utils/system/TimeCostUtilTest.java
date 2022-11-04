package com.cqx.common.utils.system;

import org.junit.Before;
import org.junit.Test;

public class TimeCostUtilTest {
    private TimeCostUtil tc;

    @Before
    public void setUp() {
        tc = new TimeCostUtil();
    }

    @Test
    public void checkByTimeFormat() {
        checkByTime(1);
        checkByTime(5);
        checkByTime(10);
        checkByTime(15);
        checkByTime(20);
        checkByTime(10);
        checkByTime(5);
        checkByTime(10);
        checkByTime(15);
        checkByTime(20);
        checkByTime(10);
    }

    private void checkByTime(long sleep) {
        SleepUtil.sleepMilliSecond(sleep * 1000L);
        System.out.println(String.format("sleep: %s, check: %s, now: %s"
                , sleep
                , tc.checkByTimeFormat("yyyyMMddHHmm")
                , TimeCostUtil.getNow("yyyy-MM-dd HH:mm:ss")));
    }
}