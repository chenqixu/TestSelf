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

	private ExecutorService executorService;// �̳߳�

	private final int POOL_SIZE = 10;// ����CPU�̳߳ش�С

	public MultiThreadServer() throws IOException {
		serverSocket = new ServerSocket(port);
		// Runtime��availableProcessor()�������ص�ǰϵͳ��CPU��Ŀ.
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_SIZE);
		System.out.println("����������");
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				// ���տͻ�����,ֻҪ�ͻ�����������,�ͻᴥ��accept();�Ӷ���������
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
			//��ȡ����
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String msg = null;
//			while ((msg = br.readLine()) != null)
			if((msg = br.readLine()) != null)
				System.out.println(msg);
			
			//��������
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
