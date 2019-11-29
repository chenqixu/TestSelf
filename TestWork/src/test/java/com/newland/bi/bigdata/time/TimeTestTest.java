package com.newland.bi.bigdata.time;

import org.junit.Test;

public class TimeTestTest {

    @Test
    public void jodaTime() {
        Long micTime = TimeTest.getmicTime();
        System.out.println(micTime);
        System.out.println(TimeTest.parserMicTime(micTime));
    }

    @Test
    public void supplementZero() {
        System.out.println(TimeTest.getNow());
        System.out.println(TimeTest.supplementZero(""));
        System.out.println(TimeTest.supplementZero(null));
        System.out.println(TimeTest.supplementZero("20181114 01:01"));
        System.out.println(TimeTest.supplementZero("20181114"));
        System.out.println(TimeTest.supplementZero("2018111401"));
        System.out.println(TimeTest.supplementZero("201811140101"));
        System.out.println(TimeTest.supplementZero("20181114010101"));
    }

}