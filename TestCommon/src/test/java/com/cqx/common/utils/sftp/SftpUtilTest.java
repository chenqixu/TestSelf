package com.cqx.common.utils.sftp;

import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.ftp.ProxyBean;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class SftpUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(SftpUtilTest.class);
    private SftpConnection sftpConnection;
    private SftpConnection sftpConnectionProxy;
    private FtpParamCfg ftpParamCfg;

    @Before
    public void setUp() throws Exception {
//        ftpParamCfg = new FtpParamCfg("192.168.230.128", 22, "hadoop", "hadoop");
        ftpParamCfg = new FtpParamCfg("10.1.8.203", 22, "edc_base", "fLyxp1s*");
//        ftpParamCfg = new FtpParamCfg("10.1.8.204", 22, "edc_base", "fLyxp1s*");
        sftpConnection = SftpUtil.getSftpConnection(ftpParamCfg);

        FtpParamCfg ftpParamCfg213 = new FtpParamCfg("10.47.248.213", 22, "edc_base", "aBc4Y5L6");
        ProxyBean proxyBean = new ProxyBean();
        proxyBean.setProxyHost("10.1.2.199");
        proxyBean.setProxyPort(8123);
        sftpConnectionProxy = SftpUtil.getSftpConnection(ftpParamCfg213, proxyBean);
    }

    @After
    public void tearDown() throws Exception {
        if (sftpConnection != null) SftpUtil.closeSftpConnection(sftpConnection);
        if (sftpConnectionProxy != null) SftpUtil.closeSftpConnection(sftpConnectionProxy);
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
    public void uploadByProxy() {
        String file = "LTE_S1UHTTP_010531112002_20190507000000.txt";
        String local_path = "d:\\tmp\\data\\dpi\\dpi_ltedata\\";
        String remote_path = "/bi/data/";
        SftpUtil.upload(sftpConnectionProxy, local_path + file, remote_path + file);
    }

    @Test
    public void submit() {
        ThreadTool threadTool = new ThreadTool(5, 200);
        for (int i = 0; i < 80; i++) {
            threadTool.addTask(new Runnable() {
                @Override
                public void run() {
                    logger.info("{} start", this);
                    Random random = new Random();
                    int r = random.nextInt(5000);
                    logger.info("{} sleep：{}", this, r);
                    SleepUtil.sleepMilliSecond(r);
                    logger.info("{} end", this);
                }
            });
        }
        threadTool.startTask();
    }

    @Test
    public void muSubmit() {
        final ThreadTool threadTool = new ThreadTool(10, 50);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Random random = new Random();
                    int r = random.nextInt(50);
                    SleepUtil.sleepMilliSecond(r);
                    logger.info("{} Sleep {}，addTask", "T1", r);
                    threadTool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            Random random = new Random();
                            int r = random.nextInt(500);
                            logger.info("Task {} sleep：{}", this, r);
                            SleepUtil.sleepMilliSecond(r);
                        }
                    });
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Random random = new Random();
                    int r = random.nextInt(50);
                    SleepUtil.sleepMilliSecond(r);
                    logger.info("{} Sleep {}，addTask", "T2", r);
                    threadTool.addTask(new Runnable() {
                        @Override
                        public void run() {
                            Random random = new Random();
                            int r = random.nextInt(500);
                            logger.info("Task {} sleep：{}", this, r);
                            SleepUtil.sleepMilliSecond(r);
                        }
                    });
                }
            }
        });
        t1.start();
        t2.start();
        SleepUtil.sleepMilliSecond(500);
        threadTool.startTask();
    }

    @Test
    public void ftpFileDownload() {
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = SftpUtil.ftpFileDownload(sftpConnection
                , "/bi/user/cqx/data/avsc/"
                , "FRTBASE.TB_SER_OGG_USER_ADDI_INFO.avsc")) {
            //设置SFTP下载缓冲区
            byte[] buffer = new byte[2048];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, c));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("{}", sb.toString());
    }
}