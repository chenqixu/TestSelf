package util.net;

import java.net.*;
import java.io.*;
import javax.swing.*;

/**
 * 1.监听一个端口，比如1000。
 * 2.然后客户发送一段信息，其中的一个子程序就开始处理这则信息，接收到后信息框弹出来，在关闭这个客户的连接。
 * 3.有新客户来的时候继续处理上面的第二条。
 * */
public class Server {
	public Server() throws Exception {
		ServerSocket socket = new ServerSocket(1000);//在1000端口监听
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
