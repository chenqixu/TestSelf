package com.cqx.common.utils.net;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

/**
 * SocketServer
 *
 * @author chenqixu
 */
public class SocketServer implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private int port;
    private int soTimeout = 100;
    private String lang = Charset.defaultCharset().name();
    private ServerSocket serverSocket;

    private SocketServer() {
    }

    public static SocketServer newbuilder() {
        return new SocketServer();
    }

    public SocketServer setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketServer setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    public SocketServer setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public SocketServer build() throws IOException {
        serverSocket = new ServerSocket(port);// 服务端
        // 设置accept超时时间，否则退出调用stop的时候还需要发一次消息才会正常退出
        serverSocket.setSoTimeout(soTimeout);
        return this;
    }

    public void accept(int acceptTimeOut) throws InterruptedException {
        TimeCostUtil tc = new TimeCostUtil();
        AcceptThread acceptThread = new AcceptThread();
        Thread thread = new Thread(acceptThread);
        thread.start();
        while (!tc.tag(acceptTimeOut)) {
            SleepUtil.sleepMilliSecond(1);
        }
        acceptThread.stop();
        thread.join();
    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null) serverSocket.close();
    }

    class AcceptThread extends BaseRunable {

        @Override
        public void exec() throws Exception {
            // 接受客户端请求，阻塞状态
            try {
                Socket client = serverSocket.accept();
                if (client != null) {
                    logger.info("接收到客户端：{}", client);
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            client.getInputStream(), Charset.forName(lang)));
                         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                 client.getOutputStream(), Charset.forName(lang)))
                    ) {
                        String tmp;
                        while ((tmp = br.readLine()) != null) {
                            logger.info("read: {}", tmp);
                            // 这里写的是java unicode，真正确认回写客户端编码的是OutputStreamWriter
                            bw.write("谢谢");
                            break;
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (SocketTimeoutException ste) {
                // throw
            }
        }
    }
}
