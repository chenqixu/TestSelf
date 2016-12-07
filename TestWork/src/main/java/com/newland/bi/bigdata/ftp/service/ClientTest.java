package com.newland.bi.bigdata.ftp.service;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTest {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost",21);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(new String("PWD"));
        out.flush();
        out.close();
        socket.close();
	}
}
