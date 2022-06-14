package com.bussiness.bi.bigdata.ftp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerB {
	public static void main(String[] args) throws Exception {
		final int PORT = 21;// 监听端口号
		ServerSocket s = new ServerSocket(PORT);
		while (true) {
			// 接受客户端请求
			Socket client = s.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
			pw.println("welcome.");
			FtpThread ft = new ServerB().new FtpThread(client);
			ft.start();
		}
	}
	
	class FtpThread extends Thread {
		private Socket socketClient;// 客户端socket
		public FtpThread(Socket socketClient){
			this.socketClient = socketClient;
		}
		public void run(){
			BufferedReader br = null;
			PrintWriter pw = null;
			String commands = "";
			try{
				br = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
				pw = new PrintWriter(this.socketClient.getOutputStream(), true);
				boolean b = true;
				while (b) {
					commands = br.readLine();
					if(commands==null) b = false;
					else System.out.println(commands);
				}			
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					br = null;
				}
				if(pw!=null){
					pw.close();
					pw = null;
				}
			}
		}
	}
}
