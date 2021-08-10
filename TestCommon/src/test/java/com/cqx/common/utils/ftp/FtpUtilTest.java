package com.cqx.common.utils.ftp;

import com.cqx.common.utils.io.BufferedReaderUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FtpUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtilTest.class);
    private FtpParamCfg ftpParamCfg;

    @Before
    public void setUp() throws Exception {
        ftpParamCfg = new FtpParamCfg("10.1.8.203", 21,
                "edc_base", "fLyxp1s*", false);
    }

    @Test
    public void listFtpFiles() throws IOException {
        FTPClient ftpClient = FtpUtil.getFtpConnect(ftpParamCfg);
        List<FileInfo> fileList = new ArrayList<>();
        String remoteFilePath = "/bi/datacollect/cdr/599/A04002/";
        String saveFilePath = "d:\\tmp\\data\\gejie\\";
        CollectorConfInfo.ifUseRegex = true;
        CollectorConfInfo.dataSourceFileRegex = "A04002[0-9]{17}.AVL";
        FTPFileFilter fileFilter = new DataColectorFTPFileFilter();
        FtpUtil.listFtpFiles(fileList, ftpClient, remoteFilePath, fileFilter);
        FtpReplyCode replyCode = FtpUtil.getFtpReplyCode(ftpClient);
        String replyString = FtpUtil.getReplyString(ftpClient);
        if (fileList.size() > 0) {
            FileInfo fileInfo = fileList.get(0);
            logger.info("size：{}，simple：{}，source_path：{}，file_name：{}", fileList.size(), fileInfo,
                    fileInfo.getSource_path(), fileInfo.getFile_name());

            InputStream inputStream = FtpUtil.ftpFileDownload(ftpClient, fileInfo.getSource_path(), fileInfo.getFile_name());
            BufferedReaderUtil bufferedReaderUtil = new BufferedReaderUtil(inputStream, fileInfo.getFile_name());
            String read = bufferedReaderUtil.readLineSimple();
            logger.info("read：{}", read);

//            int down_cnt = 0;
//            for (FileInfo _fileInfo : fileList) {
//                FtpUtil.ftpFileDownload(ftpClient, _fileInfo.getSource_path(), _fileInfo.getFile_name(), saveFilePath);
//                down_cnt++;
//                if (down_cnt > 2) break;
//            }
        } else {
            logger.info("没有文件，\n【状态】{}，\n【说明】{}", replyCode, replyString);
        }
        FtpUtil.closeFtpConnect(ftpClient);
    }
}