package com.newland.bi.bigdata.net.udp;

import java.io.IOException;
import java.net.*;

/**
 * UDP测试
 *
 * @author chenqixu
 */
public class UDPClientAPI {
    private int port;
    private DatagramSocket client;
    private InetAddress addr;
//    private IUDPListener listener;
    private boolean isCycle = false;

    public UDPClientAPI(String desAddr, int desPort) throws IOException {
        client = new DatagramSocket();
        addr = InetAddress.getByName(desAddr);
        port = desPort;
    }

//    public UDPClientAPI(String desAddr, int desPort, IUDPListener listener) throws IOException {
//        client = new DatagramSocket();
//        addr = InetAddress.getByName(desAddr);
//        port = desPort;
//        this.listener = listener;
//    }
//
//    public void sendData(byte[] bytes) throws IOException {
//        if (addr == null || port == -1) {
//            throw new NullPointerException("请重新初始化");
//            //return;
//        }
//        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, addr, port);
//        client.send(sendPacket);
//    }
//
//    /**
//     * 简单的循环执行读取数据
//     *
//     * @throws IOException
//     */
//    public void simpleWaitingRead(boolean isRead) throws IOException, InterruptedException {
//        if (isRead) {
//            if (!isCycle) {//未进行循环执行循环读取
//                isCycle = true;//执行循环
//                CycleRead();//执行循环操作
//            }
//        } else {
//            if (isCycle) {//正在运行时执行停止循环
//                isCycle = false;//停止循环
//            }
//        }
//    }
//
//    /**
//     * 执行循环读取操作
//     *
//     * @throws IOException
//     */
//    public void CycleRead() throws InterruptedException {
//        Thread thread = new Thread() {
//            public void run() {
//                while (isCycle) {
//                    System.out.println("into the cycleRead");
//                    byte[] read_b = new byte[2048];//作为数据缓存区,读取数据最大长度无法超出2K
//                    DatagramPacket recvPacket = new DatagramPacket(read_b, read_b.length);//缓存区装进包中
//                    try {
//                        client.receive(recvPacket);//阻塞读取数据
//                        byte[] vaild_b = new byte[recvPacket.getLength()];//有效数据
//                        System.arraycopy(read_b, 0, vaild_b, 0, vaild_b.length);//取出有效数据
//                        listener.onReceiveData(vaild_b);//通过监听器回调有效数据
//                    } catch (IOException e) {
//                        if (!e.getMessage().equals("Socket closed")) {
//                            e.printStackTrace();
//                        }
//                    }//进行包读取,阻塞型
//                }
//            }
//        };
//        thread.start();
//        thread.join();
//    }
//
//    /**
//     * 关闭所有
//     */
//    public void CloseAll() {
//        isCycle = false;
//        port = -1;
//        if (client != null && !client.isClosed()) {
//            client.close();
//        }
//    }

    // 定义发送数据报的目的地
//    public static final int DEST_PORT = 500;
//    public static final String DEST_IP = "211.138.148.16";
    private static final int TIMEOUT = 3000;   // 设置超时为3秒
    // 定义每个数据报的最大大小为4KB
    private static final int DATA_LEN = 4096;
    // 定义接收网络数据的字节数组
    byte[] inBuff = new byte[DATA_LEN];
    // 以指定的字节数组创建准备接收数据的DatagramPacket对象
    private DatagramPacket inPacket = new DatagramPacket(inBuff , inBuff.length);
    // 定义一个用于发送的DatagramPacket对象
    private DatagramPacket outPacket = null;

    public void simple(String data) throws IOException {
        // 创建一个客户端DatagramSocket，使用随机端口
        System.out.println("new socket, use random port.");
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT); // 设置阻塞时间
        // 初始化发送用的DatagramSocket，它包含一个长度为0的字节数组
        outPacket = new DatagramPacket(new byte[0] , 0, addr , port);
        // 串转换成字节数组
        byte[] buff = data.getBytes();
        // 设置发送用的DatagramPacket中的字节数据
        outPacket.setData(buff);
        // 发送数据报
        System.out.println("send outPacket : "+ outPacket);
        socket.send(outPacket);
        // 读取Socket中的数据，读到的数据放在inPacket所封装的字节数组中
        System.out.println("receive inPacket : "+ inPacket);
        socket.receive(inPacket);
        System.out.println(new String(inBuff , 0, inPacket.getLength()));
        System.out.println("socket close.");
        socket.close();
    }

    public static InetAddress getLocalAddress(String ip, int ISAKMP_PORT) {
        if (ip == null)
            return null;
        try {
            DatagramSocket sock = new DatagramSocket();
            InetAddress dstAddr = InetAddress.getByName(ip);
            sock.connect(dstAddr, ISAKMP_PORT);
            InetAddress srcAddr = sock.getLocalAddress();
            sock.close();
            System.out.println("srcAddr : " + srcAddr);
            return srcAddr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            String ip = args[0];
            int port = Integer.valueOf(args[1]);
            System.out.println("[IP] : " + ip + " , [port] : " + port);
//            UDPClientAPI udpClientAPI = new UDPClientAPI(IP, port);
//            udpClientAPI.simple("{\"user\":\"common\"}");
            getLocalAddress(ip, port);
        }else{
            System.out.println("please input param !");
        }
    }
}
//javac UDPClientAPI.java
//java -cp `pwd` UDPClientAPI 211.138.148.16 500