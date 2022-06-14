package com.bussiness.bi.bigdata.utils.system;

import com.bussiness.bi.bigdata.utils.system.MyShutDownHook;
import com.cqx.concurrent.ConcurrenceExec;
import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.junit.Test;

public class MyShutDownHookTest {
    @Test
    public void test() throws Exception {
        ConcurrenceExec concurrenceExec = new ConcurrenceExec();
        concurrenceExec.setBolt("MyShutDownHookTest", new MyShutDownHook(), 10);
        concurrenceExec.start();
        SleepUtils.sleepSecond(2);
    }
}