package com.cqx.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * BusThread
 *
 * @author chenqixu
 */
public class BusThread extends Thread {

    private String id;

    public BusThread(String id) {
        this.id = id;
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(BusThread.class);
    }

    public void run() {
        LogInfoThread.init(getLogger(), id);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int sleep = random.nextInt(1000);
            LogInfoThread.info("随机休眠：" + sleep);
            SleepUtil.sleepMilliSecond(sleep);
        }
    }

    public void println() {
        System.out.println("BusThread========" + Thread.currentThread().getName());
    }
}
