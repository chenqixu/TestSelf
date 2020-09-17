package com.newland.bi.bigdata.txt;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileListLogTest {

    private FileListLog fileListLog;

    @Before
    public void setUp() throws Exception {
        fileListLog = new FileListLog("d:\\tmp\\data\\jk\\", "yyyyMMddHHmm");
        fileListLog.startMonitor();
    }

    @After
    public void tearDown() throws Exception {
        fileListLog.stopMonitor();
    }

    @Test
    public void exec() {
        for (int i = 0; i < 20; i++) {
            fileListLog.exec(i + ".txt");
            SleepUtil.sleepMilliSecond(1000);
        }
    }
}