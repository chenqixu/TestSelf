package com.cqx.jk;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * FileDeal
 *
 * @author chenqixu
 */
public class FileDeal {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("you need input file_name.");
            System.exit(-1);
        }
        new FileDeal().deal(args[0]);
    }

    private void bussniess(String tmp) {
        String[] array = tmp.split(",", -1);
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
    }

    private void deal(String file_name) {
        BufferedReader reader = null;
        long start_time = System.currentTimeMillis();
        // 构造文件类
        File readFile = new File(file_name);
        // 构造工具类
        Tools tools = new Tools();
        tools.createQueue(5000);
        // 启动处理线程
        tools.newFileThread();
        tools.newFileThread();
        tools.newFileThread();
//        tools.newFileThread(5000);
//        tools.newFileThread(5000);
//        tools.newFileThread(5000);
        // putQueue监控
        PonitMonitor ponitMonitor = new PonitMonitor("putQueue", 200);
        try {
            // 构造读取文件流
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(readFile)));
            // 按行读取
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                tools.getQueue().put(tmp);
                ponitMonitor.addPonit(1);
            }
            long read_end_time = System.currentTimeMillis();
            System.out.println("read cost：" + (read_end_time - start_time));
            // 启动监控
            tools.startMonitor();
            // 等待消费线程处理完成
            tools.join();
            // 计算完成时间
            long end_time = System.currentTimeMillis();
            System.out.println("write cost：" + (end_time - start_time));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    enum DealStrategy {
        OneToOne,
        OneToMore,
        ;
    }

    class Tools {

        List<Thread> threads = new ArrayList<>();
        List<FileThread> fileThreads = new ArrayList<>();
        List<MonitorQueue> monitorQueues = new ArrayList<>();
        int index = 0;
        DealStrategy dealStrategy;
        BlockingQueue<String> queue;
        int strategy = 0;

        void createQueue(int queue_size) {
            queue = new LinkedBlockingQueue<>(queue_size);
        }

        /**
         * 一对一
         *
         * @param queue_size
         */
        void newFileThread(int queue_size) {
            dealStrategy = DealStrategy.OneToOne;
            index++;
            BlockingQueue<String> queue = new LinkedBlockingQueue<>(queue_size);
            FileThread fileThread = new FileThread(queue, index);
            Thread thread = new Thread(fileThread);
            thread.start();
            threads.add(thread);
            fileThreads.add(fileThread);
            // 监控
            MonitorQueue monitorQueue = new MonitorQueue(index, queue, fileThread);
            monitorQueues.add(monitorQueue);
        }

        /**
         * 一对多
         */
        void newFileThread() {
            if (queue == null) throw new NullPointerException("队列未创建");
            dealStrategy = DealStrategy.OneToMore;
            index++;
            FileThread fileThread = new FileThread(queue, index);
            Thread thread = new Thread(fileThread);
            thread.start();
            threads.add(thread);
            fileThreads.add(fileThread);
            // 监控
            MonitorQueue monitorQueue = new MonitorQueue(index, queue, fileThread);
            monitorQueues.add(monitorQueue);
        }

        void startMonitor() {
            for (MonitorQueue monitorQueue : monitorQueues) {
                monitorQueue.start();
            }
        }

        void join() {
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        BlockingQueue<String> getQueue() {
            BlockingQueue<String> result = null;
            switch (dealStrategy) {
                case OneToOne:
                    result = fileThreads.get(strategy).getQueue();
                    strategy++;
                    if (strategy >= fileThreads.size()) strategy = 0;
                    break;
                case OneToMore:
                    if (fileThreads.size() > 0) {
                        result = fileThreads.get(0).getQueue();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }
    }

    class FileThread implements Runnable {

        BlockingQueue<String> queue;
        volatile boolean flag = true;
        int index;

        FileThread(BlockingQueue<String> queue, int index) {
            this.queue = queue;
            this.index = index;
        }

        void stop() {
            System.out.println(String.format("停止FileThread %S……", index));
            this.flag = false;
        }

        @Override
        public void run() {
            System.out.println(String.format("启动FileThread %S……", index));
            // pollQueue监控
            PonitMonitor ponitMonitor = new PonitMonitor("pollQueue " + index, 200);
            while (flag) {
                String tmp;
                while ((tmp = queue.poll()) != null) {
                    String[] array = tmp.split(",", -1);
                    StringBuilder sb = new StringBuilder();
                    for (String s : array) {
                        sb.append(s);
                    }
                    ponitMonitor.addPonit(1);
                }
            }
        }

        public BlockingQueue<String> getQueue() {
            return queue;
        }
    }

    class MonitorQueue extends Thread {
        BlockingQueue<String> queue;
        FileThread fileThread;
        int index;
        volatile boolean flag = true;

        MonitorQueue(int index, BlockingQueue<String> queue, FileThread fileThread) {
            this.index = index;
            this.queue = queue;
            this.fileThread = fileThread;
        }

        public void run() {
            System.out.println(String.format("启动MonitorQueue %S……", index));
            while (flag) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (queue.size() == 0) {
                    System.out.println("处理结束");
                    flag = false;
                    fileThread.stop();
                }
            }
        }
    }
}
