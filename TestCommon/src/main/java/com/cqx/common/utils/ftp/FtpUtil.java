package com.cqx.common.utils.ftp;

import com.cqx.common.utils.system.TimeCostUtil;
import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Ftp
 *
 * @author chenqixu
 */
public class FtpUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * 创建FTP连接
     *
     * @param ftpParamCfg
     * @return
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
     * 列出FTP服务器上指定目录下的指定文件名列表
     *
     * @param fileList
     * @param ftpClient
     * @param remoteFilePath 远端文件路径
     * @param fileFilter
     * @return
     */
    public static List<FileInfo> listFtpFiles(List<FileInfo> fileList, FTPClient ftpClient, String remoteFilePath, FTPFileFilter fileFilter) {
        if (ftpClient != null) {
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
                            listFtpFiles(fileList, ftpClient, subPath, fileFilter);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("FtpUtilException：%%%%列出FTP服务器上目录:" + remoteFilePath + "的文件时出错!!" + e.getMessage(), e);
            }
        }
        return fileList;
    }


    /**
     * FTP获取文件流
     *
     * @param ftpClient
     * @param file_path
     * @return
     */
    public static InputStream ftpFileDownload(FTPClient ftpClient, String file_path, String file_name) {
        InputStream inputStream = null;
        if (ftpClient != null) {
            try {
                inputStream = ftpClient.retrieveFileStream(file_path + file_name);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return inputStream;
    }

    /**
     * 文件下载
     *
     * @param ftpClient
     * @param ftp_file_path
     * @param file_name
     * @param save_file_path
     * @throws IOException
     */
    public static void ftpFileDownload(FTPClient ftpClient, String ftp_file_path, String file_name, String save_file_path) throws IOException {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        File file = new File(save_file_path + file_name);
        //本地文件如果存在就删除
        if (file.exists()) {
            boolean delete_result = file.delete();
            logger.info("delete：{}，result：{}", file, delete_result);
        }
        try (OutputStream outputStream = new FileOutputStream(file); InputStream inputStream = ftpFileDownload(ftpClient, ftp_file_path, file_name)) {
            //设置FTP下载缓冲区
            byte[] buffer = new byte[1048576];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
                //本地流写文件
                outputStream.write(buffer, 0, c);
                outputStream.flush();
            }
        } finally {
            //结束数据连接
            ftpClient.getReply();
        }
        timeCostUtil.stop();
        logger.info("download，file：{}，size：{}，cost：{}", ftp_file_path + file_name, file.length(), timeCostUtil.getCost());
    }

    /**
     * 转移文件
     *
     * @param ftpClient
     * @param newPath
     * @param oldFile
     * @param fileName
     * @return
     */
    public static boolean moveFile(FTPClient ftpClient, String newPath, String oldFile, String fileName) {
        boolean flag = false;
        String newFilePath = newPath + fileName;//新文件路径+文件名
        String oldFilePath = oldFile + fileName;//文件路径+文件名
        if (ftpClient != null) {
            try {
//                logger.info("aaaanew "+newPath);
//                logger.info("aaaaold "+oldFilePath);
//                logger.info("aaa "+(!ftpClient.changeWorkingDirectory(newPath)));
                if (!ftpClient.changeWorkingDirectory(newPath)) {
//            文件转移路径不存在 创文件夹
                    String pathArry[] = newPath.split("/");
                    StringBuffer filePath = new StringBuffer("/");
                    for (String path : pathArry) {
                        if (path.equals("")) {
                            continue;
                        }
                        filePath.append(path + "/");
                        if (!ftpClient.changeWorkingDirectory(filePath.toString())) {
                            // 建立目录
                            ftpClient.makeDirectory(filePath.toString());//创建目录
                        }
                    }
                }
//                ftpClient.rename(oldFile, newFilePath); //移动文件到新目录
//                ftpClient.rename(oldFilePath, newFilePath);
//                flag = true;
                flag = ftpClient.rename(oldFilePath, newFilePath);
//                logger.info(">>>>" + flag + "<<<<");
                logger.info(">>>>" + oldFilePath + "文件成功转移到目录：" + newFilePath + "<<<");
            } catch (IOException e) {
                logger.error(">>>" + oldFilePath + "转移文件失败");
//            e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
        return flag;
    }


    /**
     * 文件是否存在
     *
     * @param
     * @param file_path
     * @return
     */
    public static boolean isFileExit(FTPClient ftpClient, String file_path) {
        boolean flag = false;
//        String sourceFile="";
        if (ftpClient != null) {
            try {
                //精确匹配单个文件并获取属性等信息
//                logger.info(">>>>file<<<" + file_path);
                FTPFile[] ftpFilesArr = ftpClient.listFiles(file_path);
//                logger.info(">>>>length<<<" + ftpFilesArr.length);
                if (ftpFilesArr.length == 1) {
                    flag = true;
                } else {
                    //实际文件不存在
                    logger.info(">>>>文件不存在<<<" + file_path);
                }
            } catch (Exception e) {
                logger.error(">>>获取文件异常" + file_path + "<<<" + e);
            }
        }
        return flag;
    }

    /**
     * 文件删除
     *
     * @param ftpClient
     * @param file_path
     * @param filename
     * @return
     */
    public static boolean deleteFile(FTPClient ftpClient, String file_path, String filename) {
        boolean flag = false;
        String fullName = file_path + filename;
        if (ftpClient != null) {
            if (isFileExit(ftpClient, fullName)) {
                try {
                    ftpClient.deleteFile(fullName);
                    flag = true;
                    logger.info(">>>>源文件删除成功<<<" + fullName);
                } catch (IOException e) {
//                e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.warn(">>>源文件没有找到<<<" + fullName);
            }
        }
        return flag;
    }


    public static boolean upload(FTPClient ftpClient, String localFilePath, String remoteFilePath, String fileName) throws IOException {
        boolean storeRet = false;
        if (ftpClient != null) {
            storeRet = ftpClient.storeFile(remoteFilePath + fileName, new FileInputStream(localFilePath + fileName));
            logger.info("localFilePath：{}，remoteFilePath：{}，fileName：{}，storeRet：{}"
                    , localFilePath, remoteFilePath, fileName, storeRet);
        }
        return storeRet;
    }

    /**
     * 获取返回码
     *
     * @param ftpClient
     * @return
     */
    public static int getReplyCode(FTPClient ftpClient) {
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
    public static String getReplyString(FTPClient ftpClient) {
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
    public static FtpReplyCode getFtpReplyCode(FTPClient ftpClient) {
        return FtpReplyCode.getFtpReplyCodeByCode(getReplyCode(ftpClient));
    }

    /**
     * 结束数据连接
     *
     * @param ftpClient
     * @return
     * @throws IOException
     */
    public static int getReply(FTPClient ftpClient) throws IOException {
        if (ftpClient != null) {
            return ftpClient.getReply();
        }
        return -1;
    }
}
