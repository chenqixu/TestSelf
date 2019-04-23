package com.cqx.pool.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MyExecutor
 *
 * @author chenqixu
 */
public class MyExecutor {

    private static Logger logger = LoggerFactory.getLogger(MyExecutor.class);
    // 线程池
    private ExecutorService executor;
    // 采集线程并发
    private int parallel_num;
    // 随机
    private Random random = new Random();
    // 时间消耗工具
    private TimeCostUtil timeCostUtil;
    // 基于链接节点的可选有界阻塞队列，内容
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    // 心跳Map
    private HeartUtil heartUtil = new HeartUtil();

    public MyExecutor(int parallel_num) {
        this.parallel_num = parallel_num;
        this.executor = Executors.newFixedThreadPool(parallel_num);
//        new ThreadPoolMonitor(this.executor).startPoolMonitor();// 线程池监控
//        new ThreadPoolMonitor(this.heartUtil).startHeartMonitor();// 心跳监控
    }

    /**
     * 初始化，内容
     *
     * @param bound
     */
    public void init(int bound) {
        for (int i = 0; i < bound; i++) {
            try {
                queue.put("" + i);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 根据seed获取随机整形
     *
     * @param seed
     * @return
     */
    private int getRandomNextInt(int seed) {
        return random.nextInt(seed);
    }

    /**
     * 启动Runnable
     */
    public void startRunnable() {
        Runnable myRunable = new Runnable() {
            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public void run() {
                int mod = heartUtil.getHeartMod();
                int count = 0;
                logger.info("{} run. start is：{}，heartMod：{}", this, count, mod);
                TimeCostUtil timeCostUtil = new TimeCostUtil();
                timeCostUtil.start();
                while (queue.poll() != null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    count++;
                    if (timeCostUtil.tag(300))
                        // 每300毫秒发送一次心跳
                        heartUtil.heart(mod);
                }
                timeCostUtil.end();
                logger.info("dealcount is：{}，cost：{}", count, timeCostUtil.getCost());
            }
        };
        for (int i = 0; i < parallel_num; i++) {
            executor.execute(myRunable);
            heartUtil.init(i);
        }
    }

    /**
     * 启动Callable
     *
     * @throws InterruptedException
     */
    public void startCallable() throws InterruptedException {
        List<Callable<Long>> callableList = new ArrayList<>();
        Callable myCallable = new Callable() {
            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public Long call() throws Exception {
                int mod = heartUtil.getHeartMod();
                int count = 0;
                logger.info("{} run. start is：{}，heartMod：{}", this, count, mod);
                TimeCostUtil timeCostUtil = new TimeCostUtil();
                timeCostUtil.start();
                while (queue.poll() != null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    count++;
                    if (timeCostUtil.tag(300))
                        // 每300毫秒发送一次心跳
                        heartUtil.heart(mod);
                }
                timeCostUtil.end();
                logger.info("dealcount is：{}，cost：{}", count, timeCostUtil.getCost());
                return timeCostUtil.getCost();
            }
        };
        for (int i = 0; i < parallel_num; i++) {
            callableList.add(myCallable);
            heartUtil.init(i);
        }
        // 等待执行完成
        executor.invokeAll(callableList);
    }

    public void startDeal() {
        timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
    }

    public long endDeal() {
        timeCostUtil.end();
        return timeCostUtil.getCost();
    }

    public boolean timeCostTag(long limitTime) {
        return timeCostUtil.tag(limitTime);
    }

    public ICostUtil buildTimeCostUtil() {
        return new TimeCostUtil();
    }

    /**
     * 时间花费计算
     *
     * @author chenqixu
     */
    class TimeCostUtil implements ICostUtil {
        long start;
        long end;
        long lastCheckTime = System.currentTimeMillis();

        public void start() {
            start = System.currentTimeMillis();
        }

        public void end() {
            end = System.currentTimeMillis();
        }

        public boolean tag(long limitTime) {
            if (System.currentTimeMillis() - lastCheckTime > limitTime) {
                lastCheckTime = System.currentTimeMillis();
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
            return end - start;
        }
    }
}
