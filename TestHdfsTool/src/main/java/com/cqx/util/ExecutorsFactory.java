package com.cqx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExecutorsFactory
 *
 * @author chenqixu
 */
public class ExecutorsFactory {

    // 日志类
    private static Logger logger = LoggerFactory.getLogger(ExecutorsFactory.class);
    // 封闭线程池
    private ExecutorService executor;

    private IExecutorsRun iExecutorsRun;
    private int parallel_num;

    public ExecutorsFactory(int parallel_num) {
        this.parallel_num = parallel_num;
        executor = Executors.newFixedThreadPool(parallel_num);
    }

    public void setiExecutorsRun(IExecutorsRun iExecutorsRun) {
        this.iExecutorsRun = iExecutorsRun;
    }

    private void check() {
        if (iExecutorsRun == null) throw new NullPointerException("iExecutorsRun is null ! Please check !");
    }

    /**
     * 启动Callable
     *
     * @throws InterruptedException
     */
    public Long startCallable() throws InterruptedException, ExecutionException {
        List<Callable<Long>> callableList = new ArrayList<>();
        List<Future<Long>> futureList = new ArrayList<>();
        Callable myCallable = new Callable() {
            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public Long call() throws Exception {
                int mod = atomicInteger.incrementAndGet();
                logger.info("{} run. mod is：{}", this, mod--);
                check();
                return iExecutorsRun.run();
            }
        };
        for (int i = 0; i < parallel_num; i++) {
//            callableList.add(myCallable);
            futureList.add(executor.submit(myCallable));
        }
        // 等待执行完成
//        executor.invokeAll(callableList);
        long result = 0;
        for (int i = 0; i < futureList.size(); i++) {
            // 会显示的抛check里的异常
            result += futureList.get(i).get();
        }
        return result;
    }
}
