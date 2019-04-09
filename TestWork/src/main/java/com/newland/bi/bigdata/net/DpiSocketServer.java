package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

/**
 * socket服务端
 *
 * @author chenqixu
 */
public class DpiSocketServer {

    private static Logger logger = LoggerFactory.getLogger(DpiSocketServer.class);
    private int port;
    private boolean status = false;
    private ServerSocket serverSocket;

    public DpiSocketServer(int port) {
        this.port = port;
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
            logger.info("start server：{}", serverSocket);
            while (status) {
                try {
                    // 接受客户端请求，阻塞状态
                    Socket client = serverSocket.accept();
                    if (client != null)
                        new AcceptDeal(client).start();
                } catch (SocketTimeoutException e) {
                    logger.info(e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info("serverSocket isClosed：{}", serverSocket.isClosed());
        }
    }

    /**
     * 使用close来关闭socket
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            status = true;
            logger.info("start server：{}", serverSocket);
            while (status) {
                // 接受客户端请求，阻塞状态
                Socket client = serverSocket.accept();
                if (client != null)
                    new AcceptDeal(client).start();
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("socket closed")) {
                logger.info(e.getMessage());
            } else {
                logger.error(e.getMessage(), e);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.info("serverSocket isClosed：{}", serverSocket.isClosed());
        }
    }

    /**
     * 设置accept的超时时间来停止socket
     */
    public void stopByTimeout() {
        logger.info("stop server：{}", serverSocket);
        status = false;
    }

    /**
     * 使用close来关闭socket
     */
    public void stop() {
        logger.info("stop server：{}", serverSocket);
        status = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
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
            logger.info("{} accetp client：{}", this, client);
            this.client = client;
            try {
                br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                        Charset.forName(LANG)));
                pw = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            status = true;
        }

        @Override
        public void run() {
            String content;
            try {
                while (status) {
                    while ((content = br.readLine()) != null) {
                        logger.info("client：{}，read content：{}", client, content);
                        pw.println(content + "|replay");
                    }
                    SleepUtils.sleepMilliSecond(50);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (pw != null)
                    pw.close();
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
