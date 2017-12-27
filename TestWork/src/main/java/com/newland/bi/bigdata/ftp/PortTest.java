package com.newland.bi.bigdata.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class PortTest {
	public static void main(String[] args) {
    	FTPClient ftpClient = null;
    	String ftpServerIp = "192.168.230.128";
    	int ftpServerPort = 21;
    	String ftpServerUser = "hadoop";
    	String ftpServerPassword = "zyh";
    	int type = 0;// 0：本机；1：开发环境
    	if(type==0){
        	ftpServerIp = "192.168.230.128";
        	ftpServerPort = 21;
        	ftpServerUser = "hadoop";
        	ftpServerPassword = "hadoop";
    	}else{
        	ftpServerIp = "10.1.8.96";
        	ftpServerPort = 32021;
        	ftpServerUser = "zyh";
        	ftpServerPassword = "zyh";
    	}
    	try {
    		ftpClient = new FTPClient();
    		ftpClient.connect(ftpServerIp, ftpServerPort);
			ftpClient.login(ftpServerUser, ftpServerPassword);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        ftpClient.enterLocalActiveMode();
	        ftpClient.setDataTimeout(300000);
//	        ftpClient.setActivePortRange(32020, 32020);
	        ftpClient.changeWorkingDirectory("/");
	        for(FTPFile ff : ftpClient.listFiles()){
	        	System.out.println(ff.getName());
	        }
    	} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ftpClient!=null){
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
			ftpClient=null;
		}
	}
}
