package com.cqx.pool.ftp;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FileTransferUtilTest {

    private static Logger log = LoggerFactory.getLogger(FileTransferUtilTest.class);

    /**
     * ftp配置bean
     */
    private FtpCfg ftpCfg = null;

    @Before
    public void setUp() throws Exception {
        ftpCfg = new FtpCfg();
        ftpCfg.setHost("10.1.8.81");
        ftpCfg.setUser("edc_base");
        ftpCfg.setPassword("edc_base");
        ftpCfg.setPort(21);
    }

    @Test
    public void testFtpUtil() throws Exception {
        try {
            log.info("testFtpUtil");
            FileTransferUtil ftpUtil = new FileTransferUtil(ftpCfg);
            log.info("get ftp conn：{}", ftpUtil);
            List<FileTransferUtil.FTPFileInfo> ftplist = ftpUtil.getFilesInfo("/fbscj/sourcebak/", "14*.txt.gz.ok", false);
            log.info("ftplist.size：{}", ftplist.size());
            ftpUtil.disconnect();
            log.info("disconnect");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}