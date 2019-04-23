package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.net.bean.NetBean;
import com.newland.bi.bigdata.net.impl.IClientDeal;
import com.newland.bi.bigdata.net.impl.IClientDealNetBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private IClientDeal<NetBean> iClientDeal;

    public DpiSocketClient(String server_ip, int server_port) {
        this.server_ip = server_ip;
        this.server_port = server_port;
        iClientDeal = new IClientDealNetBean();
    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 6795;
        String content = "8613900000000|Dalvik/2.1.0 (Linux; U; Android 6.0.1; vivo X9Plus Build/MMB29M)|/mmsns/BrVA8rJQ5YiaE3jsNCbIlvWuxZqwy7iceOyo4hmLlZcPhIQdGr41EoEmXPVFTzzeIr8xSa3mlvGjM/150?tp=wxpc&token=WSEN6qDsKwV8A02w3onOGQYfxnkibdqSOkmHhZGNB4DFBVuxiac0cLTBOPNlRfmSzxb4E8u5TPZzWfCdpkAiaX3Ug&idx=1|shmmsns.qpic.cn|117.172.5.42|80|1|9|1198.0|5086.0|image/wxpc";
        DpiSocketClient dpiSocketClient = new DpiSocketClient(ip, port);
        dpiSocketClient.connect();
        String recive = dpiSocketClient.sendMsg(content);
        System.out.println("recive：" + recive);
        dpiSocketClient.disconnect();
    }

    /**
     * 连接服务端
     */
    public void connect() {
        try {
            client = new Socket(server_ip, server_port);
            iClientDeal.newReader(client.getInputStream());
            iClientDeal.newWriter(client.getOutputStream());
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
            //封包
            NetBean netBean = NetUtils.buildString(data);
            //发送获取
            NetBean recive = iClientDeal.writeAndRead(netBean);
            //拆包
            Object content = NetUtils.getValue(recive);
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
        iClientDeal.close();
    }
}
