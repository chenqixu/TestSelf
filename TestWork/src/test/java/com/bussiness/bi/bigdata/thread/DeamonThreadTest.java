package com.bussiness.bi.bigdata.thread;

import org.junit.Test;

public class DeamonThreadTest {

    @Test
    public void deamonTest1() throws Exception {
        new DeamonThread(2).deamonTest1();
    }

    @Test
    public void deamonTest2() throws Exception {
        new DeamonThread(2).deamonTest2();
    }

    @Test
    public void deamonTest3() throws Exception {
        new DeamonThread(2).deamonTest3();
    }
}