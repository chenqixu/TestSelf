package com.cqx.pool.ftp;

import com.enterprisedt.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chenqixu
 * @description 文件转换工具类
 * @date 2018/11/28 23:04
 */
public class FileTransferUtil {

    private static Logger log = LoggerFactory.getLogger(FileTransferUtil.class);

    public static final int BUFFER_SIZE = 5 * 1024 * 1024; // 设置文件缓冲区为5M。加快下载速度
    public static final String FTP = "FTP";
    public static final String SFTP = "SFTP";

    private FtpCfg ftpCfg;
    private NlFtpClient client = null;
    private String borrower;

    /**
     * 获取一个FTP连接
     *
     * @param ftpCfg
     * @throws Exception
     */
    public FileTransferUtil(FtpCfg ftpCfg) throws Exception {
        this(ftpCfg, "default.Thread." + Thread.currentThread().getName());
    }

    /**
     * 获取一个FTP连接
     *
     * @param ftpCfg
     * @param borrower 借出者的标识。比如类名+方法名，用来跟踪连接线程被谁借出不还
     * @throws Exception
     */
    public FileTransferUtil(FtpCfg ftpCfg, String borrower) throws Exception {
        this.ftpCfg = ftpCfg;
        this.borrower = borrower;
        FtpClientPool pool = FtpClientPool.getInstance();
        try {
            client = pool.borrowObject(ftpCfg, FtpClientFactory.CONNECT_TIME_WAIT); // 借取一个消息，5秒钟返回
            FtpActivateClientMetrics.borrowMark(borrower, client);
            log.info("获取新连接,clientUUid {}，线程池当前活跃active数:NumActive{} {}", client.getClientUUid(), pool.getNumActive(ftpCfg));
        } catch (Exception e) {
            FtpActivateClientMetrics.printStatus();
            throw new IOException("无法获取FTP连接.连接配置:" + ftpCfg, e);
        }
    }

    /**
     * 关闭FTP客户端连接
     *
     * @return 关闭成功标识
     * @throws IOException
     * @throws Exception
     */
    public void disconnect() {
        if (this.client == null) {
            log.warn("ftp连接对象{}已经归还过了", ftpCfg);
        }
        try {
            log.info("归还ftp连接对象{}，当前key:{}", this.client.getClientUUid(), ftpCfg);
            FtpClientPool.getInstance().returnObject(ftpCfg, this.client);
        } catch (Exception e) {
            FtpActivateClientMetrics.printStatus();
            log.warn("归还ftp连接对象{}出现{}异常，当前key:{}", this.client.getClientUUid(), e.getMessage(), ftpCfg, e);
            try {
                if (this.client != null) {
                    client.disconnect();
                }
            } catch (Exception e1) {
                log.warn("释放连接对象发生异常，当前key:" + ftpCfg, e);
            }
        } finally {
            FtpActivateClientMetrics.returnMark(borrower, client);
        }
        client = null;
    }

    public FtpCfg getFtpCfg() {
        return ftpCfg;
    }

    public String toString() {
        if (client != null) {
            return client.toString();
        }
        return this.ftpCfg.toString();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public static class FtpUtilRespInfo {
        private int totalCount = 0;
        private int successCount = 0;
        private int failCount = 0;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount() {
            this.totalCount++;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount() {
            this.successCount++;
        }

        public int getFailCount() {
            return failCount;
        }

        public void setFailCount() {
            this.failCount++;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public void setFailCount(int failCount) {
            this.failCount = failCount;
        }

    }

    public static class FTPFileInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        String name;
        String path;
        String host;// 所在机器
        boolean isFile;
        boolean isDir;
        Date createTime;
        Date lastModifiedTime;
        long size;

        public FTPFileInfo() {
        }

        public FTPFileInfo(FTPFile ftpFile, String currentDirPath, String host) {
            this.name = ftpFile.getName();
            this.path = currentDirPath; // 不使用ftpFile.getPath()，因为远程使用通匹配符查询的方式，api不会返回正确的文件路径
            this.isFile = ftpFile.isFile();
            this.isDir = ftpFile.isDir();
            this.createTime = ftpFile.created() != null ? ftpFile.created() : ftpFile.lastModified();
            this.lastModifiedTime = ftpFile.lastModified();
            this.size = ftpFile.size();
            this.host = host;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isFile() {
            return isFile;
        }

        public void setFile(boolean isFile) {
            this.isFile = isFile;
        }

        public boolean isDir() {
            return isDir;
        }

        public void setDir(boolean isDir) {
            this.isDir = isDir;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getLastModifiedTime() {
            if (lastModifiedTime == null) {
                return createTime;
            }
            return lastModifiedTime;
        }

        public void setLastModifiedTime(Date lastModifiedTime) {
            this.lastModifiedTime = lastModifiedTime;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

    }
}
