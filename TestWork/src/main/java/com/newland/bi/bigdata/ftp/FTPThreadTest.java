package com.newland.bi.bigdata.ftp;

import com.newland.bd.model.cfg.FtpCfg;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FTPThreadTest
 *
 * @author chenqixu
 */
public class FTPThreadTest {
    static FTPThreadTest ftpThreadTest = new FTPThreadTest();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        if (args.length == 1) {
            int cnt = Integer.valueOf(args[0]);
            System.out.println("cnt：" + cnt);
            System.out.println("add thread.");
            for (int i = 0; i < cnt; i++) {
                threads.add(new Thread(FTPThreadTest.createRunnable()));
            }
            System.out.println("start thread.");
            for (Thread thread : threads) {
                thread.start();
            }
        }
    }

    public static Runnable createRunnable() {
        return ftpThreadTest.new FTPThread();
    }

    public static FTPClient getConnection(FtpParamCfg ftpParamCfg) {
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
                    System.out.println("FtpUtilException：关闭FTP连接异常，" + e1.getMessage());
                }
            }
            ftpClient = null;
            System.out.println("FtpUtilException：%%%%失败连接FTP服务器:" + ftpParamCfg.getHost()
                    + ",端口号:" + ftpParamCfg.getPort() + ",FTP用户名:"
                    + ftpParamCfg.getUser() + ",FTP密码:"
                    + ftpParamCfg.getPassword());
        }
        return ftpClient;
    }

    public static void closeConnection(FTPClient ftpClient) {
        if (ftpClient != null) {
            String RemoteAddress = ftpClient.getRemoteAddress().getHostAddress();
            int port = ftpClient.getRemotePort();
            try {
                ftpClient.logout();
            } catch (Exception e) {
                System.out.println("FtpUtilException：%%%%FTPClient在logout时出错!!!" + e.getMessage());
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        ftpClient.disconnect();
                    } catch (Exception e2) {
                        System.out.println("FtpUtilException：%%%%FTPClient在disconnect时出错!!!" + e2.getMessage());
                    }
                }
            }
            System.out.println("成功关闭ftpClient" + RemoteAddress + ":" + port);
        }
    }

    public void listFile() {

    }

    public void downLoadFile() {

    }

    class FtpParamCfg {

        private String host;
        private Integer port;
        private String user;
        private String password;
        private boolean isSftp;

        public FtpParamCfg() {
        }

        public FtpParamCfg(FtpCfg ftpCfg) {
            host = ftpCfg.getHost();
            port = ftpCfg.getPort();
            user = ftpCfg.getUser();
            password = ftpCfg.getPassword();
            isSftp = ftpCfg.isUseSftp();
        }

        @Override
        public String toString() {
            return user + "@" + host + ":" + port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isSftp() {
            return isSftp;
        }

        public void setSftp(boolean sftp) {
            isSftp = sftp;
        }
    }

    class FTPThread implements Runnable {

        @Override
        public void run() {

        }
    }
}
