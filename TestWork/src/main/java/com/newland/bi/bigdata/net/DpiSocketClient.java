package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.net.bean.NetBean;
import com.newland.bi.bigdata.net.impl.IClientDeal;
import com.newland.bi.bigdata.net.impl.IClientDealFile;
import com.newland.bi.bigdata.net.impl.IClientDealNetBean;
import com.newland.bi.bigdata.net.impl.IClientDealString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * socket客户端
 *
 * @author chenqixu
 */
public class DpiSocketClient {

    private static Logger logger = LoggerFactory.getLogger(DpiSocketClient.class);
    private Socket client;
    private int server_port;
    private String server_ip;
//    private IClientDeal<NetBean> iClientDeal;
    private IClientDeal<File> iClientDeal;

    public DpiSocketClient(String server_ip, int server_port) {
        this.server_ip = server_ip;
        this.server_port = server_port;
//        iClientDeal = new IClientDealNetBean();
        iClientDeal = new IClientDealFile();
    }

    /**
     * 连接服务端
     */
    public void connect() {
        try {
            client = new Socket(server_ip, server_port);
            iClientDeal.initClient(client);
            logger.info("connect：{}", client);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            disconnect();
        }
    }

    /**
     * 校验
     */
    private void check() {
        if (client == null) throw new NullPointerException("client is null ! please connect first !");
    }

    /**
     * 发送、接收数据
     *
     * @param data send data
     */
    public String sendMsg(String data) {
        check();
        try {
//            // NetBean：
//            //封包
//            NetBean netBean = NetUtils.buildString(data);
//            //发送获取
//            NetBean recive = iClientDeal.writeAndRead(netBean);
//            //拆包
//            Object content = NetUtils.getValue(recive);
//            // String：
//            String content = iClientDeal.writeAndRead(data);
            // File：
            iClientDeal.writeSingle(new File(data));
            String content = "";
            logger.info("client：{}，send：{}，receive：{}", client, data, content);
            return (String) content;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 退出服务端
     */
    public void disconnect() {
        logger.info("disconnect，client：{}", client);
        iClientDeal.closeClient();
        closeSocket();
    }

    private void closeSocket() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
