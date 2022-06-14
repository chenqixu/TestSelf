package com.bussiness.bi.bigdata.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * socket服务端 System.out.println 输出版本
 *
 * @author chenqixu
 */
public class DpiSocketServerSOUT {

    private int port;
    private boolean status = false;
    private ServerSocket serverSocket;

    public DpiSocketServerSOUT(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            new DpiSocketServerSOUT(Integer.valueOf(args[0])).start();
//            new DpiSocketServerSOUT(Integer.valueOf(args[0])).startByTimeout(3000);
        } else {
            System.out.println("no enough args.");
        }
    }

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几豪秒
     */
    public static void sleepMilliSecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
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
            System.out.println("start server：" + serverSocket);
            while (status) {
                try {
                    // 接受客户端请求，阻塞状态
                    Socket client = serverSocket.accept();
                    if (client != null)
                        new DpiSocketServerSOUT.AcceptDeal(client).start();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("serverSocket isClosed：" + serverSocket.isClosed());
        }
    }

    /**
     * 使用close来关闭socket
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            status = true;
            System.out.println("start server：" + serverSocket);
            while (status) {
                // 接受客户端请求，阻塞状态
                Socket client = serverSocket.accept();
                if (client != null)
                    new DpiSocketServerSOUT.AcceptDeal(client).start();
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("socket closed")) {
                System.out.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("serverSocket isClosed：" + serverSocket.isClosed());
        }
    }

    /**
     * 设置accept的超时时间来停止socket
     */
    public void stopByTimeout() {
        System.out.println("stop server：" + serverSocket);
        status = false;
    }

    /**
     * 使用close来关闭socket
     */
    public void stop() {
        System.out.println("stop server：" + serverSocket);
        status = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端处理线程
     *
     * @author chenqixu
     */
    class AcceptDeal extends Thread {
        static final String LANG = "utf-8";
        private BufferedReader br = null;
        private Socket client;
        private PrintWriter pw = null;
        private boolean status;
        private TimeCostUtil timeCostUtil = null;
        private long time_out;//客户端超时时间

        public AcceptDeal(Socket client) {
            this(client, 1);//xx无输入就断开客户端连接
        }

        public AcceptDeal(Socket client, long time_out) {
            this.timeCostUtil = new TimeCostUtil();
            System.out.println(timeCostUtil.getNow("yyyy-MM-dd HH:mm:ss.SSS") + " " + this + " accetp client：" + client + "，time_out：" + time_out);
            this.client = client;
            this.time_out = time_out;
            try {
                br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                        Charset.forName(LANG)));
                pw = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            status = true;
        }

        @Override
        public void run() {
            String content;
//            TimeCostUtil run_timeCostUtil = new TimeCostUtil(true);
            try {
                timeCostUtil.start();
                while (status) {
//                    run_timeCostUtil.stopAndIncrementCost();
//                    if (run_timeCostUtil.getIncrementCost() >= time_out) break;
                    while ((content = br.readLine()) != null) {
//                        System.out.println("client：" + client + "，read content：" + content);
                        pw.println(content + "|replay1|replay2|replay3|replay4|replay5|replay6|replay7|replay8");
//                        pw.println(content + "");
                        status = false;
                    }
//                    sleepMilliSecond(50);
//                    run_timeCostUtil.start();
                }
                timeCostUtil.stop();
                System.out.println(String.format("%s client：%s，start：%s，end：%s，all cost：%s",
                        this, client, timeCostUtil.getStart(), timeCostUtil.getEnd(), timeCostUtil.getCost()));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (pw != null)
                    pw.close();
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class TimeCostUtil {
        long start;
        long end;
        long incrementCost = 0;
        boolean isNanoTime = false;
        long lastCheckTime = getCurrentTime();

        public TimeCostUtil() {
        }

        public TimeCostUtil(boolean isNanoTime) {
            this.isNanoTime = isNanoTime;
        }

        public String getNow(String format) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(now);
        }

        private long getCurrentTime() {
            if (isNanoTime) {
                return System.nanoTime();//纳秒
            } else {
                return System.currentTimeMillis();
            }
        }

        public void start() {
            start = getCurrentTime();
        }

        public void stop() {
            end = getCurrentTime();
        }

        public boolean tag(long limitTime) {
            if (getCurrentTime() - lastCheckTime > limitTime) {
                lastCheckTime = getCurrentTime();
                return true;
            }
            return false;
        }

        /**
         * 花费的时间
         *
         * @return
         */
        public long getCost() {
            if (start == 0) return 0;
            if (isNanoTime)
                return (end - start) / 1000000;
            else
                return end - start;
        }

        public long stopAndGet() {
            stop();
            return getCost();
        }

        public String getStart() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(start));
        }

        public String getEnd() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(end));
        }

        public void stopAndIncrementCost() {
            stop();
            incrementCost += getCost();
        }

        public long getIncrementCost() {
            if (isNanoTime)
                return incrementCost / 1000000;
            else
                return incrementCost;
        }

        public void resetIncrementCost() {
            incrementCost = 0;
        }
    }
}
