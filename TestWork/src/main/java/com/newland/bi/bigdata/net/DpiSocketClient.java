package com.newland.bi.bigdata.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * socket客户端
 *
 * @author chenqixu
 */
public class DpiSocketClient {

    private static final String LANG = "utf-8";
    private static Logger logger = LoggerFactory.getLogger(DpiSocketClient.class);
    private Socket client;
    private int server_port;
    private String server_ip;
    private BufferedReader br = null;
    private PrintWriter pw = null;

    public DpiSocketClient(String server_ip, int server_port) {
        this.server_ip = server_ip;
        this.server_port = server_port;
    }

    /**
     * 连接服务端
     */
    public void connect() {
        try {
            client = new Socket(server_ip, server_port);
            br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    Charset.forName(LANG)));
            pw = new PrintWriter(client.getOutputStream(), true);
            logger.info("connect：{}", client);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            disconnect();
        }
    }

    /**
     * 校验
     */
    private void check() {
        if (client == null) throw new NullPointerException("client is null ! please connect first !");
        if (br == null) throw new NullPointerException("br is null ! please connect first !");
        if (pw == null) throw new NullPointerException("pw is null ! please connect first !");
    }

    /**
     * 发送、接收数据
     *
     * @param data send data
     */
    public String sendMsg(String data) {
        check();
        try {
            pw.println(data);
            String content = br.readLine();
            logger.info("client：{}，send：{}，receive：{}", client, data, content);
            return content;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 退出服务端
     */
    public void disconnect() {
        logger.info("disconnect，br：{}，pw：{}，client：{}", br, pw, client);
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
