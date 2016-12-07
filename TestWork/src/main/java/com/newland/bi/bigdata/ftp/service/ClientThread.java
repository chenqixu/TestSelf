package com.newland.bi.bigdata.ftp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * 客户端子线程类
 */
public class ClientThread extends Thread {
	private static Logger logger = Logger.getLogger(ClientThread.class);// 日志对象
	private final static Random generator = new Random();// 随机数
	
	public final String LOGIN_WARNING = "530 Please log in with USER and PASS first.";
	
	private Socket socketClient;// 客户端socket
	private String defaultdir;// 默认绝对路径
	private String rootdir;// 绝对路径
	private String pdir = "/";// 相对路径
	private String LANG = "UTF-8";	
	private String clientIp = null;// 记录客户端IP
	private String username = "not logged in";// 用户名
	private String password = "";// 口令
	private String command = "";// 命令
//	private boolean loginStuts = false;// 登录状态
	private String str = "";// 命令内容字符串
	private String cmd = ""; // 存放指令(空格前)
	private String param = ""; // 放当前指令之后的参数(空格后)
	private int port_high = 0;
	private int port_low = 0;
	private String retr_ip = "";// 接收文件的IP地址
	private Socket tempsocket = null;// 套接字
	private int type = 0; // 文件类型(ascII 或 bin)
	private int state = 0; // 用户状态标识符
	private String reply; // 返回报告	

	private Socket ctrlSocket; // 用于控制的套接字
	private Socket dataSocket; // 用于传输的套接字
	
	private BufferedReader br = null;
	private PrintWriter pw = null;

