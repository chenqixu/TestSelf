package com.bussiness.bi.bigdata.net;

import com.bussiness.bi.bigdata.thread.ExecutorsFactory;
import com.bussiness.bi.bigdata.thread.IExecutorsRun;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DpiSocketClientTest {

    private static final char[] dataStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private String server_ip = "127.0.0.1";
    private int server_port = DpiSocketServerTest.SERVER_PORT;
    private ExecutorsFactory executorsFactory;
    private int parallel_num = 1;
    private Random random = new Random();
    private Map<Integer, DpiSocketClient> dpiSocketClientMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        executorsFactory = new ExecutorsFactory(parallel_num);
        for (int i = 0; i < parallel_num; i++) {
            DpiSocketClient dpiSocketClient = new DpiSocketClient(server_ip, server_port);
            dpiSocketClient.connect();
            dpiSocketClientMap.put(i, dpiSocketClient);
        }
        executorsFactory.setiExecutorsRun(new IExecutorsRun() {
            @Override
            public void run() throws Exception {
//                connectAndSend(getRandomStr(10));
//                for (int i = 0; i < 100; i++)
//                    connectAndSend(i % parallel_num, getRandomStr(10));
                connectAndSendFile();
            }
        });
    }

    @Test
    public void connect() throws Exception {
        executorsFactory.startCallable();
        close();
    }

    private void connectAndSend(String data) {
        DpiSocketClient dpiSocketClient = new DpiSocketClient(server_ip, server_port);
        dpiSocketClient.connect();
        dpiSocketClient.sendMsg(data);
        dpiSocketClient.disconnect();
    }

    private void connectAndSend(int mod, String data) {
        dpiSocketClientMap.get(mod).sendMsg(data);
    }

    private void connectAndSendFile() {
        dpiSocketClientMap.get(0).sendMsg("d:\\tmp\\data\\dpi\\dpi_gndata\\Uar_103_01_rtsp_session_60_20190411_081400_20190411_081459.csv");
    }

    private void close() {
        for (Map.Entry<Integer, DpiSocketClient> entry : dpiSocketClientMap.entrySet()) {
            entry.getValue().disconnect();
        }
    }

    /**
     * 根据seed获取随机字符串
     *
     * @param seed
     * @return
     */
    private String getRandomStr(int seed) {
        String result = "";
        for (int i = 0; i < seed; i++) {
            result += dataStr[random.nextInt(52)];
        }
        return result;
    }

    @Test
    public void StringValueOf() {
        String data = "123";
        String s = String.valueOf(NetUtils.buildString(data));
        System.out.println(s);

    }
}