package com.cqx.pool.ftp;

import com.cqx.pool.ftp.FileTransferUtil.FTPFileInfo;
import com.cqx.pool.ftp.FileTransferUtil.FtpUtilRespInfo;
import com.enterprisedt.net.ftp.*;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.enterprisedt.net.ftp.pro.ProFTPClientInterface;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.ftp.ssh.SSHFTPException;
import com.enterprisedt.net.ftp.ssh.SSHFTPInputStream;
import com.enterprisedt.net.ftp.ssh.SSHFTPOutputStream;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenqixu
 * @description Ftp客户端
 * @date 2018/11/28 17:14
 */
public class NlFtpClient {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(NlFtpClient.class);
    public static final int BUFFER_SIZE = 5 * 1024 * 1024; // 设置文件缓冲区为5M。加快下载速度
    private FtpCfg ftpCfg;
    private ProFTPClientInterface client = null;
    private final String clientUUid; // 用来做对象是否相同比较.uuid来保障两个对象的hashCode完全相同.
    private static AtomicInteger SEQ = new AtomicInteger();

    /**
     * 获取一个FTP连接
     *
     * @param ftpCfg
     * @throws Exception
     */
    public NlFtpClient(FtpCfg ftpCfg, ProFTPClientInterface client) throws Exception {
        this.ftpCfg = ftpCfg;
        this.client = client;

        clientUUid = "UUID:" + SEQ.incrementAndGet();// 构造一个UUID;
        log.debug("创建ftp连接对象{},key {}", this.clientUUid, ftpCfg);
    }

    /**
     * 关闭FTP客户端连接
     *
     * @return 关闭成功标识
     * @throws IOException
     * @throws Exception
     */
    public void disconnect() {
        if (client == null) {
            log.warn("对象已经关闭过了{}", this.clientUUid);
            return;
        }
        log.debug("关闭连接对象" + clientUUid + "，当前key:" + ftpCfg);
        try {
            this.client.quit();
        } catch (ControlChannelIOException | FTPException e) {
            log.info("释放连接对象" + clientUUid + "发生异常，当前key:" + ftpCfg, e.getMessage());
            try {
                client.quitImmediately();
                log.info("ftp 连接关闭成功" + clientUUid);
            } catch (Exception e1) {
                log.info("ftp 关闭连接出错" + clientUUid, e.getMessage());
            }
        } catch (Exception e) {
            log.warn("释放连接对象" + clientUUid + "发生异常，当前key:" + ftpCfg, e);
        }
        client = null;
    }

    /**
     * 上传文件路径
     *
     * @param localPath      本地文件路径
     * @param remotePath     远程文件路径
     * @param localFileName  本地文件名
     * @param remoteFileName 远程文件名
     * @throws Exception
     */
    public void fileUpload(String localPath, String remotePath, String localFileName, String remoteFileName) throws Exception {
        localPath = covertPath(localPath);
        remotePath = covertPath(remotePath);
        log.info("{}开始上传文件:" + localPath + localFileName, clientUUid);
        // 上传文件
        client.put(localPath + localFileName, remotePath + remoteFileName);

        log.info("{}文件上传成功:" + localFileName, clientUUid);

    }

    /**
     * 文件上传
     *
     * @param localPath      本地文件路径
     * @param remotePath     远程文件路径
     * @param localFileName  本地文件名
     * @param remoteFileName 远程文件名
     * @throws Exception
     */
    public void fileDownload(String localPath, String remotePath, String localFileName, String remoteFileName) throws Exception {
        localPath = covertPath(localPath);
        remotePath = covertPath(remotePath);
        log.info("开始下载文件：" + remotePath + remoteFileName);

        // 下载文件
        client.get(localPath + localFileName, remotePath + remoteFileName);
        log.info("下载文件成功:" + localFileName);

    }

    /**
     * 上出文件
     *
     * @param localPath  本地文件路径
     * @param remotePath 远程文件路径
     * @throws Exception
     */
    public void fileUpload(String localPath, String remotePath) throws Exception {
        log.info("开始上传文件:" + localPath + "往" + remotePath);
        // 上传文件
        client.put(localPath, remotePath);
        log.info("文件上传成功:" + localPath);
    }

