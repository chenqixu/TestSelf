package com.bussiness.bi.bigdata.net;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * SocketToNettyClient
 *
 * @author chenqixu
 */
public class SocketToNettyClient {

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 8007;
        String charSetType = "UTF-8";
        String reqData = "1234567890";

        OutputStream out = null; // 写
        InputStream in = null; // 读
        String respData = ""; // 响应报文
        Socket socket = new Socket(); // 客户机
        ByteArrayOutputStream bytesOut = null;
        try {
            socket.setTcpNoDelay(true);//关闭socket的缓冲,将数据立即发送出去
            socket.setReuseAddress(true);
            socket.setSoLinger(true, 0);
            socket.setSendBufferSize(32 * 1024);//发送缓存区
            socket.setReceiveBufferSize(32 * 1024);//接收缓存区
            socket.setKeepAlive(true);//长连接
            socket.connect(new InetSocketAddress(ip, port), 5000);

            for (int i = 0; i < 10; i++) {
                /**
                 * 发送TCP请求
                 */
                out = socket.getOutputStream();
                String requestData = setLength(reqData, charSetType);
                System.out.println("请求数据：" + requestData);
                out.write(requestData.getBytes(charSetType));
                out.flush();

                /**
                 * 接收TCP响应
                 */
                socket.setSoTimeout(30000);//socket调用InputStream读数据的超时时间，以毫秒为单位
                in = socket.getInputStream();
                //1、读取数据长度
                byte[] lenBytes = new byte[4];
                int readlen = in.read(lenBytes);
                System.out.println("读取到的长度：" + readlen);
            }

//            Integer length = Integer.valueOf(new String(lenBytes));
//            System.out.println("返回数据长度：" + length);

//            //2、读取报文数据
//            byte[] data = new byte[1024 * 4];
//            int len = 0;
//            bytesOut = new ByteArrayOutputStream();
//            Integer dataLen = 0;
//            while ((len = in.read(data)) != -1) {
//                bytesOut.write(data, 0, len);
//                dataLen = bytesOut.toByteArray().length;
//
//                if (length.equals(dataLen)) {
//                    break;
//                }
//            }
//            System.out.println("返回数据：" + bytesOut.toString(charSetType));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String setLength(String str, String charSetType) throws UnsupportedEncodingException {
        // 完整报文：10位长度 + 报文内容
        return String.format("%010d", str.getBytes(charSetType).length) + str;
    }
}
