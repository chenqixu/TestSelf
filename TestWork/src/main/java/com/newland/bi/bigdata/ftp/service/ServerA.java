package com.newland.bi.bigdata.ftp.service;

import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 角色-服务器A
 */
public class ServerA {
	private static Logger logger = Logger.getLogger(ServerA.class);
	
	public static void main(String[] args) {
		//日志初始化
		PropertyConfigurator.configure("H:/Work/WorkSpace/MyEclipse10/self/test/src/main/resources/conf/log4j.properties");
		
		final String F_DIR = "H:/logs";// 根路径
		final int PORT = 21;// 监听端口号
		try {
			ServerSocket s = new ServerSocket(PORT);
			logger.info("Connecting to server A...");
			logger.info("Connected Successful! Local Port:" + s.getLocalPort()
					+ ". Default Directory:'" + F_DIR + "'.");
			while (true) {
				// 接受客户端请求
				Socket client = s.accept();
				// 创建服务线程
				new ClientThread(client, F_DIR).start();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
	}
}