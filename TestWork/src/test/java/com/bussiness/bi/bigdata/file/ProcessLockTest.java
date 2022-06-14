package com.bussiness.bi.bigdata.file;

import com.bussiness.bi.bigdata.file.ProcessLock;
import com.bussiness.bi.bigdata.utils.SleepUtils;
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