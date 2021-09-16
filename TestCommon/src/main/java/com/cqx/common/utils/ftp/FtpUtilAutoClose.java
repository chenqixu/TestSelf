package com.cqx.common.utils.ftp;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * FTP工具，重新改版
 *
 * @author chenqixu
 */
public class FtpUtilAutoClose implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtilAutoClose.class);
    private boolean isLongConnect = false; // 是否长连接
    private FtpParamCfg ftpParamCfg;
    private FTPClient longFtpClient; // 用于长连接
    private FtpMode ftpMode = FtpMode.PassiveMode; // FTP模式，默认被动模式

    public FtpUtilAutoClose(FtpParamCfg ftpParamCfg) {
        this.ftpParamCfg = ftpParamCfg;
    }

    /**
     * 创建FTP连接
     *
     * @return
     */
    private FTPClient getFtpConnect() {
        FTPClient ftpClient = null;
        // 配置验证
        if (ftpParamCfg == null) throw new RuntimeException("FTP配置为空，请正确初始化！");

        // 长连接
        if (isLongConnect && longFtpClient != null) {
            logger.info("获取长连接：{}", longFtpClient);
            return longFtpClient;
        }

        // 连接初始化
        try {
            ftpClient = new FTPClient();
            // 设置连接超时时间
            ftpClient.setConnectTimeout(30000);
            ftpClient.connect(ftpParamCfg.getHost(), ftpParamCfg.getPort());
            ftpClient.login(ftpParamCfg.getUser(), ftpParamCfg.getPassword());
            // 验证是否登陆成功
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new FTPConnectionClosedException("FtpUtilException：FTP连接登录失败,应答码为:" + replyCode);
            }
            // 设置以二进制方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // FTP传输模式
            if (ftpMode.equals(FtpMode.ActiveMode)) {
                // 主动模式
                ftpClient.enterLocalActiveMode();
            } else {
                // 被动模式
                ftpClient.enterLocalPassiveMode();
            }

            // 长连接
            if (isLongConnect) {
                longFtpClient = ftpClient;
                logger.info("设置长连接：{}", longFtpClient);
            }
        } catch (Exception e) {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e1) {
                    logger.error("FtpUtilException：关闭FTP连接异常，" + e1.getMessage(), e1);
                }
            }
            logger.error("FtpUtilException：%%%%失败连接FTP服务器:" + ftpParamCfg.getHost()
                    + ",端口号:" + ftpParamCfg.getPort() + ",FTP用户名:"
                    + ftpParamCfg.getUser() + ",FTP密码:"
                    + ftpParamCfg.getPassword(), e);
            return null;
        }
        return ftpClient;
    }

    /**
     * 关闭FTP连接
     *
     * @param ftpClient
     */
    private void closeFtpConnect(FTPClient ftpClient) {
        if (ftpClient != null) {
            // 长连接
            if (isLongConnect) {
                // todo 长连接的策略处理
                logger.info("长连接，不用关闭连接，由用到的程序自行处理");
                return;
            }
            String RemoteAddress = "?";
            int port = -1;
            boolean logoutRet = false;
            try {
                // 获取ip
                RemoteAddress = ftpClient.getRemoteAddress().getHostAddress();
                // 获取端口
                port = ftpClient.getRemotePort();
                // 登出
                logoutRet = ftpClient.logout();
                // 获取返回
                getFtpReplyCode(ftpClient);
            } catch (Exception e) {
                logger.error("FtpUtilException：%%%%FTPClient在logout时出错!!!" + e.getMessage(), e);
            } finally {
                if (ftpClient.isConnected()) {
                    logger.info("ftpClient.isConnected：{}", true);
                    try {
                        // 断开连接
                        ftpClient.disconnect();
                        // 获取返回
                        getFtpReplyCode(ftpClient);
                    } catch (Exception e2) {
                        logger.error("FtpUtilException：%%%%FTPClient在disconnect时出错!!!" + e2.getMessage(), e2);
                    }
                }
                // 防止意外，这里强制为null
                ftpClient = null;
            }
            logger.info("成功关闭ftpClient，{}:{}，logoutRet：{}", RemoteAddress, port, logoutRet);
        }
    }

    /**
     * 列出FTP服务器上指定目录下的指定文件名列表
     *
     * @param fileList
     * @param remoteFilePath 远端文件路径
     * @param fileFilter
     * @return
     */
    public List<FileInfo> listFtpFiles(List<FileInfo> fileList, String remoteFilePath, FTPFileFilter fileFilter) {
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        try {
            //描述指定目录,取出指定后缀名的文件,放入数组中
            FTPFile[] fileArrays = ftpClient.listFiles(remoteFilePath, fileFilter);
            String hostAddress = ftpClient.getRemoteAddress().getHostAddress();
            //把数组转为List
            for (FTPFile ftpFile : fileArrays) {
                //取出文件名
                String fileName = ftpFile.getName();
                //转换成List
                if (ftpFile.isFile() || ftpFile.isSymbolicLink()) {
                    FileInfo fileInfo = new FileInfo();
                    //文件名
                    fileInfo.setFile_name(fileName);
                    //文件大小
                    fileInfo.setFile_size(ftpFile.getSize());
                    //文件时间
                    fileInfo.setSource_file_createTime(ftpFile.getTimestamp().getTimeInMillis());
                    //文件路径
//                        fileInfo.setCheck_file_path(remoteFilePath);
                    fileInfo.setSource_path(remoteFilePath);
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
                    if (CollectorConfInfo.ifRoundSubdirectory) {
                        listFtpFiles(fileList, subPath, fileFilter);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("FtpUtilException：%%%%列出FTP服务器上目录:" + remoteFilePath + "的文件时出错!!" + e.getMessage(), e);
        } finally {
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        return fileList;
    }

    /**
     * FTP获取文件流
     *
     * @param file_path
     * @return
     */
    public InputStream ftpFileDownload(String file_path, String file_name) {
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        InputStream inputStream = null;
        try {
            inputStream = ftpClient.retrieveFileStream(file_path + file_name);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        return inputStream;
    }

    /**
     * 文件下载，注意：需要长连接！！！
     *
     * @param ftp_file_path
     * @param file_name
     * @param save_file_path
     * @throws IOException
     */
    public void ftpFileDownload(String ftp_file_path, String file_name, String save_file_path) throws IOException {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        File file = new File(save_file_path + file_name);
        //本地文件如果存在就删除
        if (file.exists()) {
            boolean delete_result = file.delete();
            logger.info("delete：{}，result：{}", file, delete_result);
        }
        try (OutputStream outputStream = new FileOutputStream(file);
             InputStream inputStream = ftpFileDownload(ftp_file_path, file_name)) {
            //设置FTP下载缓冲区
            byte[] buffer = new byte[1048576];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                //本地流写文件
                outputStream.write(buffer, 0, c);
                outputStream.flush();
            }
        } finally {
            // 结束数据连接
            ftpClient.getReply();
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        timeCostUtil.stop();
        logger.info("download，file：{}，size：{}，cost：{}", ftp_file_path + file_name, file.length(), timeCostUtil.getCost());
    }

    /**
     * 转移文件
     *
     * @param newPath
     * @param oldFile
     * @param fileName
     * @return
     */
    public boolean moveFile(String newPath, String oldFile, String fileName) {
        boolean flag = false;
        String newFilePath = newPath + fileName;//新文件路径+文件名
        String oldFilePath = oldFile + fileName;//文件路径+文件名
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        try {
            if (!ftpClient.changeWorkingDirectory(newPath)) {
                // 文件转移路径不存在 创文件夹
                String[] pathArry = newPath.split("/");
                StringBuilder filePath = new StringBuilder("/");
                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }
                    filePath.append(path).append("/");
                    if (!ftpClient.changeWorkingDirectory(filePath.toString())) {
                        // 建立目录
                        ftpClient.makeDirectory(filePath.toString());//创建目录
                    }
                }
            }
            flag = ftpClient.rename(oldFilePath, newFilePath);
            logger.info(">>>>" + oldFilePath + "文件成功转移到目录：" + newFilePath + "<<<");
        } catch (IOException e) {
            logger.error(">>>" + oldFilePath + "转移文件失败");
            logger.error(e.getMessage(), e);
        } finally {
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        return flag;
    }


    /**
     * 文件是否存在
     *
     * @param file_path
     * @return
     */
    public boolean isFileExit(String file_path) {
        boolean flag = false;
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        try {
            //精确匹配单个文件并获取属性等信息
            logger.info(">>>>file<<<" + file_path);
            FTPFile[] ftpFilesArr = ftpClient.listFiles(file_path);
            logger.info(">>>>length<<<" + ftpFilesArr.length);
            if (ftpFilesArr.length == 1) {
                flag = true;
            } else {
                //实际文件不存在
                logger.info(">>>>文件不存在<<<" + file_path);
            }
        } catch (Exception e) {
            logger.error(">>>获取文件异常" + file_path + "<<<" + e);
        } finally {
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        return flag;
    }

    /**
     * 文件删除
     *
     * @param file_path
     * @param filename
     * @return
     */
    public boolean deleteFile(String file_path, String filename) {
        boolean flag = false;
        String fullName = file_path + filename;
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        if (isFileExit(fullName)) {
            try {
                ftpClient.deleteFile(fullName);
                flag = true;
                logger.info(">>>>源文件删除成功<<<" + fullName);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                // 关闭连接
                closeFtpConnect(ftpClient);
            }
        } else {
            logger.warn(">>>源文件没有找到<<<" + fullName);
        }
        return flag;
    }

    /**
     * 文件上传
     *
     * @param localFilePath
     * @param remoteFilePath
     * @param localFileName
     * @param remoteFileName
     * @return
     * @throws IOException
     */
    public boolean upload(String localFilePath, String remoteFilePath, String localFileName, String remoteFileName) throws IOException {
        boolean storeRet;
        FTPClient ftpClient = getFtpConnect();
        assert ftpClient != null;
        try {
            storeRet = ftpClient.storeFile(remoteFilePath + remoteFileName
                    , new FileInputStream(localFilePath + localFileName));
            logger.info("localFilePath：{}，remoteFilePath：{}，localFileName：{}，remoteFileName：{}，storeRet：{}"
                    , localFilePath, remoteFilePath, localFileName, remoteFileName, storeRet);
            // 获取返回
            getFtpReplyCode(ftpClient);
        } finally {
//            SleepUtil.sleepSecond(10);
            // 关闭连接
            closeFtpConnect(ftpClient);
        }
        return storeRet;
    }

    /**
     * 获取返回码
     *
     * @param ftpClient
     * @return
     */
    public int getReplyCode(FTPClient ftpClient) {
        if (ftpClient != null) {
            return ftpClient.getReplyCode();
        }
        return -1;
    }

    /**
     * 获取返回码的说明
     *
     * @param ftpClient
     * @return
     */
    public String getReplyString(FTPClient ftpClient) {
        if (ftpClient != null) {
            return ftpClient.getReplyString();
        }
        return null;
    }

    /**
     * 获取返回码的说明
     *
     * @param ftpClient
     * @return
     */
    public void getFtpReplyCode(FTPClient ftpClient) {
        FtpReplyCode ftpReplyCode = FtpReplyCode.getFtpReplyCodeByCode(getReplyCode(ftpClient));
        logger.info("获取返回：{}", ftpReplyCode);
    }

    /**
     * 结束数据连接
     *
     * @param ftpClient
     * @return
     * @throws IOException
     */
    public int getReply(FTPClient ftpClient) throws IOException {
        if (ftpClient != null) {
            return ftpClient.getReply();
        }
        return -1;
    }

    @Override
    public void close() throws IOException {
        // 尝试关闭长连接
        isLongConnect = false;
        closeFtpConnect(longFtpClient);
    }

    /**
     * 设置长连接
     *
     * @param longConnect
     */
    public void setLongConnect(boolean longConnect) {
        isLongConnect = longConnect;
    }

    public void setFtpMode(FtpMode ftpMode) {
        this.ftpMode = ftpMode;
    }
}
