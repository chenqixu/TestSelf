package com.cqx.common.utils.sftp;

import com.cqx.common.utils.ftp.FileInfo;
import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.system.TimeCostUtil;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

public class SftpUtil {

    //sftp channel
    public static final String SFTP_CHANNEL = "sftp";
    //日志记录器
    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);
    //数据源文件是否使用正则表达式匹配,只用于只采集数据文件的情况
    public static boolean ifUseRegex = false;
    //数据源文件正则表达式,只用于只采集数据文件的情况
    public static String dataSourceFileRegex;

    /**
     * @return SftpConnection sftp连接实体类
     * @description:公用方法,创建SFTP连接
     * @author:xixg
     * @date:2014-01-18
     */
    public static SftpConnection getSftpConnection(FtpParamCfg ftpParamCfg) {
        SftpConnection sftpConnection = new SftpConnection();
        try {
            JSch jsch = new JSch();
            jsch.getSession(ftpParamCfg.getUser(), ftpParamCfg.getHost(), ftpParamCfg.getPort());
            Session sshSession = jsch.getSession(ftpParamCfg.getUser(), ftpParamCfg.getHost(), ftpParamCfg.getPort());
            sshSession.setPassword(ftpParamCfg.getPassword());
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel(SFTP_CHANNEL);
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            sftpConnection.setChannelSftp(channelSftp);
            sftpConnection.setSshSession(sshSession);
        } catch (Exception e) {
            logger.error("%%%%%失败连接SFTP服务器:" + ftpParamCfg.getHost() + ",端口号:" + ftpParamCfg.getPort() + ",SFTP用户名:"
                    + ftpParamCfg.getUser() + ",SFTP密码:" + ftpParamCfg.getPassword(), e);
            if (sftpConnection.getChannelSftp() != null && sftpConnection.getChannelSftp().isConnected()) {
                sftpConnection.getChannelSftp().disconnect();
            }
            if (sftpConnection.getSshSession() != null && sftpConnection.getSshSession().isConnected()) {
                sftpConnection.getSshSession().disconnect();
            }
            return null;
        }
        return sftpConnection;
    }

    /**
     * @return void
     * @description:公用方法,关闭SFTP连接
     * @author:xixg
     * @date:2014-01-18
     */
    public static void closeSftpConnection(SftpConnection sftpConnection) {
        if (sftpConnection != null) {
            try {
                ChannelSftp channelSftp = sftpConnection.getChannelSftp();
                if (channelSftp != null) {
                    channelSftp.disconnect();
                }
                Session sshSession = sftpConnection.getSshSession();
                if (sshSession != null) {
                    sshSession.disconnect();
                }
            } catch (Exception e) {
                logger.error("%%%%%关闭SFTP连接出错!!!" + e.getMessage(), e);
            }
        }
    }

    /**
     * @param remoteFilePath 远端文件路径
     * @return List<String>
     * @description: 列出FTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     */
    public static List<FileInfo> listFtpFiles(List<FileInfo> fileList, SftpConnection sftpConnection, String remoteFilePath, String currFilterSpecificFileName) {
        if (sftpConnection != null) {
            try {
                ChannelSftp channelSftp = sftpConnection.getChannelSftp();
                //改变SFTP的工作目录
                channelSftp.cd(remoteFilePath);
                //列出当前目录的所有文件,存放在Vector中
                Vector fileVector = channelSftp.ls(remoteFilePath);
                //迭代Vector
                Iterator it = fileVector.iterator();
                //循环取出Vector中的文件名
                while (it.hasNext()) {
                    //取出文件名
                    LsEntry lsEntry = (LsEntry) it.next();
                    String fileName = lsEntry.getFilename();
                    SftpATTRS sftpATTRS = lsEntry.getAttrs();
                    //过滤出符合要求的文件名
                    if (filterFileName(fileName, currFilterSpecificFileName)) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFile_name(fileName);
                        //文件时间
                        int A_TIME = sftpATTRS.getATime();
                        String _A_TIME = String.valueOf(A_TIME);
                        if (_A_TIME.length() == 10) {
                            _A_TIME = _A_TIME + "000";
                        }
                        fileInfo.setSource_file_createTime(Long.valueOf(_A_TIME));
                        //文件路径
                        fileInfo.setCheck_file_path(remoteFilePath);
                        //源主机
                        fileInfo.setSource_machine(sftpConnection.getSshSession().getHost());
                        fileList.add(fileInfo);
                    }
                }
            } catch (Exception e) {
                logger.error("%%%%%列出SFTP服务器上目录:" + remoteFilePath + "的文件时出错!!", e);
            }
        }
        return fileList;
    }

    /**
     * @param fileName        文件名
     * @param fileNameInclude 文件包含关键字
     * @return boolean 过滤后是否符合要求
     * @description: 过滤出SFTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     */
    public static boolean filterFileName(String fileName, String fileNameInclude) {
        boolean returnFlag = true;
        try {
            //是否需要正则匹配
            if (ifUseRegex) {
                //数据源文件正则表达式,只用于只采集数据文件的情况
                if (!Pattern.matches(dataSourceFileRegex, fileName))
                    return false;
            }
//            //是否排除指定文件名的文件
//            if (CollectorConfInfo.ifExcludeSpecificFileName) {
//                //排除指定文件名的文件
//                if (fileName.indexOf(CollectorConfInfo.excludeSpecificFileName) > -1) return false;
//            }
//            //是否过滤出文件名包含特定字符串
//            if (CollectorConfInfo.ifFilterSpecificFileName) {
//                //过滤出文件名包含特定字符串
//                if (fileName.indexOf(CollectorConfInfo.filterSpecificFileName) < 0) return false;
//            }
//            //如果需要过滤特定文件名
//            if (CollectorConfInfo.ifFilterSpecificTimeFileName
//                    //过滤特定文件名不为空
//                    && fileNameInclude != null && !"".equals(fileNameInclude)
//                    //不包含特定文件名的去除
//                    && fileName.indexOf(fileNameInclude) < 0) return false;
//            //源文件是否有控制文件
//            if (CollectorConfInfo.ifHasCtlSourceFile) {
//                if (CollectorConfInfo.ifDownloadCtlFile) {
//                    //如果需要先下载控制文件
//                    if (CollectorConfInfo.ifFirstDownloadCtlFile) {
//                        //滤出控制文件后缀名为CTL的文件
//                        if (fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
//                    } else {
//                        //滤出数据文件后缀名为配置文件配置的值的文件
//                        if (fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
//                    }
//                } else {
//                    //滤出控制文件后缀名为CTL的文件
//                    if (fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
//                }
//            } else {
//                //滤出数据文件后缀名为配置文件配置的值的文件
//                if (fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
//            }
        } catch (Exception e) {
            logger.error("%%%%%SFTP过滤文件名出错!!!" + e.getMessage(), e);
        }
        return returnFlag;
    }

    /**
     * 获取文件流
     *
     * @param sftpConnection
     * @param file_path
     * @return
     */
    public static InputStream ftpFileDownload(SftpConnection sftpConnection, String file_path) {
        InputStream inputStream = null;
        if (sftpConnection != null) {
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
            try {
                //改变SFTP的工作目录
//                channelSftp.cd(file_path);
                inputStream = channelSftp.get(file_path);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 上传文件
     *
     * @param sftpConnection
     * @param local_file
     * @param remote_file
     */
    public static void upload(SftpConnection sftpConnection, String local_file, String remote_file) {
        if (sftpConnection != null) {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
            try {
                channelSftp.put(local_file, remote_file, ChannelSftp.OVERWRITE);
            } catch (SftpException e) {
                e.printStackTrace();
            }
            timeCostUtil.stop();
            logger.info("local_file：{}，remote_file：{}，cost：{}", local_file, remote_file, timeCostUtil.getCost());
        }
    }

    /**
     * 转移文件
     *
     * @param sftpConnection
     * @param newPath
     * @param
     * @param fileName
     * @return
     */
    public static boolean moveFile(SftpConnection sftpConnection, String newPath, String oldPath, String fileName) {
        boolean flag = false;
        String newFilePath = newPath + fileName;//新文件路径+文件名
        String oldFilepath = oldPath + fileName;
//        logger.info("new "+newPath);
//        logger.info("old "+oldPath);
        if (sftpConnection != null) {
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
//            try {
//                if (!ftpClient.changeWorkingDirectory(newPath)) {
//            String n_path = newPath.substring(0, newPath.lastIndexOf("/"));
            try {
                if (!SftpUtil.isDirExist(sftpConnection, newPath)) {
                    //文件转移路径不存在 创文件夹
                    String pathArry[] = newPath.split("/");
                    StringBuffer filePath = new StringBuffer("/");
                    for (String path : pathArry) {
                        if (path.equals("")) {
                            continue;
                        }
                        filePath.append(path + "/");
                        if (isDirExist(sftpConnection, filePath.toString())) {
                            channelSftp.cd(filePath.toString());
                        } else {
                            // 建立目录
                            channelSftp.mkdir(filePath.toString());
                            // 进入并设置为当前目录
                            channelSftp.cd(filePath.toString());
                        }
                    }
//                    channelSftp.cd(newPath);
                }
                channelSftp.rename(oldFilepath, newFilePath);//移动文件到新目录
                flag = true;
                logger.info(">>>>" + oldFilepath + "文件成功转移到目录：" + newFilePath + "<<<");
//                inputStream.close();
//                newInput.close();
            } catch (SftpException e) {
                logger.error(">>>" + oldFilepath + "转移文件异常" + e);
            }
        }
        return flag;
    }

    /**
     * 判断目录是否存在
     *
     * @param directory 路径
     * @return
     */
    public static boolean isDirExist(SftpConnection sftpConnection, String directory) {
        boolean isDirExistFlag = false;
        if (sftpConnection != null) {
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
            try {
                SftpATTRS sftpATTRS = channelSftp.lstat(directory);
//                isDirExistFlag = false;
                isDirExistFlag = sftpATTRS.isDir();
            } catch (Exception e) {
                if (e.getMessage().toLowerCase().equals("no such file")) {
                    isDirExistFlag = false;
                }
            }
        }
        return isDirExistFlag;
    }

    /**
     * 文件是否存在
     *
     * @param
     * @param file_path
     * @return
     */
    public static boolean isFileExit(SftpConnection sftpConnection, String file_path) {
        boolean flag = false;
        String filename = file_path.substring(file_path.lastIndexOf("/") + 1);
        String path = file_path.substring(0, file_path.lastIndexOf("/"));
        if (sftpConnection != null) {
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
            try {
                if (channelSftp.ls(path) != null) {
                    //列出当前目录的所有文件,存放在Vector中
                    Vector fileVector = channelSftp.ls(path);
                    //迭代Vector
                    Iterator it = fileVector.iterator();
                    //循环取出Vector中的文件名
                    while (it.hasNext()) {
                        //取出文件名
                        LsEntry lsEntry = (LsEntry) it.next();
                        String checkfilename = lsEntry.getFilename();
//                        SftpATTRS sftpATTRS = lsEntry.getAttrs();
                        if (checkfilename.equals(filename)) {
                            flag = true;
                            break;
                        }
                    }
                }
            } catch (Exception e1) {
                logger.error(">>检测文件异常！" + file_path, e1);
            }
//            catch (SftpException se) {
//                logger.error(">>检测文件异常！", se);
//            }
        }
        return flag;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(SftpConnection sftpConnection, String file_path, String filename) {
        boolean flag = false;
//        String directory= file_path.substring(0, file_path.lastIndexOf("/"));
//        String deleteFileName= file_path.substring(file_path.lastIndexOf("/") + 1);
        String directory = file_path;
        String deleteFileName = filename;
        if (sftpConnection != null) {
            ChannelSftp channelSftp = sftpConnection.getChannelSftp();
            try {
                channelSftp.cd(directory);
                channelSftp.rm(deleteFileName);
            } catch (SftpException e) {
                logger.error(">>>文件删除异常！" + file_path, e);
            }
        }
        return flag;
    }
}
