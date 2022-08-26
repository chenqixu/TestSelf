package com.cqx.common.utils.net;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class SocketServerTest {
    private static final Logger logger = LoggerFactory.getLogger(SocketServerTest.class);

    @Test
    public void server() throws IOException, InterruptedException {
        try (SocketServer socketServer = SocketServer.newbuilder()
                .setPort(8111)
                .setLang("GBK")
                .build()) {
            socketServer.accept(15000);
        }
    }

    @Test
    public void client() throws IOException {
        try (SocketClient socketClient = SocketClient.newbuilder()
                .setIp("127.0.0.1")
                .setPort(8111)
                .build()) {
            ClientReceive clientReceive = new ClientReceive();
            // 发送的是GBK
            socketClient.send("你好\n".getBytes("GBK"));
            // 发送的是默认编码
//            socketClient.send("你好\n".getBytes());
            socketClient.receive(clientReceive);
        }
    }

    class ClientReceive implements SocketClient.ReceiveCall {

        @Override
        public void read(InputStream in) throws IOException {
            int size = 0;
            while (size == 0) {
                size = in.available();
                if (size > 0) {
                    byte[] result = new byte[size];
                    int readLen = in.read(result);
                    // 读到字节数组，可能已经有一定的编码方式了，这里我们需要按需要的编码进行解码
                    logger.info("{}, readLen: {}", new String(result, "GBK"), readLen);
                }
                SleepUtil.sleepMilliSecond(10);
            }
        }
    }
}