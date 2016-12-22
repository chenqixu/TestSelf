package util.net;

import java.net.*;
import java.io.*;
import javax.swing.*;

/**
 * 1.����һ���˿ڣ�����1000��
 * 2.Ȼ��ͻ�����һ����Ϣ�����е�һ���ӳ���Ϳ�ʼ����������Ϣ�����յ�����Ϣ�򵯳������ڹر�����ͻ������ӡ�
 * 3.���¿ͻ�����ʱ�������������ĵڶ�����
 * */
public class Server {
	public Server() throws Exception {
		ServerSocket socket = new ServerSocket(1000);//��1000�˿ڼ���
		while (true) {
			Socket s = socket.accept();
			while (!s.isConnected()) {

			}
			new Processor(s).start();
		}
	}

	public static void main(String args[]) throws Exception {
		new Server();
	}
}

class Processor extends Thread {
	Socket s = null;

	public Processor(Socket s) throws Exception {
		this.s = s;
	}

	public void run() {
		try {
			ObjectInputStream obj = new ObjectInputStream(s.getInputStream());
			JOptionPane.showMessageDialog(null, (String) obj.readObject());
			obj.close();
		} catch (Exception e) {
		}
	}
}
