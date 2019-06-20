package com.newland.bi.bigdata.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * socket服务端 System.out.println 输出版本
 *
 * @author chenqixu
 */
public class DpiSocketServerSOUT {

    private int port;
    private boolean status = false;
    private ServerSocket serverSocket;

    public DpiSocketServerSOUT(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length == 1)
            new DpiSocketServerSOUT(Integer.valueOf(args[0])).start();
        else
            System.out.println("no enough args.");
    }

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几豪秒
     */
    public static void sleepMilliSecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 设置accept的超时时间来停止socket
     *
     * @param soTimeout accept timeout
     */
    public void startByTimeout(int soTimeout) {
        try {
            serverSocket = new ServerSocket(port);
            // 设置accept超时时间，否则退出调用stop的时候还需要发一次消息才会正常退出
            serverSocket.setSoTimeout(soTimeout);
            status = true;
            System.out.println("start server：" + serverSocket);
            while (status) {
                try {
                    // 接受客户端请求，阻塞状态
                    Socket client = serverSocket.accept();
                    if (client != null)
                        new DpiSocketServerSOUT.AcceptDeal(client).start();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("serverSocket isClosed：" + serverSocket.isClosed());
        }
    }

    /**
     * 使用close来关闭socket
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            status = true;
            System.out.println("start server：" + serverSocket);
            while (status) {
                // 接受客户端请求，阻塞状态
                Socket client = serverSocket.accept();
                if (client != null)
                    new DpiSocketServerSOUT.AcceptDeal(client).start();
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("socket closed")) {
                System.out.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("serverSocket isClosed：" + serverSocket.isClosed());
        }
    }

    /**
     * 设置accept的超时时间来停止socket
     */
    public void stopByTimeout() {
        System.out.println("stop server：" + serverSocket);
        status = false;
    }

    /**
     * 使用close来关闭socket
     */
    public void stop() {
        System.out.println("stop server：" + serverSocket);
        status = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端处理线程
     *
     * @author chenqixu
     */
    class AcceptDeal extends Thread {
        static final String LANG = "utf-8";
        private BufferedReader br = null;
        private Socket client;
        private PrintWriter pw = null;
        private boolean status;

        public AcceptDeal(Socket client) {
            System.out.println(this + " accetp client：" + client);
            this.client = client;
            try {
                br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                        Charset.forName(LANG)));
                pw = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = true;
        }

        @Override
        public void run() {
            String content;
            try {
                while (status) {
                    while ((content = br.readLine()) != null) {
                        System.out.println("client：" + client + "，read content：" + content);
                        pw.println(content + "|replay1|replay2|replay3|replay4|replay5|replay6|replay7|replay8");
//                        pw.println(content + "");
                    }
                    sleepMilliSecond(50);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (pw != null)
                    pw.close();
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
