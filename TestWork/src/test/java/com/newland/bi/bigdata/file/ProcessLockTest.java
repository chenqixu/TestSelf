package com.newland.bi.bigdata.file;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.Test;

public class ProcessLockTest {

    private ProcessLock processLock = new ProcessLock();

    @Test
    public void tryLock() {
        boolean lock = processLock.tryLock("D:/tmp/");
        System.out.println(lock);
        SleepUtils.sleepSecond(30);
    }

    @Test
    public void tryLock2() throws Exception {
        boolean lock = processLock.tryLock("D:/tmp/", 3);
        System.out.println(lock);
    }
}