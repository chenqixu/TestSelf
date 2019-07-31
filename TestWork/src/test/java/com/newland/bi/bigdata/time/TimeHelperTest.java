package com.newland.bi.bigdata.time;

import org.junit.Test;

public class TimeHelperTest {

    @Test
    public void timeComparison() {
    }

    @Test
    public void timeSubtract() {
        long ret = TimeHelper.timeSubtract("20181204172300", "20181204224600", "yyyyMMddHHmmss");
        System.out.println(ret);
    }

    @Test
    public void timestampToDate() {
        TimeHelper.timestampToDate(1562648342695L);
    }
}