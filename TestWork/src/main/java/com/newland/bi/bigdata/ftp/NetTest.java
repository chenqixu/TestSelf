package com.newland.bi.bigdata.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class NetTest {
//	public static void closeFtpConnect(FTPClient ftpClient) {
//		try {
//			if (ftpClient != null && ftpClient.isConnected()) {
//				ftpClient.logout();
//				ftpClient.disconnect();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) throws Exception {
////		java.net.SocketInputStream a;
////		java.net.SocketException b;
//		String hostname = "192.168.230.128";
//		int port = 21;
//		String username = "hadoop";
//		String password = "hadoop";
//		FTPClient ftpClient = new FTPClient();
////		ftpClient.setConnectTimeout(1);
//		System.out.println("connect");
//		ftpClient.connect(hostname, port);
//		System.out.println("login");
//		ftpClient.login(username, password);
//		System.out.println("setFileType");
//		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//		System.out.println("enterLocalPassiveMode");
//		ftpClient.enterLocalPassiveMode();
//		System.out.println("setDataTimeout");
//		ftpClient.setDataTimeout(300000);
//		System.out.println("[isAvailable]"+ftpClient.isAvailable());
//		System.out.println("[isConnected]"+ftpClient.isConnected());
//		int i = 0;
//		while(i<10){
//			i++;
//			System.out.println("sleep 1 seconds. i is "+i);
//			Thread.sleep(1000);
//		}
//		System.out.println("close ftp connect start.");
//		closeFtpConnect(ftpClient);
//		System.out.println("close ftp connect end.");
//		System.out.println("[isAvailable]"+ftpClient.isAvailable());
//		System.out.println("[isConnected]"+ftpClient.isConnected());
////		while(true){
////			System.out.println("1");
////			throw new IOException("Connection timed out.");
////		}
//	}
}
