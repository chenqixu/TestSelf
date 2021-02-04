package com.cqx.work.jdk8.model;

import com.cqx.common.utils.thread.BaseRunableThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * BoltBeanProducer
 *
 * @author chenqixu
 */
public class BoltBeanProducer extends BaseRunableThread {
    private static final Logger logger = LoggerFactory.getLogger(BoltBeanProducer.class);
    private LinkedBlockingQueue<BoltBean> data;
    private Random random;
    private long cnt;

    public BoltBeanProducer(LinkedBlockingQueue<BoltBean> data) {
        this.data = data;
        this.random = new Random();
    }

    @Override
    protected void runnableExec() throws Exception {
        boolean result = false;
        BoltBean boltBean = new BoltBean(random.nextInt(1000) + "");
        //===================================
//        data.put(boltBean);//waiting queue idle
//        result = true;
        //===================================
//        try {
//            result = data.add(boltBean);//throw java.lang.IllegalStateException: Queue full
//            //实际上是调用offer，不成功就throw异常
//        } catch (IllegalStateException e) {
//            SleepUtil.sleepSecond(1);
//        }
        //===================================
        result = data.offer(boltBean, 1, TimeUnit.SECONDS);//不会waiting，会抛数据
        //===================================
        if (!result) {
            logger.warn("正在抛数据 {}", boltBean);
        } else {
            logger.debug("queue.put {}", boltBean);
            cnt++;
        }
    }

    @Override
    protected void afterStop() {
        logger.info("producer {}", cnt);
    }
}
