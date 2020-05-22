package com.cqx.common.utils.sftp;

import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SftpUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(SftpUtilTest.class);
    private SftpConnection sftpConnection;

    @Before
    public void setUp() throws Exception {
//        FtpParamCfg ftpParamCfg = new FtpParamCfg("192.168.230.128", 22, "hadoop", "hadoop");
        FtpParamCfg ftpParamCfg = new FtpParamCfg("10.1.8.204", 22, "edc_base", "fLyxp1s*");
        sftpConnection = SftpUtil.getSftpConnection(ftpParamCfg);
    }

    @After
    public void tearDown() throws Exception {
        if (sftpConnection != null) SftpUtil.closeSftpConnection(sftpConnection);
    }

    @Test
    public void upload() {
        String file = "LTE_S1UHTTP_010531112002_20190507000000.txt";
        String local_path = "d:\\tmp\\data\\dpi\\dpi_ltedata\\";
        String remote_path = "/home/hadoop/data/hblog/";
        SftpUtil.upload(sftpConnection, local_path + file, remote_path + file);
        file = "LTE_S1UOTHER_008388682002_20190411080100.txt";
        SftpUtil.upload(sftpConnection, local_path + file, remote_path + file);
    }

    @Test
    public void submit() {
        final AtomicLong run = new AtomicLong();
        final AtomicLong end = new AtomicLong();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            threadList.add(new Thread() {
                public void run() {
                    //运行
                    run.incrementAndGet();
                    logger.info("{} start", this);
                    Random random = new Random();
                    int r = random.nextInt(1000);
                    logger.info("{} sleep：{}", this, r);
                    SleepUtil.sleepMilliSecond(r);
                    //完成
                    end.incrementAndGet();
                    logger.info("{} end", this);
                }
            });
        }
        while (threadList.size() > 0) {
            long running = run.get() - end.get();
            logger.info("running {}，all_task：{}", running, threadList.size());
            if (running < 5) {
                //启动5-cnt个线程
                long start_num = 5 - running;
                long s_num = 0;
                logger.info("start_num {}", start_num);
                //找到NEW的，启动它
                Iterator<Thread> it = threadList.iterator();
                while (it.hasNext()) {
                    Thread t = it.next();
                    if (t.getState().equals(Thread.State.NEW)) {
                        if (s_num == start_num) break;
                        t.start();
                        s_num++;
                    } else if (t.getState().equals(Thread.State.TERMINATED)) {
                        it.remove();
                    }
                }
            }
            SleepUtil.sleepMilliSecond(50);
        }
    }
}