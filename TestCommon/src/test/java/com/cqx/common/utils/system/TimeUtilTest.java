package com.cqx.common.utils.system;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;

public class TimeUtilTest {

    @Test
    public void devtiation() {
        System.out.println(TimeUtil.devtiation(Calendar.HOUR, Integer.valueOf("00"), "yyyy-MM-dd HH"));
        System.out.println(TimeUtil.devtiation(Calendar.HOUR, Integer.valueOf("01"), "yyyy-MM-dd HH"));
    }

    @Test
    public void currentTimeMillis() throws ParseException {
        System.out.println(TimeUtil.formatTime("2023-09-01 01:00:00", "yyyy-MM-dd HH:mm:ss"));
        System.out.println(System.currentTimeMillis());
    }
}