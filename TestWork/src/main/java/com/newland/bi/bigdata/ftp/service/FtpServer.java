package com.newland.bi.bigdata.ftp.service;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FtpServer {
	public static String initDir = "";
	public static ArrayList<UserInfo> usersInfo = new ArrayList<UserInfo>();	
	static {
		UserInfo u1 = new UserInfo();
		u1.setUser("test");
		u1.setPassword("test");
		u1.setWorkDir("h:/logs/");
		usersInfo.add(u1);
	}
	
	public static void main(String[] args) throws Exception {
		int counter = 0;
		int i = 0;
		ArrayList<FtpHandler> users = new ArrayList<FtpHandler>();
		// 监听21号端口,21口用于控制,20口用于传数据
		ServerSocket s = new ServerSocket(21);
		for (;;) {
			// 接受客户端请求
			Socket incoming = s.accept();
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);// 文本文本输出流
			out.println("220 Ready to serve you" + ",You are the current " + counter + " landing!");// 命令正确的提示
			
			// 创建服务线程
			FtpHandler h = new FtpHandler(incoming, i);
			h.start();
			users.add(h); // 将此用户线程加入到这个 ArrayList 中
			counter++;
			i++;
		}
	}
}
