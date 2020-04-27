package com.cqx.common.utils.sftp;

import com.cqx.common.utils.ftp.FtpParamCfg;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SftpUtilTest {

    private SftpConnection sftpConnection;

    @Before
    public void setUp() throws Exception {
        FtpParamCfg ftpParamCfg = new FtpParamCfg("192.168.230.128", 22, "hadoop", "hadoop");
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
}