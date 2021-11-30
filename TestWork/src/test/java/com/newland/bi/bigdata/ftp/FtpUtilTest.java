package com.newland.bi.bigdata.ftp;

import com.enterprisedt.util.debug.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FtpUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtilTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void connectServer() throws Exception {
        String ftpServerIp = "10.1.8.203";
        int ftpServerPort = 22;
        String ftpServerUser = "edc_base";
        String ftpServerPassword = "fLyxp1s*";
        int timeout = 30000;
        FtpUtil ftpUtil = null;
        try {
            ftpUtil = new FtpUtil(ftpServerIp, ftpServerUser, ftpServerPassword, ftpServerPort, timeout);
            ftpUtil.connectServerBySFTP();
            List<String> paths = new ArrayList<>();
            paths.add("/bi/app/");
            for (String tmp : ftpUtil.getPaths(paths, new String[]{"*comm*"})) {
                logger.info("path：{}", tmp);
            }
        } finally {
            if (ftpUtil != null)
                ftpUtil.disconnect();
        }
    }
}