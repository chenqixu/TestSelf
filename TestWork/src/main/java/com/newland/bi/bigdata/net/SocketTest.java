package com.newland.bi.bigdata.net;

import java.net.Socket;

public class SocketTest {
        public static void main(String[] args) {
                if(args.length==2){
                        String IP = args[0];
                        int port = Integer.valueOf(args[1]);
                        System.out.println("[IP]"+IP+"[port]"+port);
                        Socket client = null;
                        try {
                                client = new Socket(IP, port);
                                System.out.println("端口已开放");
                                client.close();
                        } catch (Exception e) {
                                System.out.println("端口未开放");
                        }
                }
        }
}
//javac SocketTest.java
//java -cp `pwd` SocketTest 10.1.8.1 22
