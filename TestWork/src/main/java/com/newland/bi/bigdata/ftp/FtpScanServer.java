package com.newland.bi.bigdata.ftp;

import com.newland.bd.model.cfg.FtpCfg;
import com.newland.bd.utils.commons.ftp.FileTransferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FtpScanServer
 * 开多并发，每隔几秒扫描一次
 *
 * @author chenqixu
 */
public class FtpScanServer {

    private static final String scanPath = "/fbscj/source1";
    private static final String FileNameExpress = "LTE_UU_*_20[0-9]{6}(00|01|02|03|04|05|06|07|08|11|12|13|14|15|16|17|19|21|22|23)[0-9]{4}*.txt.chk";
    private static Logger logger = LoggerFactory.getLogger(FtpScanServer.class);
    private int concurrence = 0;
    private long interval = 30 * 1000l;
    private ScanStatus scanStatus = ScanStatus.STOP;
    private FtpCfg ftpCfg;
    private FileTransferUtil ftpUtil;

    private FtpScanServer() {
        ftpCfg = new FtpCfg();
        ftpCfg.setHost("10.1.8.78");
        ftpCfg.setUser("edc_base");
        ftpCfg.setPassword("edc_base");
        ftpCfg.setPort(21);
    }

    public static FtpScanServer builder() {
        return new FtpScanServer();
    }

    public static void main(String[] args) throws Exception {
        FtpScanServer.builder().setConcurrence(3).startServer();
    }

    private void checkStatus() throws Exception {
        if (!(this.concurrence > 0 && interval >= 30 * 1000l && scanStatus == ScanStatus.STOP)) {
            throw new Exception("FtpScanServer status is not ok.");
        }
    }

    public void startServer() throws Exception {
        //校验
        checkStatus();
        //启动线程
        for (int i = 0; i < concurrence; i++) {
            logger.info("start {} FtpScanServer", i);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        //扫描
                        scan();
                        //休眠
                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }).start();
        }
        //改变状态
        scanStatus = ScanStatus.START;
        logger.info("change status：{}", scanStatus);
    }

    private void scan() {
        try {
            ftpUtil = new FileTransferUtil(ftpCfg);
            ftpUtil.getFilesInfo(scanPath, FileNameExpress, false);
        } catch (Exception e) {
            logger.error("###scanfail！" + e.getMessage(), e);
        } finally {
            if (ftpUtil != null) {
                ftpUtil.disconnect();
            }
        }
    }

    public int getConcurrence() {
        return concurrence;
    }

    public FtpScanServer setConcurrence(int concurrence) {
        this.concurrence = concurrence;
        return this;
    }

    public long getInterval() {
        return interval;
    }

    public FtpScanServer setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    enum ScanStatus {
        STOP, START;
    }
}
