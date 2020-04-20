package com.cqx.distributed.util;

import com.cqx.distributed.bean.FileInfo;
import com.cqx.distributed.bean.FtpParamCfg;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * FTP工具
 *
 * @author chenqixu
 */
public class FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * @return FTPClient
     * @description:公用方法,创建FTP连接
     * @author:xixg
     * @date:2014-01-18
     */
    public static FTPClient getFtpConnect(FtpParamCfg ftpParamCfg) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            //设置连接超时时间
            ftpClient.setConnectTimeout(30000);
            ftpClient.connect(ftpParamCfg.getHost(), ftpParamCfg.getPort());
            ftpClient.login(ftpParamCfg.getUser(), ftpParamCfg.getPassword());
            //验证是否登陆成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new FTPConnectionClosedException("FtpUtilException：FTP连接登录失败,应答码为:" + replyCode);
            }
            //设置以二进制方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //被动模式
            ftpClient.enterLocalPassiveMode();
        } catch (Exception e) {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e1) {
                    logger.error("FtpUtilException：关闭FTP连接异常，" + e1.getMessage(), e1);
                }
            }
            ftpClient = null;
            logger.error("FtpUtilException：%%%%失败连接FTP服务器:" + ftpParamCfg.getHost()
                    + ",端口号:" + ftpParamCfg.getPort() + ",FTP用户名:"
                    + ftpParamCfg.getUser() + ",FTP密码:"
                    + ftpParamCfg.getPassword(), e);
        }
        return ftpClient;
    }

    /**
     * @return void
     * @description:公用方法,关闭FTP连接
     * @author:xixg
     * @date:2014-01-18
     */
    public static void closeFtpConnect(FTPClient ftpClient) {
        if (ftpClient != null) {
            String RemoteAddress = ftpClient.getRemoteAddress().getHostAddress();
            int port = ftpClient.getRemotePort();
            try {
                ftpClient.logout();
            } catch (Exception e) {
                logger.error("FtpUtilException：%%%%FTPClient在logout时出错!!!" + e.getMessage(), e);
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (Exception e2) {
                        logger.error("FtpUtilException：%%%%FTPClient在disconnect时出错!!!" + e2.getMessage(), e2);
                    }
                }
            }
            logger.info("成功关闭ftpClient" + RemoteAddress + ":" + port);
        }
    }

    /**
     * @param remoteFilePath 远端文件路径
     * @return List<FileInfo>
     * @description: 列出FTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     */
    public static List<FileInfo> listFtpFiles(List<FileInfo> fileList, FTPClient ftpClient,
                                              String remoteFilePath, FTPFileFilter fileFilter) {
        try {
            //描述指定目录,取出指定后缀名的文件,放入数组中
            FTPFile[] fileArrays = ftpClient.listFiles(remoteFilePath, fileFilter);
            String hostAddress = ftpClient.getRemoteAddress().getHostAddress();
            //把数组转为List
            for (FTPFile ftpFile : fileArrays) {
                //取出文件名
                String fileName = ftpFile.getName().toString();
                //转换成List
                if (ftpFile.isFile() || ftpFile.isSymbolicLink()) {
                    FileInfo fileInfo = new FileInfo();
                    //文件名
                    fileInfo.setFile_name(fileName);
//                    //文件大小
//                    fileInfo.setFile_size(ftpFile.getSize());
                    //文件时间
                    fileInfo.setSource_file_createTime(ftpFile.getTimestamp().getTimeInMillis());
                    //文件路径
                    fileInfo.setCheck_file_path(remoteFilePath);
                    //源主机
                    fileInfo.setSource_machine(hostAddress);
//                    //文件全路径
//                    fileName = new String((remoteFilePath + fileName).getBytes(), ftpClient.getControlEncoding());
                    //把文件名放入List中
                    fileList.add(fileInfo);
                } else if (ftpFile.isDirectory()) {
                    String subPath = remoteFilePath + fileName;
                    if (ftpClient.getSystemType().toUpperCase().startsWith("UNIX")) {
                        subPath = subPath + "/";
                    } else {
                        subPath = subPath + File.separator;
                    }
                    //递归子目录
                    listFtpFiles(fileList, ftpClient, subPath, fileFilter);
                }
            }
        } catch (Exception e) {
            logger.error("FtpUtilException：%%%%列出FTP服务器上目录:" + remoteFilePath + "的文件时出错!!" + e.getMessage(), e);
        }
        return fileList;
    }
}
