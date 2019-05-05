package com.cqx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * ThreadTools
 *
 * @author chenqixu
 */
public class ThreadTools extends Thread {

    private static Logger logger = LoggerFactory.getLogger(ThreadTools.class);
    private HdfsToolFactory hdfsToolFactory;

    public ThreadTools(HdfsToolFactory hdfsToolFactory) {
        this.hdfsToolFactory = hdfsToolFactory;
    }

    public static void delayTask(final Thread t, final int delay) {
        Runnable cancellation = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    t.interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(cancellation).start();
    }


    public static void cancelTask(final Future<?> future, final int delay) {
        Runnable cancellation = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    future.cancel(true);// 取消与 future 关联的正在运行的任务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(cancellation).start();
    }

    public void run() {
        try {
            createLoop("step1", 100000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFile() throws IOException {
        String hdfsDst = "/tmp/test/dpi/a.txt";
        String localDst = "file:///d:/tmp/data/dpi/a.txt";
        hdfsToolFactory.copyFromLocalFile(localDst, hdfsDst);
    }

    private void createLoop(String step, int length) {
        for (int i = 0; i < length; i++) {
            if (i % 9 == 0)
                logger.info("{} {} {}", step, i, Thread.currentThread().isInterrupted());
        }
    }
}