	public ClientThread(Socket client, String F_DIR) {
		this.socketClient = client;
		this.rootdir = F_DIR;
		this.defaultdir = F_DIR;

		try {
			br = new BufferedReader(new InputStreamReader(client.getInputStream(),
					Charset.forName(LANG)));
			pw = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
	}
	
	/**
	 * 打印信息到前台
	 * */
	private void println(String str) {
		if(pw!=null){
			pw.println(str);
			pw.flush();
		}
	}

	/**
	 * 末尾加上斜杠
	 * */
	String addTail(String s) {
		if (!s.endsWith("/"))
			s = s + "/";
		return s;
	}
	
	/**
	 * 判断路径的属性,返回 int
	 * */
	int validatePath(String s) {
		File f = new File(s); // 相对路径
		if (f.exists() && !f.isDirectory()) {
			String s1 = s.toLowerCase();
			String s2 = rootdir.toLowerCase();
			if (s1.startsWith(s2))
				return 1; // 文件存在且不是路径,且以rootdir 开始
			else
				return 0; // 文件存在且不是路径,不以rootdir 开始
		}
		f = new File(addTail(rootdir) + s);// 绝对路径
		if (f.exists() && !f.isDirectory()) {
			String s1 = (addTail(rootdir) + s).toLowerCase();
			String s2 = rootdir.toLowerCase();
			if (s1.startsWith(s2))
				return 2; // 文件存在且不是路径,且以rootdir 开始
			else
				return 0; // 文件存在且不是路径,不以rootdir 开始
		}
		return 0; // 其他情况
	}

	/**
	 *  解析命令
	 * */
	int parseInput(String s) {
		int p = 0;
		int i = -1;
		p = s.indexOf(" ");
		if (p == -1) // 如果是无参数命令(无空格)
			cmd = s;
		else
			cmd = s.substring(0, p); // 有参数命令,过滤参数

		if (p >= s.length() || p == -1)// 如果无空格,或空格在读入的s串最后或之外
			param = "";
		else
			param = s.substring(p + 1, s.length());
		cmd = cmd.toUpperCase(); // 转换该 String 为大写

		if (cmd.equals("CDUP"))
			i = 4;
		if (cmd.equals("CWD"))
			i = 6;
		if (cmd.equals("QUIT"))
			i = 7;
		if (cmd.equals("PORT"))
			i = 9;
		if (cmd.equals("TYPE"))
			i = 11;
		if (cmd.equals("RETR"))
			i = 14;
		if (cmd.equals("STOR"))
			i = 15;
		if (cmd.equals("ABOR"))
			i = 22;
		if (cmd.equals("DELE"))
			i = 23;
		if (cmd.equals("MKD"))
			i = 25;
		if (cmd.equals("PWD"))
			i = 26;
		if (cmd.equals("LIST"))
			i = 27;
		if (cmd.equals("NOOP"))
			i = 32;
		if (cmd.equals("XPWD"))
			i = 33;
		if (cmd.equals("XRMD"))
			i = 34;
		if (cmd.equals("NLST"))
			i = 35;
		if (cmd.equals("PASV"))
			i = 36;
		return i;
	}


	@Override
	public void run() {
		// 与cmd 一一对应的号
		int parseResult;		
		// 获取一些信息，客户IP
		clientIp = socketClient.getInetAddress().toString().substring(1);// 记录客户端IP
		// 未登陆是0
		state = FtpState.FS_WAIT_LOGIN;
		// 打印欢迎信息，注意"-"是关键字，不能输出
		println("220 FTP Server A version 1.0");
		logger.info("(" + username + ") (" + clientIp
				+ ")> Connected, sending welcome message...");
		logger.info("(" + username + ") (" + clientIp
				+ ")> 220-FTP Server A version 1.0");
		boolean finished = true;
		while (finished) {
			try {
				// 获取用户输入的命令
				command = br.readLine();
				if (null == command) {
					finished = false;
				}
			} catch (IOException e) {
				println("331 Failed to get command");
				logger.info("(" + username + ") (" + clientIp
						+ ")> 331 Failed to get command");
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
				finished = false;
			}
			/*
			 * 访问控制命令
			 */
			parseResult = parseInput(command); // 指令转化为指令号
			logger.info("[cmd]"+cmd+"[param]"+param+"[state]"+state);
			
			switch (state) // 用户状态开关
			{
			case FtpState.FS_WAIT_LOGIN:
				finished = commandUSER();
				break;
			case FtpState.FS_WAIT_PASS:
				finished = commandPASS();
				break;
			case FtpState.FS_LOGIN: {
				switch (parseResult)// 指令号开关,决定程序是否继续运行的关键
				{
				case -1:
					errCMD(); // 语法错
					break;
				case 4:
					finished = commandCDUP(); // 到上一层目录
					break;
				case 6:
					finished = commandCWD(); // 到指定的目录
					break;
				case 7:
					finished = commandQUIT(); // 退出
					break;
				case 9:
					finished = commandPORT(); // 客户端IP:地址+TCP 端口号
					break;
				case 11:
					finished = commandTYPE(); // 文件类型设置(ascII 或 bin)
					break;
				case 14:
					finished = commandRETR(); // 从服务器中获得文件
					break;
				case 15:
					finished = commandSTOR(); // 向服务器中发送文件
					break;
				case 22:
					finished = commandABOR(); // 关闭传输用连接dataSocket
					break;
				case 23:
					finished = commandDELE(); // 删除服务器上的指定文件
					break;
				case 25:
					finished = commandXMKD(); // 建立目录
					break;
				case 27:
					finished = commandLIST(); // 文件和目录的列表
					break;
				case 26:
					finished = commandPWD(); // 列出当前路径
					break;
				case 32:
					finished = commandNOOP(); // "命令正确" 信息
					break;
				case 33:
					finished = commandPWD(); // "当前目录" 信息
					break;
				case 34:
					finished = commandXRMD(); // 删除目录
					break;
				case 35:
					finished = commandNLST(); // 
					break;
				case 36:
					finished = commandPASV(); // 
					break;
				}
			}
				break;
			}
			println(reply);
		}
		try {
			logger.info("(" + username + ") (" + clientIp + ")> disconnected.");
			// logger.info("用户"+clientIp+"："+username+"退出");
			br.close();
			socketClient.close();
			pw.close();
			if (null != tempsocket) {
				tempsocket.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
	}
	
	/**
	 * 没有登录提示
	 * */
	void notLogin(){
		reply = LOGIN_WARNING;
		logger.info("(" + username + ") (" + clientIp + ")> "
				+ LOGIN_WARNING);
	}

	/**
	 * 错误命令
	 * */
	void errCMD() {
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		reply = "500 Syntax error, command unrecognized.";
		logger.info("(" + username + ") (" + clientIp
				+ ")> 500 Syntax error, command unrecognized.");
	}
	
	/**
	 * 登录输入用户
	 * */
	boolean commandUSER(){
		logger.info("(not logged in) (" + clientIp + ")> " + command);
		if (cmd.equals("USER")) {
			username = param;
			reply = "331 Password required for " + username;
			logger.info("(not logged in) (" + clientIp
					+ ")> 331 Password required for " + username);
			state = FtpState.FS_WAIT_PASS;
			return true;
		} else {
			if (cmd.equals("QUIT")) {
				commandQUIT(); // 退出
				return false;
			}
//			notLogin();
//			reply = "501 Parameter syntax error, user name does not match";
			return true;
		}
	}
	
	/**
	 * 登录验证密码
	 * */
	boolean commandPASS(){
		logger.info("(not logged in) (" + clientIp + ")> " + command);
		if (cmd.equals("PASS")) {
			password = param;
			if (username.equals("root") && password.equals("root")) {
				state = FtpState.FS_LOGIN;
				reply = "230 Logged on";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 230 Logged on");
				// logger.info("客户端 "+clientIp+" 通过 "+username+"用户登录");
				return true;
			} else {
				reply = "530 Login or password incorrect!";
				logger.info("(not logged in) (" + clientIp
						+ ")> 530 Login or password incorrect!");
				username = "not logged in";
				state = FtpState.FS_WAIT_LOGIN;
				return true;
			}
		} else {
			reply = "501 The argument syntax error, the password does not match";
			state = FtpState.FS_WAIT_LOGIN;
			return true;
		}
	}
	
	boolean commandPWD(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		// logger.info("用户"+clientIp+"："+username+"执行PWD命令");
		reply = "257 "+pdir+" is current directory";
		logger.info("(" + username + ") (" + clientIp
				+ ")> 257 "+pdir+" is current directory");
		return true;
	}
	
	boolean commandCWD(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
//		str = command.substring(3).trim();
		str = param.trim();
		if ("".equals(str)) {
			reply = "250 Broken client detected, missing argument to CWD. "+pdir+" is current directory.";
			logger.info("("
					+ username
					+ ") ("
					+ clientIp
					+ ")> 250 Broken client detected, missing argument to CWD. "+pdir+" is current directory.");
		} else {
			// 判断目录是否存在
			String tmpDir = rootdir + "/" + str;
			File file = new File(tmpDir);
			if (file.exists()) {// 目录存在
				try {
					rootdir = file.getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if ("/".equals(pdir)) {
					pdir = pdir + str;
				} else {
					pdir = pdir + "/" + str;
				}
				// logger.info("用户"+clientIp+"："+username+"执行CWD命令");
				reply = "250 CWD successful. "+pdir+" is current directory";
				logger.info("("
						+ username
						+ ") ("
						+ clientIp
						+ ")> 250 CWD successful. "+pdir+" is current directory");
			} else {// 目录不存在
				reply = "550 CWD failed. "+pdir+": directory not found.";
				logger.info("("
						+ username
						+ ") ("
						+ clientIp
						+ ")> 550 CWD failed. "+pdir+": directory not found.");
			}
			
			
//			// 如果是..，则判断相对路径，如果是根目录，则不动
//			if (str.equals("..") || str.equals("..\\")) {
//				if (pdir.equals("/")) {
//				} else {
//				}
//			} else if (param.equals(".") || param.equals(".\\")) {
//				// 异常命令
//			} else {
//				// 判断是否是目录，是则切换
//			}
		}
		return true;
	}
	
	boolean commandQUIT(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		boolean flag = false;
		reply = "221 Goodbye";
		logger.info("(" + username + ") (" + clientIp
				+ ")> 221 Goodbye");
//		try {
//			Thread.currentThread();
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage());
//			for (StackTraceElement ste : e.getStackTrace()) {
//				logger.error(ste.toString());
//			}
//		}
		return flag;
	}
	
	/**
	 * PORT命令，主动模式传输数据
	 * */
	boolean commandPORT(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		try {
//			str = command.substring(4).trim();
			str = param;
			port_low = Integer.parseInt(str.substring(str
					.lastIndexOf(",") + 1));
			port_high = Integer.parseInt(str.substring(0,
					str.lastIndexOf(",")).substring(
					str.substring(0, str.lastIndexOf(","))
							.lastIndexOf(",") + 1));
			String str1 = str.substring(0,
					str.substring(0, str.lastIndexOf(","))
							.lastIndexOf(","));
			retr_ip = str1.replace(",", ".");
			try {
				// 实例化主动模式下的socket
				tempsocket = new Socket(retr_ip, port_high * 256
						+ port_low);
				// logger.info("用户"+clientIp+"："+username+"执行PORT命令");
				reply = "200 port command successful";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 200 port command successful");
			} catch (ConnectException ce) {
				reply = "425 Can't open data connection.";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 425 Can't open data connection.");
				logger.error(ce.getMessage());
				for (StackTraceElement ste : ce.getStackTrace()) {
					logger.error(ste.toString());
				}
			} catch (UnknownHostException e) {
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
		} catch (NumberFormatException e) {
			reply = "503 Bad sequence of commands.";
			logger.info("(" + username + ") (" + clientIp
					+ ")> 503 Bad sequence of commands.");
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
		return true;
	}
	
	/**
	 * PASV命令，被动模式传输数据
	 * */
	boolean commandPASV(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		ServerSocket ss = null;
		while (true) {
			// 获取服务器空闲端口
			port_high = 1 + generator.nextInt(20);
			port_low = 100 + generator.nextInt(1000);
			try {
				// 服务器绑定端口
				ss = new ServerSocket(port_high * 256 + port_low);
				break;
			} catch (IOException e) {
				continue;
			}
		}
		// logger.info("用户"+clientIp+"："+username+"执行PASV命令");
		InetAddress i = null;
		try {
			i = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		reply = "227 Entering Passive Mode ("
				+ i.getHostAddress().replace(".", ",") + ","
				+ port_high + "," + port_low + ")";
		logger.info("(" + username + ") (" + clientIp
				+ ")> 227 Entering Passive Mode ("
				+ i.getHostAddress().replace(".", ",") + ","
				+ port_high + "," + port_low + ")");
		try {
			// 被动模式下的socket
			tempsocket = ss.accept();
			ss.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
		return true;
	}
	
	boolean commandRETR(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
//		str = command.substring(4).trim();
		str = param;
		if ("".equals(str)) {
			reply = "501 Syntax error";
			logger.info("(" + username + ") (" + clientIp
					+ ")> 501 Syntax error");
		} else {
			try {
				reply = "150 Opening data channel for file transfer.";
				logger.info("("
						+ username
						+ ") ("
						+ clientIp
						+ ")> 150 Opening data channel for file transfer.");
				RandomAccessFile outfile = null;
				OutputStream outsocket = null;
				try {
					// 创建从中读取和向其中写入（可选）的随机访问文件流，该文件具有指定名称
					outfile = new RandomAccessFile(rootdir + "/" + str,
							"r");
					outsocket = tempsocket.getOutputStream();
				} catch (FileNotFoundException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				}
				byte bytebuffer[] = new byte[1024];
				int length;
				try {
					while ((length = outfile.read(bytebuffer)) != -1) {
						outsocket.write(bytebuffer, 0, length);
					}
					outsocket.close();
					outfile.close();
					tempsocket.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				}
				// logger.info("用户"+clientIp+"："+username+"执行RETR命令");
				reply = "226 Transfer OK";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 226 Transfer OK");
			} catch (Exception e) {
				reply = "503 Bad sequence of commands.";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 503 Bad sequence of commands.");
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
		}
		return true;
	}
	
	boolean commandSTOR(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
//		str = command.substring(4).trim();
		str = param;
		if ("".equals(str)) {
			reply = "501 Syntax error";
			logger.info("(" + username + ") (" + clientIp
					+ ")> 501 Syntax error");
		} else {
			try {
				reply = "150 Opening data channel for file transfer.";
				logger.info("("
						+ username
						+ ") ("
						+ clientIp
						+ ")> 150 Opening data channel for file transfer.");
				RandomAccessFile infile = null;
				InputStream insocket = null;
				try {
					infile = new RandomAccessFile(rootdir + "/" + str,
							"rw");
					insocket = tempsocket.getInputStream();
				} catch (FileNotFoundException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				} catch (IOException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				}
				byte bytebuffer[] = new byte[1024];
				int length;
				try {
					while ((length = insocket.read(bytebuffer)) != -1) {
						infile.write(bytebuffer, 0, length);
					}
					insocket.close();
					infile.close();
					tempsocket.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						logger.error(ste.toString());
					}
				}
				// logger.info("用户"+clientIp+"："+username+"执行STOR命令");
				reply = "226 Transfer OK";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 226 Transfer OK");
			} catch (Exception e) {
				reply = "503 Bad sequence of commands.";
				logger.info("(" + username + ") (" + clientIp
						+ ")> 503 Bad sequence of commands.");
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
		}
		return true;
	}
	
	boolean commandNLST(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		try {
			println("150 Opening data channel for directory list.");
			logger.info("("
					+ username
					+ ") ("
					+ clientIp
					+ ")> 150 Opening data channel for directory list.");
			PrintWriter pwr = null;
			try {
				pwr = new PrintWriter(tempsocket.getOutputStream(),
						true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			File file = new File(rootdir);
			String[] dirstructure = new String[10];
			dirstructure = file.list();
			for (int i = 0; i < dirstructure.length; i++) {
				pwr.println(dirstructure[i]);
			}
			try {
				tempsocket.close();
				pwr.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
			// logger.info("用户"+clientIp+"："+username+"执行NLST命令");
			println("226 Transfer OK");
			logger.info("(" + username + ") (" + clientIp
					+ ")> 226 Transfer OK");
		} catch (Exception e) {
			println("503 Bad sequence of commands.");
			logger.info("(" + username + ") (" + clientIp
					+ ")> 503 Bad sequence of commands.");
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
		return true;
	}
	
	boolean commandLIST(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		try {
			println("150 Opening data channel for directory list.");
			logger.info("("
					+ username
					+ ") ("
					+ clientIp
					+ ")> 150 Opening data channel for directory list.");
			PrintWriter pwr = null;
			try {
				pwr = new PrintWriter(tempsocket.getOutputStream(),
						true);
			} catch (IOException e) {
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
			FtpUtil.getDetailList(pwr, rootdir);
			try {
				tempsocket.close();
				pwr.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				for (StackTraceElement ste : e.getStackTrace()) {
					logger.error(ste.toString());
				}
			}
			// logger.info("用户"+clientIp+"："+username+"执行LIST命令");
			println("226 Transfer OK");
			logger.info("(" + username + ") (" + clientIp
					+ ")> 226 Transfer OK");
		} catch (Exception e) {
			println("503 Bad sequence of commands.");
			logger.info("(" + username + ") (" + clientIp
					+ ")> 503 Bad sequence of commands.");
			logger.error(e.getMessage());
			for (StackTraceElement ste : e.getStackTrace()) {
				logger.error(ste.toString());
			}
		}
		return true;
	}
	
	/**
	 * 建立目录
	 * */
	boolean commandXMKD(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		String s1 = param.toLowerCase();
		String s2 = rootdir.toLowerCase();
		if (s1.startsWith(s2)) {
			File f = new File(param);
			if (f.exists()) {
				reply = "550 Requested action not executed, directory already exists";
			} else {
				f.mkdirs();
				reply = "250 Requested file processing end, directory build";
			}
		} else {
			File f = new File(addTail(rootdir) + param);
			if (f.exists()) {
				reply = "550 Requested action not executed, directory already exists";
			} else {
				f.mkdirs();
				reply = "250 Requested file processing end, directory build";
			}
		}
		return true;
	}
	
	/**
	 * 删除目录
	 * */
	boolean commandXRMD(){
		logger.info("(" + username + ") (" + clientIp + ")> " + command);
		logger.info("[dir]"+rootdir+"[FtpServer.initDir]"+FtpServer.initDir);
		
		return true;
	}
	
	/**
	 * 到上一层目录
	 * */
	boolean commandCDUP(){
		rootdir = defaultdir;
		File f = new File(rootdir);
		if (f.getParent() != null && (!rootdir.equals(rootdir)))// 有父路径 && 不是根路径
		{
			rootdir = f.getParent();
			reply = "200 Command correct";
		} else {
			reply = "550 There is no parent path to the current directory";
		}
		return true;
	}	

	boolean commandTYPE() // TYPE 命令用来完成类型设置
	{
		if (param.equals("A")) {
			type = FtpState.FTYPE_ASCII;// 0
			reply = "200 Command correct, turn ASCII mode";
		} else if (param.equals("I")) {
			type = FtpState.FTYPE_IMAGE;// 1
			reply = "200 Command correct, turn BINARY mode";
		} else
			reply = "504 Command cannot execute this parameter";

		return true;
	}	

	// 强关dataSocket 流
	boolean commandABOR() {
		try {
			dataSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			reply = "451 Request failed: transmission failure";
			return true;
		}
		reply = "421 Service is not available, closing data transfer connection";
		return true;
	}
	
	// 删除服务器上的指定文件
	boolean commandDELE() {
		int i = validatePath(param);
		if (i == 0) {
			reply = "550 The requested action is not executed, the file does not exist, or the directory is incorrect, or otherwise";
			return true;
		}
		if (i == 1) {
			File f = new File(param);
			f.delete();
		}
		if (i == 2) {
			File f = new File(addTail(rootdir) + param);
			f.delete();
		}

		reply = "250 Request the end of the file processing, the successful removal of the file on the server";
		return true;
	}
	
	boolean commandNOOP() {
		reply = "200 Command correct.";
		return true;
	}
}