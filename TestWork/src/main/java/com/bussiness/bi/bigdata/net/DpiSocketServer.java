package com.bussiness.bi.bigdata.net;

import com.bussiness.bi.bigdata.net.impl.AcceptDealFile;
import com.bussiness.bi.bigdata.net.impl.AcceptDealNetBean;
import com.bussiness.bi.bigdata.net.impl.IAcceptDeal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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
                    if (client != null) {
                        IAcceptDeal iAcceptDeal = new AcceptDealNetBean(client);
                        iAcceptDeal.init();
                        iAcceptDeal.start();
                    }
                } catch (SocketTimeoutException e) {
                    logger.info(e.getMessage());
                }
            }
        } catch (Exception e) {
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
                if (client != null) {
                    IAcceptDeal iAcceptDeal = new AcceptDealFile(client);
                    iAcceptDeal.init();
                    iAcceptDeal.start();
                }
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("socket closed")) {
                logger.info(e.getMessage());
            } else {
                logger.error(e.getMessage(), e);
            }
        } catch (Exception e) {
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

}
