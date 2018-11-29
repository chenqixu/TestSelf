package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 有返回值线程测试
 *
 * @author chenqixu
 * @date 2018/11/29 9:56
 */
public class MyFutureTask {

    private static Logger logger = LoggerFactory.getLogger(MyFutureTask.class);
    public final static int CONNECT_TIME_WAIT = 3 * 1000;
    // 使用一个独立的线程池，异步构建ftp连接。否则ftp连接超时可能会导致任务阻塞几分钟
    final static ExecutorService es = Executors.newFixedThreadPool(3);

    public void futureTest() {
        MyCallable callable = new MyCallable(CONNECT_TIME_WAIT);
        Future<Boolean> ret = es.submit(callable);
        boolean done = false;
        try {
//            done = ret.get(CONNECT_TIME_WAIT, TimeUnit.MILLISECONDS);
//            done = ret.get(6 * 1000, TimeUnit.MILLISECONDS);
            done = ret.get();
            logger.info("done：{}", done);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (!done) {
                ret.cancel(true);
            }
        }
    }

    public static void main(String[] args) {
        new MyFutureTask().futureTest();
    }
}
