package com.bussiness.bi.bigdata.thread;

import com.bussiness.bi.bigdata.bean.MyCountDownBean;
import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MyCountDown
 *
 * @author chenqixu
 */
public class MyCountDown extends IExecutorsRun<MyCountDownBean> {

    private static Logger logger = LoggerFactory.getLogger(MyCountDown.class);
    private Random random = new Random();
    private ExecutorsFactory<MyCountDownBean> executorsStep2;

    public MyCountDown() {
        executorsStep2 = new ExecutorsFactory<>(5);
        executorsStep2.setiExecutorsRun(new MyCountDownStep2());
    }

    public void startTimeOut(ExecutorsFactory executorsFactory) {
        new TimeOut(executorsFactory).start();
    }

    public void step(String stepName, long sleepMilliSecond) {
        step(1, stepName, sleepMilliSecond);
    }

    public void step(int step, String stepName, long sleepMilliSecond) {
        logger.info("step{} {}", step, stepName);
        SleepUtils.sleepMilliSecond(sleepMilliSecond);
    }

    public void step(int step, String stepName) {
        step(step, stepName, 500, true);
    }

    public void step(String stepName) {
        step(stepName, 500, true);
    }

    public void step(int step, String stepName, int seed, boolean isRandom) {
        if (isRandom) step(step, stepName, getRandomSleep(seed));
        else step(step, stepName, seed);
    }

    public void step(String stepName, int seed, boolean isRandom) {
        if (isRandom) step(stepName, getRandomSleep(seed));
        else step(stepName, seed);
    }

    public boolean getCompare() {
        return false;
//        return random.nextBoolean();
    }

    public long getRandomSleep(int seed) {
        return random.nextInt(seed);
    }

    private void createLoop(String step, int length) {
        try {
            for (int i = 0; i < length; i++) {
//            if (Thread.currentThread().isInterrupted()) break;
                isInterrupted();
                if (i % 9 == 0)
                    logger.info("{} {} {}", step, i, Thread.currentThread().isInterrupted());
            }
        } catch (InterruptedException e) {
            logger.error("任务收到InterruptedException");
        }
    }

    public void timingCheck() throws InterruptedException {
        timingCheck(MyCountDownBean
                .newbuilder()
                .setMergerPath(new Path("/test"))
                .setLocalBackUpPath("/tmp"));
    }

    public void timingCheck(MyCountDownBean myCountDownBean) throws InterruptedException {
        Path mergerPath = myCountDownBean.getMergerPath();
        String localBackUpPath = myCountDownBean.getLocalBackUpPath();
        String allFileName = mergerPath.toString();
        String mergerFileName = mergerPath.getName();
//        step("获取HDFS文件大小，" + allFileName);
//        step("获取缓存汇总大小，key：" + mergerFileName);
//        step("删除本地缓存临时文件", 50);
//        step("删除hdfs临时校验文件", 50);
//        step("进行比较", 50);
//        if (!getCompare()) {
//            step("数据不一致", 50);
//            step("从缓存中获取备份文件清单");
//            step("mergeFile，" + localBackUpPath);
//            step("上传HDFS，put " + localBackUpPath + " " + allFileName + ".check");
//            step("再次校验");
//            step("如果一致", 50);
//            executorsStep2.submitCallable(myCountDownBean);
        if (localBackUpPath.equals("/temp5")) {
            createLoop("step1", 100);
        } else {
            createLoop("step1", 10000);
        }
        executorsStep2.addCallable(myCountDownBean);
//            step("提交Step2 success.", 1);
//        } else {
//            step("数据一致");
//        }
    }

    public void printStep2Count() {
        step("printStep2Count：" + executorsStep2.getCompletedTaskCount(), 1);
    }

    public void printlnStep2Status() {
        executorsStep2.printlnStatus();
    }

    public void shutdownStep2PoolSubmit() {
        logger.info("shutdownStep2PoolSubmit……");
        executorsStep2.close();
    }

    public void awaitStep2() throws InterruptedException {
        logger.info("awaitStep2……");
        logger.info("executorsStep2 startCallableQueue……");
        executorsStep2.startCallableQueue();
        logger.info("executorsStep2 awaitFutureListPool……");
        executorsStep2.awaitFutureListPool();
    }

    @Override
    public void run(MyCountDownBean myCountDownBean) throws Exception {
        timingCheck(myCountDownBean);
    }

    class MyCountDownStep2 extends IExecutorsRun<MyCountDownBean> {
        private AtomicInteger atomicInteger = new AtomicInteger();

        public void timingCheck(MyCountDownBean myCountDownBean) {
            Path mergerPath = myCountDownBean.getMergerPath();
            String localBackUpPath = myCountDownBean.getLocalBackUpPath();
            String allFileName = mergerPath.toString();
            String mergerFileName = mergerPath.getName();
//            step(2, "移动原有文件变成.delete，mv " + allFileName + " " + allFileName + ".delete");
//            step(2, "移动校验后上传的文件变成原文件，mv " + allFileName + ".check " + allFileName);
//            step(2, "删除.delete文件，rm " + allFileName + ".delete");
//            step(2, "完成Step2：" + atomicInteger.incrementAndGet(), 1);
            createLoop("step2", 1000);
        }

        @Override
        public void run(MyCountDownBean myCountDownBean) throws Exception {
            timingCheck(myCountDownBean);
        }
    }

    class TimeOut extends Thread {
        private ExecutorsFactory executorsFactory;

        public TimeOut(ExecutorsFactory executorsFactory) {
            this.executorsFactory = executorsFactory;
        }

        public void run() {
            logger.info("[TimeOut] start TimeOut……");
            int count = 0;
            long timeout = 30;
            while (count <= 3) {
                count++;
//                // step1
//                executorsFactory.printlnStatus();
//                // step2
//                printlnStep2Status();
                logger.info("[TimeOut] sleep {} ms……", timeout);
                SleepUtils.sleepMilliSecond(timeout);
            }
//            // step1
//            executorsFactory.printlnStatus();
//            // step2
//            printlnStep2Status();
//            //先停止往step2提交
//            shutdownStep2PoolSubmit();
            //取消step1
            executorsFactory.cancelFutureListPool();
//            logger.info("[TimeOut] cancelFutureListPool step1");
//            //等待step2完成
//            awaitStep2();
//            //最后打印以及退出
//            // step1
//            executorsFactory.printlnStatus();
//            // step2
//            printlnStep2Status();
//            printStep2Count();
            logger.info("[TimeOut] exit……");
//            System.exit(-1);
        }
    }
}
