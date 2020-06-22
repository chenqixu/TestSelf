package com.newland.bi.bigdata.ftp;

import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.enterprisedt.net.ftp.pro.ProFTPClientInterface;
import com.enterprisedt.util.license.License;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class PortTest {

    static String host = "10.1.8.75";
    static String user = "edc_base";
    static String pwd = "AkDXk0&d";
    static String filepath = "/home/edc_base/";
    static String filename = "P9961420200618??????.AVL";
    int ftpPort = 21;
    int timeout = 120000;
    String contorlCharset = null;

    public static void main(String[] args) {
        if (args.length == 5) {
            host = args[0];
            user = args[1];
            pwd = args[2];
            filepath = args[3];
            filename = args[4];
            new PortTest().edtFtp();
        } else {
            new PortTest().edtFtp();
        }
    }

    public void apacheFtp() {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, ftpPort);
            ftpClient.login(user, pwd);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalActiveMode();
            ftpClient.setDataTimeout(300000);
            ftpClient.changeWorkingDirectory(filepath);
            int count = 0;
            for (FTPFile ff : ftpClient.listFiles(filename)) {
                System.out.println(ff.getName());
                count++;
                if (count > 5) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.logout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ftpClient = null;
        }
    }

    public void edtFtp() {
        License.setLicenseDetails("hello", "371-2454-4908-7510");
        ProFTPClientInterface client = null;
        ProFTPClient proFTPClient = null;
        try {
            proFTPClient = new ProFTPClient();
            client = proFTPClient;
            proFTPClient.setRemoteHost(host);
            proFTPClient.setRemotePort(ftpPort);
            proFTPClient.setTimeout(timeout);
            proFTPClient.setTransferBufferSize(524288);
            proFTPClient.setNetworkBufferSize(524288);
            if (contorlCharset != null) {
                proFTPClient.setControlEncoding(contorlCharset);
            }
            proFTPClient.connect();
            proFTPClient.login(user, pwd);
            if (client instanceof ProFTPClient) {
                System.out.println("设置ftp为二进制模式");
                client.setType(FTPTransferType.BINARY);
            }
//            String[] files = client.dir(filepath);
            client.chdir(filepath);
            com.enterprisedt.net.ftp.FTPFile[] files = client.dirDetails(filepath + filename);
            int count = 0;
//            for (String file : files) {
            for (com.enterprisedt.net.ftp.FTPFile file : files) {
                System.out.println("扫描到："+file);
                count++;
                if (count > 5) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
            proFTPClient = null;
        }
    }
}
