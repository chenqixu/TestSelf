package com.cqx.distributed.scan;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.distributed.bean.FileInfo;
import com.cqx.distributed.bean.FtpParamCfg;
import com.cqx.distributed.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描服务
 *
 * @author chenqixu
 */
public class ScanServer implements Runnable {
    private boolean isStop = false;
    private FtpParamCfg ftpParamCfg;

    public ScanServer(FtpParamCfg ftpParamCfg) {
        this.ftpParamCfg = ftpParamCfg;
    }

    public void run() {
        while (!isStop) {
            FTPClient ftpClient = null;
            try {
                //扫描
                ftpClient = FtpUtil.getFtpConnect(ftpParamCfg);
                List<FileInfo> fileList = new ArrayList<>();
                String remoteFilePath = "";
                FTPFileFilter fileFilter = new ScanFTPFileFilter();
                FtpUtil.listFtpFiles(fileList, ftpClient, remoteFilePath, fileFilter);
                //吐到队列
                SleepUtil.sleepMilliSecond(1000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                FtpUtil.closeFtpConnect(ftpClient);
            }
        }
    }

    public void stop() {
        isStop = true;
    }
}
