package util.net;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.*;

import javax.swing.JOptionPane;

public class MultiThreadServer {
	private int port = 8821;

	private ServerSocket serverSocket;

	private ExecutorService executorService;// 线程池

	private final int POOL_SIZE = 10;// 单个CPU线程池大小

	public MultiThreadServer() throws IOException {
		serverSocket = new ServerSocket(port);
		// Runtime的availableProcessor()方法返回当前系统的CPU数目.
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_SIZE);
		System.out.println("服务器启动");
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				// 接收客户连接,只要客户进行了连接,就会触发accept();从而建立连接
				socket = serverSocket.accept();
				executorService.execute(new Handler(socket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new MultiThreadServer().service();
	}
}

class Handler implements Runnable {
	private final Socket socket;

	Handler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {			
			//读取命令
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String msg = null;
//			while ((msg = br.readLine()) != null)
			if((msg = br.readLine()) != null)
				System.out.println(msg);
			
			//发送命令
			OutputStream socketOut = socket.getOutputStream();
			socketOut.write("aa".getBytes());
			socketOut.flush();
			socketOut.close();
			br.close();
			System.out.println("New connection accepted " +socket.getInetAddress() + ":" +socket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// read and service request
	}
}
