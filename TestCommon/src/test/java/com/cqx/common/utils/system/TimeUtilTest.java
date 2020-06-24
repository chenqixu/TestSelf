package com.cqx.common.utils.system;

import org.junit.Test;

import java.util.Calendar;

public class TimeUtilTest {

    @Test
    public void devtiation() {
        System.out.println(TimeUtil.devtiation(Calendar.HOUR, Integer.valueOf("00"), "yyyy-MM-dd HH"));
        System.out.println(TimeUtil.devtiation(Calendar.HOUR, Integer.valueOf("01"), "yyyy-MM-dd HH"));
    }
}