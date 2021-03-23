package com.newland.bi.bigdata.time;

import org.junit.Test;

import java.util.Date;

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

    @Test
    public void timeAdd() throws Exception {
        String time1 = "20190603110100";
        String time2 = "20201230110100";
        String format = "yyyyMMddHHmmss";
        Date date1 = TimeHelper.strToDate(time1, format);
        Date date2 = TimeHelper.strToDate(time2, format);
        System.out.println(date2.getTime() - date1.getTime());//49766400000
        String newTime = TimeHelper.strAddLong(time1, format, 49766400000L);
        System.out.println(newTime);
        //转成日期，计算当前日期和程序中的差距多少，再补充
        //或者直接替换日期即可
    }
}