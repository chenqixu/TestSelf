package util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadClient {

	public static void main(String[] args) {
		new MultiThreadClient().response();
	}

	public void response() {
		System.out.println("�ͻ�������...");
		int numTasks = 2;
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < numTasks; i++) {
			exec.execute(createTask(i));
		}

	}

	// ����һ���򵥵����� 
	private static Runnable createTask(final int taskID) { // ,String sIp,final int iPort 
		return new Runnable() {
			private Socket socket = null;

			private int port = 8821;

			public void run() {
				System.out.println("�ͻ��� ������...");
				System.out.println("Task " + taskID + ":start");
				try {
					socket = new Socket("localhost", port);
					// ���͹ر����� 
					OutputStream socketOut = socket.getOutputStream();
					System.out.println("try �ͻ��� ������...");
					socketOut.write("shutdown\r\n".getBytes());
					socketOut.flush();

					// ���շ������ķ��� 
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String msg = null;
					while ((msg = br.readLine()) != null)
						System.out.println(msg);
					
					socketOut.close();
					br.close();
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
			}

		};
	}
}