package com.cqx.common.utils.ftp;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FtpUtilAutoCloseTest {
    private FtpParamCfg ftpParamCfg;
    private String localFilePath = "d:\\tmp\\data\\dpi\\";//"d:\\Soft\\CLASS\\";//"d:\\tmp\\data\\dpi\\";
    private String remoteFilePath = "/bi/user/cqx/data/";
    private String localFileName = "sc.txt";//"jdk1.8.0_161.tar.gz";//"sc.txt";
    private AtomicInteger cnt = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception {
        ftpParamCfg = new FtpParamCfg("10.1.8.203", 21,
                "edc_base", "fLyxp1s*", false);
    }

    @Test
    public void uploadMultiActive() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int cnt = 5;
        for (int i = 0; i < cnt; i++) {
            FtpUploadCallable ftpUploadCallable = new FtpUploadCallable(5, FtpMode.ActiveMode);
            executor.submit(ftpUploadCallable);
        }
        executor.shutdown();
        executor.awaitTermination(5L, TimeUnit.SECONDS);
    }

    @Test
    public void uploadMultiPassive() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int cnt = 5;
        for (int i = 0; i < cnt; i++) {
            FtpUploadCallable ftpUploadCallable = new FtpUploadCallable(5, FtpMode.PassiveMode);
            executor.submit(ftpUploadCallable);
        }
        executor.shutdown();
        executor.awaitTermination(5L, TimeUnit.SECONDS);
    }

    @Test
    public void uploadOnceActive() throws IOException {
        try (FtpUtilAutoClose ftpUtilAutoClose = new FtpUtilAutoClose((ftpParamCfg))) {
            // 主动模式
            ftpUtilAutoClose.setFtpMode(FtpMode.ActiveMode);
            // 上传
            ftpUtilAutoClose.upload(localFilePath, remoteFilePath, localFileName, localFileName);
        }
    }

    @Test
    public void uploadOncePassive() throws IOException {
        try (FtpUtilAutoClose ftpUtilAutoClose = new FtpUtilAutoClose((ftpParamCfg))) {
            // 被动模式
            ftpUtilAutoClose.setFtpMode(FtpMode.PassiveMode);
            // 上传
            ftpUtilAutoClose.upload(localFilePath, remoteFilePath, localFileName, localFileName);
        }
    }

    class FtpUploadCallable implements Callable<Boolean> {
        int num;
        FtpMode ftpMode;

        FtpUploadCallable(int num, FtpMode ftpMode) {
            this.num = num;
            this.ftpMode = ftpMode;
        }

        @Override
        public Boolean call() throws Exception {
            try (FtpUtilAutoClose ftpUtilAutoClose = new FtpUtilAutoClose((ftpParamCfg))) {
                // 长连接
                ftpUtilAutoClose.setLongConnect(true);
                // 传输模式
                ftpUtilAutoClose.setFtpMode(ftpMode);
                for (int i = 0; i < num; i++) {
                    String remoteFileName = cnt.incrementAndGet() + localFileName;
                    ftpUtilAutoClose.upload(localFilePath, remoteFilePath, localFileName, remoteFileName);
                }
            }
            return null;
        }
    }
}