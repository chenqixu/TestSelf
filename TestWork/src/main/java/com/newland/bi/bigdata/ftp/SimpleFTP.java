package com.newland.bi.bigdata.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;
 
 
/**
 * @description 没有图形用户界面， 只编写了连接，获取当前目录，上传文件功能。
 */
public class SimpleFTP {
 
	private Socket socket = null;
	
	private BufferedReader reader = null;
	
	private BufferedWriter writer = null;
	
	private static boolean debug = true;
	
	private String user = "edc_base";
	
	private String pass = "edc_base";
	
	public SimpleFTP() {		
	}
	
	/**
	 * connect to the ftp server
	 * @param host
	 * @throws Exception
	 */
	public synchronized void connect(String host) throws Exception {
		connect(host, 21, user, pass);
	}
	
	public synchronized void connect(String host, int port, String user, String pass) throws Exception {
		if(socket != null) {
			throw new Exception("already connect!");
		}
		socket = new Socket(host, port);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		String response = readLine();
		if(!response.startsWith("220")) {
			throw new Exception("unknow response after connect!");
		}
		sendLine("USER " + user);
		response = readLine();
		if(!response.startsWith("331")) {
			throw new Exception("unknow response after send user");
		}
		sendLine("PASS " + pass);
		response = readLine();
		if(!response.startsWith("230")) {
			throw new Exception("unknow response after send pass");
		}
		System.out.println("login!");
	}
	
	private void sendLine(String line) throws Exception {
		if(socket == null) {
			throw new Exception("not connect!");
		}
		writer.write(line + "\r\n");
		writer.flush();
		if(debug) {
			System.out.println(">" + line);
		}
	}
	
	private String readLine() throws IOException {
		String line = reader.readLine();
		if(debug) {
			System.out.println("<" + line);
		}
		return line;
	}
	
	/**
	 * get the working directory of the FTP server
	 * @return
	 * @throws Exception
	 */
	public synchronized String pwd() throws Exception {
		sendLine("PWD");
		String dir = null;
		String response = readLine();
		System.out.println("[response]"+response);
		if(response.startsWith("257")) {
			int firstQuote = response.indexOf("/");
			int secondQuote = response.indexOf("/", firstQuote + 1);
			if(secondQuote > 0) {
				dir = response.substring(firstQuote + 1, secondQuote);
			}
		}
		return dir;
	}
	
	public synchronized String list() throws Exception {
		sendLine("PASV");
		System.out.println("[response]"+readLine());
		sendLine("LIST /");
		System.out.println("[response]"+readLine());
		return null;
	}
	
	/**
	 * send a file to ftp server
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean stor(File file) throws Exception {
		if(!file.isDirectory()) {
			throw new Exception("cannot upload a directory!");
		}
		String fileName = file.getName();
		return upload(new FileInputStream(file), fileName);
	}
	
	public synchronized boolean upload(InputStream inputStream, String fileName) throws Exception {
		BufferedInputStream input = new BufferedInputStream(inputStream);
		sendLine("PASV");
		String response = readLine();
		if(!response.startsWith("227")) {
			throw new Exception("not request passive mode!");
		}
		String ip = null;
		int port = -1;
		int opening = response.indexOf('(');
		int closing = response.indexOf(')', opening + 1);
		if(closing > 0) {
			String dataLink = response.substring(opening + 1, closing);
			StringTokenizer tokenzier = new StringTokenizer(dataLink, ",");
			try {
				ip = tokenzier.nextToken() + "." + tokenzier.nextToken() + "." 
						+  tokenzier.nextToken() + "." + tokenzier.nextToken();
				port = Integer.parseInt(tokenzier.nextToken()) * 256 +Integer.parseInt(tokenzier.nextToken());;
			} catch (Exception e) {
				throw new Exception("bad data link after upload!");
			}
		}
		sendLine("STOR " + fileName);
		Socket dataSocket = new Socket(ip, port);
		response = readLine();
		if(!response.startsWith("150")) {
			throw new Exception("not allowed to send the file!");
		}
		BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		while((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		output.flush();
		output.close();
		input.close();
		response = readLine();
		return response.startsWith("226");
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception { 
		SimpleFTP  ftp = new SimpleFTP();
		ftp.connect("10.1.8.78");
		ftp.pwd();
		ftp.list();
	}
 
}