    /**
     * 多文件上传吧
     *
     * @param localPath  本地文件路径
     * @param remotePath 远程文件路径
     * @param fileName   文件名规则 如*.gz
     * @return
     */
    public FtpUtilRespInfo fileUploadByReg(String localPath, String remotePath, String fileName) {
        FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
        localPath = covertPath(localPath);
        remotePath = covertPath(remotePath);
        try {
            log.info("开始批量上传文件：" + fileName);
            File dir = new File(localPath);
            File[] files = dir.listFiles();
            for (File file : files) {
                if (matchFile(file.getName(), fileName)) {
                    info.setTotalCount();
                    try {
                        client.put(file.getAbsolutePath(), remotePath + file.getName());
                        info.setSuccessCount();
                        log.info("成功上传文件:" + file.getAbsolutePath());
                    } catch (Exception e) {
                        info.setFailCount();
                        log.error("上传文件异常:" + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.error("批量上传文件异常", e);

        }
        return info;

    }

    /**
     * 针对单个文件下载，下在过程中会生成临时文件
     *
     * @param localFilePath
     * @param remoteFilePath
     * @throws Exception
     */
    public boolean fileDownloadByTransaction(String localFilePath, String remoteFilePath) throws Exception {
        // FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
        // 下载的中间态
        String tempFilePath = localFilePath + ".ftemp";
        // log.info("开始下载文件:" + remoteFilePath);
        try {
            client.get(tempFilePath, remoteFilePath);
        } catch (FTPException e) {
            // log.error("下载文件异常:" + host + "---" + remoteFilePath, e);
            if (e.getReplyCode() == 2 || e.getMessage().indexOf("No such file") > -1 || e.getMessage().indexOf("Failed to open file") > -1) {
                log.warn("无法下载文件:" + ftpCfg.getHost() + "---" + remoteFilePath + "文件已不存在");
                return false;
            } else if (e.getReplyCode() == 425) // 425 Failed to establish
            // connection. 尝试重连一次
            {
                log.error("下载文件异常:425 Failed to establish connection.尝试重连下载", e);
                Thread.sleep(2);
                try {
                    // 尝试重新下载
                    client.get(tempFilePath, remoteFilePath);
                } catch (Exception e2) {
                    throw e;
                }
            } else if (!client.exists(remoteFilePath)) { // 文件已不存在
                log.warn("下载文件异常:" + ftpCfg.getHost() + "---" + remoteFilePath + "文件已不存在");
                return false;
            } else {
                throw e;
            }
        } catch (Exception e) {
            log.error("下载文件异常:" + ftpCfg.getHost() + "---" + remoteFilePath, e);
            throw e;
        }
        File tempFile = new File(tempFilePath);
        tempFile.renameTo(new File(localFilePath));
        return true;
    }

    /**
     * 文件下载，支持目录
     *
     * @param localPath
     * @param remoteFilePath
     * @throws Exception
     */
    public void fileDownload(String localPath, String remoteFilePath) throws Exception {
        // FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
        localPath = covertPath(localPath);
        // remotePath = covertPath(remotePath);
        log.info("开始下载文件:" + remoteFilePath);
        try {
            client.get(localPath, remoteFilePath);

        } catch (Exception e) {
            log.error("下载文件异常:" + ftpCfg.getHost() + "---" + remoteFilePath, e);
            throw e;
        }

    }

    public FtpUtilRespInfo batchFileDownloadByReg(String localPath, String remotePath, String fileName) throws Exception {
        FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
        localPath = covertPath(localPath);
        remotePath = covertPath(remotePath);
        log.info("开始批量下载文件:" + fileName);
        try {
            FTPFile[] files = client.dirDetails(remotePath);
            for (FTPFile ftpFile : files) {
                if (matchFile(ftpFile.getName(), fileName)) {
                    try {
                        info.setTotalCount();
                        client.get(localPath + ftpFile.getName(), remotePath + ftpFile.getName());
                        info.setSuccessCount();
                        log.info("成功下载文件：" + localPath + ftpFile.getName());
                    } catch (Exception e) {
                        info.setFailCount();
                        log.error("下载文件失败：" + remotePath + ftpFile.getName());
                    }

                }
            }

        } catch (Exception e) {
            log.error("下载文件异常:" + ftpCfg.getHost() + "---" + remotePath + fileName, e);
            throw e;
        }

        return info;

    }

    /**
     * 根据路径，找出下面所有的文件
     *
     * @param remoteDir 远程目录
     * @param
     * @return
     * @throws Exception
     */
    public FTPFile[] listStatus(String remoteDir) throws Exception {
        remoteDir = covertPath(remoteDir);
        FTPFile[] files = null;
        long start = System.currentTimeMillis();
        try {
            files = client.dirDetails(remoteDir);// 获取目录下的所有文件和文件夹
            log.info(this.ftpCfg.getHost() + remoteDir + "获取到directoryList数量:" + files.length);
        } catch (SSHFTPException e)
            /*
             * com.enterprisedt.net.ftp.ssh.SSHFTPException: The SSH client has not yet
             * connected to the server. The requested action cannot be performed until after
             * a connection has been established.java.io.IOException: The channel is closed
             * [Unnamed Channel]java.io.IOException: Unexpected server response
             * nulljava.io.IOException: The thread was interrupted
             */ {
            log.error(this.ftpCfg.getHost() + "服务器ftp连接时出现SSHFTPException异常,此时的ftp连接状态:" + this.isConnected(), e);
            throw e;
        } catch (Exception e) {
            long useTime = System.currentTimeMillis() - start;
            log.error(this.ftpCfg.getHost() + "服务器ftp连接时出现异常,此时的client为" + client + " ftp连接状态:" + this.isConnected() + "运行耗时：" + useTime, e);
            throw e;
        }
        return files;
    }

    /**
     * 根据路径，找出下面所有的文件
     *
     * @param remoteDir 远程目录
     * @param recursion 是否递归子目录
     * @param
     * @return
     * @throws Exception
     */
    public List<FTPFileInfo> getFilesInfo(String remoteDir, String sourceFileName, boolean recursion) throws Exception {
        List<FTPFileInfo> fileInfos = new ArrayList<FTPFileInfo>();
        remoteDir = covertPath(remoteDir);
        FTPFile[] files = null;
        long totleNum = 0;
        long start = System.currentTimeMillis();

        try {
            files = client.dirDetails(remoteDir);// 获取目录下的所有文件和文件夹
            totleNum = files.length;
            log.info(this.ftpCfg.getHost() + remoteDir + "获取到所有文件数量:" + files.length);
        } catch (SSHFTPException e) {
            /*
             * com.enterprisedt.net.ftp.ssh.SSHFTPException: The SSH client has not yet
             * connected to the server. The requested action cannot be performed until after
             * a connection has been established.java.io.IOException: The channel is closed
             * [Unnamed Channel]java.io.IOException: Unexpected server response
             * nulljava.io.IOException: The thread was interrupted
             */
            log.error(this.ftpCfg.getHost() + "服务器ftp连接时出现SSHFTPException异常,此时的ftp连接状态:" + this.isConnected(), e);
            throw e;
        } catch (Exception e) {
            long useTime = System.currentTimeMillis() - start;
            log.error(this.ftpCfg.getHost() + "服务器ftp连接时出现异常,此时的client为" + client + " ftp连接状态:" + this.isConnected() + "运行耗时：" + useTime, e);
            throw e;
        }
        int nameFilterNum = 0;
        // 遍历所有文件名，找出与fileName匹配的文件
        for (FTPFile ftpFile : files) {
            if (ftpFile.isDir()) // 如果是目录，遍历拷贝
            {
                // 如果配置了递归
                if (recursion) {
                    // 对于这种名字的目录不做扫描。否则会无限循环
                    if ("..".equals(ftpFile.getName()) || ".".equals(ftpFile.getName()) || ftpFile.getName().indexOf("/") > -1) {
                        continue;
                    }

                    // // 如果是子目录做递归
                    String subDirPath = remoteDir + "/" + ftpFile.getName();
                    subDirPath = subDirPath.replace("//", "/");
                    log.info(this.ftpCfg.getHost() + remoteDir + "扫描到子目录:" + subDirPath + "递归获取子目录下的文件.");
                    // System.out.println("扫描到子目录:" + subDirPath +
                    // "递归获取子目录下的文件.");
                    fileInfos.addAll(getFilesInfo(subDirPath, sourceFileName, recursion));
                }
            } else {
                String name = ftpFile.getName();
                if ((ftpFile.isFile() || ftpFile.isLink()) && matchFile(name, sourceFileName)) {
                    // log.info("获取到文件，"+name + "文件大小:===" + ftpFile.size());
                    fileInfos.add(new FTPFileInfo(ftpFile, remoteDir, this.ftpCfg.getHost()));
                } else {
                    log.debug("{}过滤掉文件名:{}", sourceFileName, name);
                    nameFilterNum++; // 文件名过滤
                    continue;
                }
            }
        }
        log.info(this.ftpCfg.getHost() + remoteDir + "总扫描文件数量:{}. 文件名规则过滤掉的文件数量:{} ，满足扫描表达式文件数量:{}", totleNum, nameFilterNum, fileInfos.size());
        if (nameFilterNum > 10000 && totleNum - nameFilterNum < 1000) {
            log.warn(this.ftpCfg.getHost() + remoteDir + "目录下含有大量不在处理范围内的文件，可能影响扫描性能。");

        }
        return fileInfos;
    }

    /**
     * 根据路径，找出所有符合规则的文件，支持通配符* 和？
     *
     * @param remoteDir 远程目录
     * @param fileName  文件名规则 如：aa_*.txt
     * @return
     * @throws Exception
     */
    public List<String> getFileNames(String remoteDir, String fileName) throws Exception {
        log.info("扫描目标目录参数:" + remoteDir);
        ArrayList<String> fileNames = new ArrayList<String>();
        List<String> pathList = getPaths(null, remoteDir.split("/"));
        log.info("解析后目标目录:" + pathList);
        for (String path : pathList) {
            log.info("开始扫描目录" + path);
            FTPFile[] files = client.dirDetails(path);// 获取目录下的所有文件和文件夹
            log.info("扫描到文件和子目录数" + files.length);
            int num = 0;
            // 遍历所有文件名，找出与fileName匹配的文件
            for (FTPFile ftpFile : files) {
                String name = ftpFile.getName();
                if (!ftpFile.isDir() && matchFile(name, fileName)) {
                    fileNames.add(path + name);
                    num++;
                }
            }
            log.info("筛选后符合条件的文件数:" + num);

        }

        return fileNames;
    }

    public void flushDir(String srcPath) {
        FTPFile[] p;
        try {
            p = client.dirDetails(srcPath);
            if (p == null || p.length == 0) {
                return;
            }
            for (FTPFile ftpFile : p) {
                if (ftpFile.isFile())
                    client.delete(ftpFile.getPath() + "/" + ftpFile.getName());
            }
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 由于ftpApi不能创建多级目录，切判断目录是否存在只能通过tu.client.directoryNameList(
     * "/home/hadoop/output/"
     * ,true).length这种方式，当文件很多时，判断性能会很慢，因此直接从在源头开始构建目录（报错的忽略）。
     *
     * @param path
     * @throws Exception
     */
    public void createDirectory(String path) throws Exception {

        try {
            // 先判断父目录是否存在
            String parantPath = this.getParentPath(path);
            if (parantPath != null) {
                if (!this.exists(parantPath)) // 父目录不存在
                {
                    try {
                        // 创建父目录
                        this.createDirectory(parantPath);
                    } catch (FTPException e) {
                        if (550 == e.getReplyCode()) {
                            // 目录已存在。忽略
                        }
                        throw e;
                    }
                }
            }
            // 判断文件本身是否存在。如果不存在，创建
            if (this.exists(path)) {
                return;
            } else {
                this.client.mkdir(path);
            }
        } catch (FTPException e) {
            if (550 == e.getReplyCode()) {
                return; // 目录已存在。忽略
            }
            throw e;
        }
    }

    /**
     * 远程拷贝目录以及子目录下的jar文件到目标文件夹中 目标文件夹只有单层结构
     *
     * @param srcPath 源目录。如果源目录有下级目录
     * @param tgtPath 临时目录
     * @return
     * @throws @throws Exception
     */
    public List<String> remoteCopyDir(String srcPath, NlFtpClient tgtFtp, String tgtPath) throws Exception {
        String targetHost = tgtFtp.ftpCfg.getHost();
        List<String> fileList = new ArrayList<String>();
        srcPath = covertPath(srcPath);
        tgtPath = covertPath(tgtPath);
        if (srcPath.equals(tgtPath) && this.ftpCfg.getHost().equals(targetHost)) {
            throw new Exception("源目录与目标目录不允许相同。" + srcPath);
        }
        ProFTPClientInterface tgtFtpClient = tgtFtp.client;
        try {
            if (!tgtFtpClient.exists(tgtPath)) {
                // System.out.println("创建目录:" + tgtPath);
                tgtFtpClient.mkdir(tgtPath);
            }
        } catch (Exception e) {
            log.info("创建目录失败 :" + tgtPath, e);
            throw new Exception("目录不存在，且无法通过ftp客户端创建，请手工建立" + targetHost + "的目录(" + tgtPath + ")", e);

        }
        // 遍历并拷贝
        FTPFile[] p = client.dirDetails(srcPath);
        if (p == null || p.length == 0) {
            return fileList;
        }
        String fileName = null;
        String path = null;

        for (FTPFile ftpFile : p) {
            path = srcPath + ftpFile.getName();
            log.info("源：" + path);

            if (ftpFile.isDir()) // 如果是目录，遍历拷贝
            {
                // 暂时只支持一个打成大包
                // if ("..".equals(ftpFile.getName())
                // || ".".equals(ftpFile.getName())
                // || ftpFile.getName().indexOf("/") > -1) {
                // continue;
                // }
                // // 如果是子目录，递归调用。
                // fileList.addAll(remoteCopyDir(path, tgtPath));

            } else // 不是目录，只拷贝jar
            {
                fileName = ftpFile.getName();

                if (fileName != null && fileName.endsWith("jar")) {
                    log.info(this.ftpCfg.getHost() + "源：" + path);
                    log.info(targetHost + "目标：" + tgtPath + fileName);
                    // byte[] jarContent = client.downloadByteArray(path);
                    // //下载

                    InputStream in;
                    if (client instanceof ProFTPClient) {
                        in = new FTPInputStream((ProFTPClient) client, path);
                    } else {
                        in = new SSHFTPInputStream((SSHFTPClient) client, path);
                    }

                    OutputStream out;
                    if (client instanceof ProFTPClient) {
                        out = new FTPOutputStream((ProFTPClient) client, tgtPath + fileName);
                    } else {
                        out = new SSHFTPOutputStream((SSHFTPClient) client, tgtPath + fileName);
                    }

                    log.info("开始拷贝....");
                    // client.uploadStream(tgtPath + fileName,
                    // WriteMode.OVERWRITE) ;
                    long totalSize = copyStream(in, out);
                    log.info("拷贝完成，" + totalSize + "字节");

                    out.flush();
                    out.close();
                    in.close();
                    fileList.add(path);
                }
            }

        }
        return fileList;
    }

    private long copyStream(InputStream in, OutputStream out) throws Exception {
        long totalSize = 0;// 总大小
        int count;
        byte data[] = new byte[(int) BUFFER_SIZE];
        int tmpLength = 0;// 每次读的数据长度

        while ((count = in.read(data, 0, data.length)) != -1) {
            // System.out.println("正在写入");
            tmpLength = count;
            totalSize += count;
            out.write(data, 0, count);
        }
        return totalSize;
    }

    /**
     * 获取叶子节点的子目录列表。只取最后一级的。
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getSubTreePaths(String path) throws Exception {

        FTPFile[] p = client.dirDetails(path);
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        List<String> paths = new ArrayList<String>();
        for (FTPFile ftpFile : p) {
            if ("..".equals(ftpFile.getName()) || ".".equals(ftpFile.getName()) || ftpFile.getName().indexOf("/") > -1) {
                continue;
            }
            if (ftpFile.isDir()) {

                String pa = path + ftpFile.getName() + "/";
                // System.out.println(pa);
                List<String> subs = getSubTreePaths(pa);
                if (subs.isEmpty()) {
                    paths.add(pa);
                } else {
                    paths.addAll(subs);
                }
            }

        }
        return paths;
    }

    /**
     * @return
     * @throws Exception
     */
    public List<String> getPaths(List<String> allPath, String[] pathArr) throws Exception {
        if (allPath == null) {
            allPath = new ArrayList<String>();
            allPath.add("");
        }
        List<String> paths = new ArrayList<String>();
        for (String path : allPath) {

            String str = pathArr[0];
            if (str.equals("")) {
                path += "/";
                paths.add(path);
            } else if (str.contains("*") || str.contains("?")) {
                FTPFile[] p = client.dirDetails(path);
                for (FTPFile ftpFile : p) {
                    if (ftpFile.isDir() && matchFile(ftpFile.getName(), str)) {
                        String pa = path + ftpFile.getName() + "/";
                        paths.add(pa);
                    }
                }
                // paths2 = new ArrayList<String>(paths);
                // paths.clear();
                // paths = getPaths(paths2,Arrays.copyOfRange(pathArr, 1,
                // pathArr.length));
            } else {
                path += str + "/";
                paths.add(path);
            }
        }
        if (pathArr.length > 1) {
            paths = getPaths(paths, Arrays.copyOfRange(pathArr, 1, pathArr.length));
        }
        return paths;

    }

    /**
     * 重命名文件，移动文件
     *
     * @param srcPath 源路径
     * @param srcFile 源文件名称
     * @param tgtPath 目标路径
     * @param tgtName 目标路径名称
     * @return ture表示移动成功。 false表示对端已存在。移动失败。此时需要删除这个文件
     * @throws Exception
     */
    public boolean rename(String srcPath, String srcFile, String tgtPath, String tgtName) throws Exception {
        srcPath = covertPath(srcPath);
        tgtPath = covertPath(tgtPath);
        try {

            log.info("FTP移动文件" + srcPath + srcFile + "至" + tgtPath + tgtName);
            client.rename(srcPath + srcFile, tgtPath + tgtName);
        } catch (FTPException e) // ftp连接报错，重连尝试重做一次
        {
            if (e.getReplyCode() == 4 || e.getReplyCode() == 550) // 移动失败
            {
                // 判断对端文件是否存在
                if (client.exists(tgtPath + tgtName)) { // 对端文件已存在
                    return false;
                } else {
                    if (!client.exists(srcPath + srcFile)) // 判断自己是否存在
                    {
                        throw new RuntimeException("在对文件" + srcPath + srcFile + "做rename时，发现源文件已被删除");
                    } else if (!client.exists(tgtPath)) {// 判断目录是否存在
                        try {
                            client.mkdir(tgtPath); // 不存在时，创建目录

                        } catch (Exception e1) { // 不要覆盖原来的异常。屏蔽

                        }
                    }
                    log.error("FTP移动文件" + srcPath + srcFile + "至" + tgtPath + tgtName + "发生一次" + e.getReplyCode() + e.getMessage() + "进行重试重连");
                    Thread.sleep(2); // 2毫秒后重试一次
                    // 以上情况都不是，重试一次
                    client.rename(srcPath + srcFile, tgtPath + tgtName);
                    return true;
                }
            }

            throw e;
        }
        return true;
    }

    /**
     * 重命名或移动文件
     *
     * @param srcFile 源文件
     * @param tgtFile 目标文件
     * @throws Exception
     */
    public void rename(String srcFile, String tgtFile) throws Exception {

        try {
            client.rename(srcFile, tgtFile);
        } catch (FTPException e) // ftp连接报错，重连尝试重做一次
        {
            if (e.getReplyCode() == 4) {
                if (client.exists(tgtFile)) {
                    log.error("FTP移动文件" + srcFile + "至" + tgtFile + "发生4异常,目标目录已经存在该文件,目标末尾增加时间戳后重试一次");
                    client.rename(srcFile, tgtFile + System.currentTimeMillis());

                }
            } else if (e.getReplyCode() == 550) {
                Thread.sleep(10);
                // 判断对端文件是否存在
                if (!client.exists(srcFile)) { // 源端文件已不存在
                    return;
                }
            }

            log.error("FTP移动文件" + srcFile + "至" + tgtFile + "发生异常" + e.getReplyCode());

            throw e;
        }
    }

    /**
     * 删除文件或目录，当文件名为空的时候删除目录
     *
     * @param path
     * @param fileName
     */
    public void delete(String path, String fileName) throws Exception {
        path = covertPath(path);
        if (fileName != null && !fileName.equals("")) {
            if (path != null && !path.equals("") && !path.endsWith("/")) {
                path += "/";
            }
            try {
                client.delete(path + fileName);
            } catch (FTPException e) {
                if (e.getReplyCode() == 4 || e.getReplyCode() == 550) // 移动失败
                {
                    // 判断对端文件是否存在
                    if (!client.exists(path + fileName)) { // 对端文件已不存在
                        return;
                    } else {
                        log.error("FTP删除文件" + path + fileName + "发生一次" + e.getReplyCode() + e.getMessage() + "进行重试重连");
                        Thread.sleep(2); // 2毫秒后重试一次
                        // 以上情况都不是，重试一次
                        client.delete(path + fileName);
                        return;
                    }
                }
            }
        } else {
            client.rmdir(path, true);
        }
    }

    /**
     * 删除文件或目录，当文件名为空的时候删除目录
     *
     * @param filePath 文本路径
     */
    public void delete(String filePath) throws Exception {

        client.delete(filePath);

    }

    /**
     * 看path最后一个字符是否为/，若不是则补上
     *
     * @param path
     * @return
     */
    private String covertPath(String path) {
        if (path == null) {
            return "";
        }
        path = path.trim();
        char lastStr = path.charAt(path.length() - 1);
        if (lastStr != '/') {
            path = path + "/";
        }
        path = path.replace("//", "/");
        return path;
    }

    /**
     * 原生正在表达式匹配
     *
     * @param realFileName 真是文件名 如 123.txt
     * @param fileName     要匹配的文件名 如 .*.txt
     * @return
     */
    public boolean matchFileByNative(String realFileName, String fileName) {
        fileName = fileName.replace('.', '#');
        fileName = fileName.replaceAll("#", "\\\\.");
        fileName = fileName.replace('*', '#');
        fileName = fileName.replaceAll("#", ".*");
        fileName = fileName.replace('?', '#');
        fileName = fileName.replaceAll("#", ".?");
        fileName = "^" + fileName + "$";
        Pattern p = Pattern.compile(fileName);
        Matcher fMatcher = p.matcher(realFileName);
        return fMatcher.matches();
    }

    /**
     * 根据通配符来匹配对应的文件 通配符 * ？
     *
     * @param realFileName 真是文件名 如 123.txt
     * @param fileName     要匹配的文件名 如 *.txt
     * @return
     */
    public boolean matchFile(String realFileName, String fileName) {
        fileName = fileName.replace('.', '#');
        fileName = fileName.replaceAll("#", "\\\\.");
        fileName = fileName.replace('*', '#');
        fileName = fileName.replaceAll("#", ".*");
        fileName = fileName.replace('?', '#');
        fileName = fileName.replaceAll("#", ".?");
        fileName = "^" + fileName + "$";
        Pattern p = Pattern.compile(fileName);
        Matcher fMatcher = p.matcher(realFileName);
        return fMatcher.matches();
    }

    /**
     * 下载文件，返回文件输入流
     *
     * @param filePath 远程文件路径
     * @param fileName 远程文件名
     * @return 远程文件输入流
     * @throws Exception
     * @throws IOException
     */
    public BufferedInputStream downloadStream(String filePath, String fileName) throws Exception {
        String path = covertPath(filePath);
        return downloadStream(path + fileName);
    }

    /**
     * 判断文件或则文件夹是否存在
     *
     * @param filePath
     * @return
     * @throws FTPException
     * @throws IOException
     */
    public boolean exists(String filePath) throws Exception {
        boolean isExist = true;

        try {
            isExist = fileExists(filePath);
        } catch (FTPException e) {
            if ("No such file".equals(e.getMessage())) {
                return false;
            }
        } catch (ControlChannelIOException e) // 如果是连接问题，重连一次
        {
            log.warn(this.ftpCfg.getHost() + "判断文件{}exists发生异常，尝试重新连接", filePath, e);
            try {
                Thread.sleep(2);
                return fileExists(filePath);
            } catch (Exception e3) {
                throw e;
            }
        }
        return isExist;
    }

    private boolean fileExists(String filePath) throws Exception {
        return client.exists(filePath);
    }

    /**
     * 判断文件夹是否存在
     *
     * @param filePath
     * @return
     */
    public boolean existsDirectory(String filePath) throws IOException, FTPException {
        String var2 = client.pwd();
        try {
            client.chdir(filePath);
        } catch (Exception var4) {
            log.error(var4.getMessage(), var4);
            return false;
        }
        client.chdir(var2);
        return true;
    }

    /**
     * 下载文件，返回文件输入流
     *
     * @param filePath 文件的全路径
     * @return
     * @throws Exception
     */
    public BufferedInputStream downloadStream(String filePath) throws Exception {
        return downloadStream(filePath, BUFFER_SIZE);// 使用默认buffer大小
    }

    /**
     * 下载文件，返回文件输入流
     *
     * @param filePath 文件的全路径
     * @return
     * @throws Exception
     */
    public BufferedInputStream downloadStream(String filePath, int bufferSize) throws Exception {
        try {
            return getDownlaodStream(filePath);
        } catch (IOException e) {
            log.warn("下载异常", e);
            log.info("ftp采集文件IO异常，重试1次");
            return getDownlaodStream(filePath);
        }

    }

    /**
     * 用io流的方式上传文件
     *
     * @param remotePath 远程地址
     * @param fileName   远程文件名
     * @return
     * @throws Exception
     */
    public OutputStream uploadStream(String remotePath, String fileName) throws Exception {
        String path = covertPath(remotePath);
        createDirectory(remotePath);
        return getUploadStream(path + fileName);
    }

    /**
     * 用io流的方式上传文件
     *
     * @param filePath 远程地址
     * @return
     * @throws Exception
     */
    public OutputStream uploadStream(String filePath) throws Exception {
        if (this.exists(filePath)) // 判断文件是否存在，顺便做个重连
        {
            log.warn(this.ftpCfg.getHost() + "文件" + filePath + "已经存在，本次输出将会覆盖原本数据。");
        } else {
            // 自动创建文件目录
            String remotePath = getParentPath(filePath);
            if (remotePath != null) {
                try {
                    if (!this.exists(remotePath)) {

                        this.createDirectory(remotePath);
                        log.info("成功构建的ftp文件目录" + this.ftpCfg.getHost() + "文件夹" + remotePath);
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        log.info(this.ftpCfg.getHost() + "获取到文件" + filePath + "输出流");
        return getUploadStream(filePath);
    }

    /**
     * 获取上传输出流
     *
     * @param uploadFilePath 文件路径
     * @return
     */
    private BufferedOutputStream getUploadStream(String uploadFilePath) throws IOException, FTPException {
        if (client instanceof ProFTPClient) {
            return new BufferedOutputStream(new FTPOutputStream((ProFTPClient) client, uploadFilePath));
        } else {
            return new BufferedOutputStream(new SSHFTPOutputStream((SSHFTPClient) client, uploadFilePath));
        }
    }

    /**
     * 获取下载输入流
     *
     * @param uploadFilePath 文件路径
     * @return
     */
    private BufferedInputStream getDownlaodStream(String uploadFilePath) throws IOException, FTPException {
        if (client instanceof ProFTPClient) {
            return new BufferedInputStream(new FTPInputStream((ProFTPClient) client, uploadFilePath));
        } else {
            return new BufferedInputStream(new SSHFTPInputStream((SSHFTPClient) client, uploadFilePath));
        }
    }

    public String getParentPath(String filePath) {
        if (filePath.length() < 2) {
            return null;
        }
        if (filePath.endsWith("/")) {
            filePath.substring(0, filePath.length() - 1);
        }
        int index = filePath.lastIndexOf("/");
        if (index > 0 && filePath.length() > 1) {
            return filePath.substring(0, index);
        } else
            return null;
    }

    /**
     * 根据路径获得文件的大小
     *
     * @param remoteFilePath 远程文件路径
     * @param
     * @return
     * @throws Exception
     */
    public long getFilesSzie(String remoteFilePath) throws Exception {
        long size = client.size(remoteFilePath);// 获取目录下的文件大小
        return size;
    }

    public ProFTPClientInterface getClient() {
        return client;
    }

    public void setClient(ProFTPClientInterface client) {
        this.client = client;

    }

    public FtpCfg getFtpCfg() {
        return ftpCfg;
    }

    public String toString() {

        return this.clientUUid + ":" + this.ftpCfg.getHost() + ":" + this.ftpCfg.getPort();
    }

    /**
     * 查找通配符的路径，返回过滤结果到路径列表finalPathList里
     *
     * @param subPaths      源路径按/切分后的路径节点数组
     * @param pathOne       之前遍历过的节点拼接后的路径，第一次调用可为/
     * @param finalPathList 满足通配符路径的所有路径列表，结果存放在这里
     * @throws Exception
     */
    public void findExprDir(String[] subPaths, String pathOne, List<String> finalPathList) throws Exception {
        String subPathOne = pathOne;
        for (int i = 0; i < subPaths.length; i++) {
            String subPath = subPaths[i];
            if (subPath == null || subPath == "" || subPath.trim().equals("")) {
                continue;
            }
            if (subPath.contains("*") || subPath.contains("?")) {
                FTPFile[] ftpFiles = client.dirDetails(subPathOne + subPath);
                if (ftpFiles != null && ftpFiles.length > 0 && i == subPaths.length - 1) {
                    for (FTPFile file : ftpFiles) {
                        finalPathList.add(subPathOne + file.getName() + "/");
                    }
                } else {
                    for (FTPFile file : ftpFiles) {
                        findExprDir(Arrays.copyOfRange(subPaths, i + 1, subPaths.length), subPathOne + file.getName() + "/", finalPathList);
                    }
                }
                break;
            } else {
                subPathOne += subPath + "/";
                if (client.dir(subPathOne).length > 0) {
                    if (i == subPaths.length - 1) {
                        finalPathList.add(subPathOne);
                    }
                }

            }
        }
    }

    /**
     * 查找通配符的路径 方法2 查命令的方式
     *
     * @param path 源路径
     * @return
     * @throws FTPException
     * @throws IOException
     */
    public String[] findExprDir(String path) throws FTPException, IOException {
        // String exec = client.executeCommand("ls " + path + " | grep [/] | awk -F ':'
        // '{print $1}'");
        if (path.lastIndexOf("/") == path.length() - 1) {
            path = path.substring(0, path.length() - 1);
        }
        String[] pathSplit = path.split("/");
        String lastPathName = "";
        if (pathSplit != null && pathSplit.length > 0) {
            lastPathName = pathSplit[pathSplit.length - 1];
        }
        String exec = client.executeCommand("find " + path + " -maxdepth 0 -name " + lastPathName);
        if (exec != null && !"".equals(exec.trim())) {
            if (exec.contains("No such file or directory")) {
                return null;
            } else {
                return exec.split("\\n");
            }
        } else {
            return null;
        }
    }

    static int i = 0;

    // public static void main(String[] args) throws Exception {
    //
    // long start1 = System.currentTimeMillis();
    //
    // FtpCfg ftpCfg = new FtpCfg();
    // ftpCfg.setHost("10.1.8.1");
    // ftpCfg.setUser("biapp");
    // ftpCfg.setPassword("biapp");
    // ftpCfg.setPort(21);
    // final FileTransferUtil tu = new FileTransferUtil(ftpCfg);
    // System.out.println("b时间:" + (System.currentTimeMillis() - start1));
    // long start2 = System.currentTimeMillis();
    // tu.downloadStream(filePath)
    // System.out.println("c时间:" + (System.currentTimeMillis() - start2));
    // long start4 = System.currentTimeMillis();
    // OutputStream os = tu.uploadStream("/home/newhadoop/yck/test.txt");
    // os.write("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes());
    // os.flush();
    // System.out.println("run时间:" + (System.currentTimeMillis() - start4));
    // Thread.sleep(5000);
    // os.close();
    // // tu.getFilesInfo("/home/biapp/hxw/data/dest", "*", false);
    // // List<FTPFileInfo> ls = tu.getFilesInfo("/home/newhadoop/yck/test",
    // // "*", false);
    // // for (FTPFileInfo ff : ls) {
    // // System.out.println(ff.size);
    // // System.out.println("/home/newhadoop/yck/test/" + ff.getName());
    // // System.out.println(tu.getFilesSzie("/home/newhadoop/yck/test/" +
    // // ff.getName()));
    // // }
    // // tu.exists("/home/newhadoop/yck/test");
    // // System.out.println(tu.exists("./xxxx"));
    // long start3 = System.currentTimeMillis();
    // tu.isConnected();
    // tu.disconnect();
    // System.out.println("d时间:" + (System.currentTimeMillis() - start3));
    // }

    public boolean isConnected() {
        boolean isConnected = true;
        if (client == null) {
            return false;
        }
        if (client != null) {
            try {
                isConnected = client.connected();
                if (isConnected) {
                    String pwd = client.pwd(); // 测试一下是获取当前目录
                    log.info("验证连接是否可用FTP{}，当前目录:{}", ftpCfg, pwd);
                    return true;
                }
            } catch (Exception e) {
                log.error("判断" + this.ftpCfg.getHost() + "ftpclient .isconnected异常{}", e.getMessage());
                return false;
            }
        }
        return isConnected;
    }

    @Override
    public int hashCode() {
        log.info("调用hashCode:" + this.clientUUid.hashCode());
        return this.clientUUid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        log.info("调用equal target:" + ((NlFtpClient) obj).clientUUid);

        if (obj instanceof NlFtpClient) {
            return this.clientUUid.equals(((NlFtpClient) obj).clientUUid);
        }
        return false;
    }

    public String getClientUUid() {
        return clientUUid.toString();
    }
}
