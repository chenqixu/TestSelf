package com.cqx.common.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * SocketClient
 *
 * @author chenqixu
 */
public class SocketClient implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private Socket socket;
    private String ip;
    private int port;
    private OutputStream out;
    private InputStream in;

    private SocketClient() {
    }

    public static SocketClient newbuilder() {
        return new SocketClient();
    }

    public SocketClient setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public SocketClient setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketClient build() throws IOException {
        socket = new Socket();// 客户机
        socket.setTcpNoDelay(true);// 关闭socket的缓冲,将数据立即发送出去
        socket.setReuseAddress(true);
        socket.setSoLinger(true, 0);
        socket.setSendBufferSize(32 * 1024);// 发送缓存区
        socket.setReceiveBufferSize(32 * 1024);// 接收缓存区
        socket.setKeepAlive(true);//长连接
        socket.connect(new InetSocketAddress(ip, port), 30000);
        socket.setSoTimeout(30000);// socket调用InputStream读数据的超时时间，以毫秒为单位
        out = socket.getOutputStream();
        in = socket.getInputStream();
        return this;
    }

    /**
     * 发送数据到服务器
     *
     * @param bytes
     * @throws IOException
     */
    public void send(byte[] bytes) throws IOException {
        if (socket != null && out != null && in != null) {
            out.write(bytes);
            out.flush();
        } else {
            throw new NullPointerException("客户端未初始化，请检查！");
        }
    }

    /**
     * 接收服务器响应
     *
     * @throws IOException
     */
    public void receive(ReceiveCall receiveCall) throws IOException {
        receiveCall.read(in);
    }

    /**
     * 资源释放
     */
    @Override
    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void finalize() {
        close();
    }

    public interface ReceiveCall {
        void read(InputStream in) throws IOException;
    }
}
