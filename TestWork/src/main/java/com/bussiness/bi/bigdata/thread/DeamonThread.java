package com.bussiness.bi.bigdata.thread;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import com.cqx.common.utils.thread.BaseCallableV1;
import com.cqx.common.utils.thread.ExecutorFactoryV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 守护进程测试验证<br>
 * <pre>
 *     验证步骤如下：
 *     根据文件大小判断是否开启守护进程
 * </pre>
 *
 * @author chenqixu
 */
public class DeamonThread {
    private static final Logger logger = LoggerFactory.getLogger(DeamonThread.class);
    private ExecutorFactoryV1 executorFactory;

    public DeamonThread(int parallNum) {
        this.executorFactory = ExecutorFactoryV1.newInstance(parallNum);
    }

    public static void main(String[] args) throws Exception {
        new DeamonThread(2).deamonTest(3);
    }

    public void deamonTest(int num) throws Exception {
        for (int i = 0; i < num; i++) {
            DeamonT1 dt1 = new DeamonT1();
            DeamonT2 dt2 = new DeamonT2(dt1);
            dt1.setDt2(dt2);

            if (dt1.getFileSize() > 5000) {
                logger.info("start...");
                executorFactory.submit(dt1);
                executorFactory.submit(dt2);
                // 状态打印
                executorFactory.printPoolStatus(logger);
                executorFactory.joinAndClean();
            } else {
                logger.info("not start...");
            }
        }
        executorFactory.stop();

        // 状态打印
        executorFactory.printPoolStatus(logger);
    }

    public void deamonTest1() throws Exception {
        DeamonSend ds = new DeamonSend(10000);
        executorFactory.submit(ds);
        executorFactory.joinAndClean();
        executorFactory.stop();
    }

    public void deamonTest2() throws Exception {
        DeamonSendV1 ds = new DeamonSendV1(5000);
        executorFactory.submit(ds);
        executorFactory.joinAndClean();
        executorFactory.stop();
    }

    public void deamonTest3() throws Exception {
        DeamonSendV2 ds = new DeamonSendV2(50);
        executorFactory.submit(ds);
        executorFactory.joinAndClean();
        executorFactory.stop();
    }

    public class DeamonT1 extends BaseCallableV1 {
        private long startT;
        private int fileSize;
        private DeamonT2 dt2;

        DeamonT1() {
            startT = System.currentTimeMillis();
            Random r = new Random(startT);
            fileSize = r.nextInt(15) * 1000;
            logger.info("init fileSize={}", fileSize);
        }

        @Override
        public void exec() throws Exception {
            if (fileSize-- > 0) {
                SleepUtil.sleepMilliSecond(1);
            } else {
                dt2.stop();
                stop();
            }
        }

        private void setDt2(DeamonT2 dt2) {
            this.dt2 = dt2;
        }

        private int getFileSize() {
            return fileSize;
        }

        private long getDiff() {
            return System.currentTimeMillis() - startT;
        }
    }

    public class DeamonT2 extends BaseCallableV1 {
        private DeamonT1 dt1;

        public DeamonT2(DeamonT1 dt1) {
            this.dt1 = dt1;
        }

        @Override
        public void exec() {
            SleepUtil.sleepMilliSecond(200);
            // 如果超过5秒，发告警
            long diff = dt1.getDiff();
            if (diff > 5000) {
                logger.warn("线程运行超过5秒！diff={}", diff);
            }
        }
    }

    public class DeamonSend extends BaseCallableV1 {
        private int packageSize;
        private Random r;
        private TimeCostUtil tc;
        private int monitorCnt = 0;
        private int circuitBreakerMonitorCnt = 0;
        private long sleep = 1L;
        private int rate;
        private TimeCostUtil circuitBreakerTC;

        DeamonSend(int packageSize) {
            tc = new TimeCostUtil();
            circuitBreakerTC = new TimeCostUtil();
            tc.start();
            r = new Random(System.currentTimeMillis());
            this.packageSize = packageSize;
            logger.info("init packageSize={}", packageSize);
        }

        @Override
        public void exec() throws Exception {
            if (packageSize-- > 0) {
                // 模拟偶发时延
                rate = r.nextInt(10);
                if (rate >= 8) {
                    if (circuitBreakerTC.tag(1000)) {
                        logger.warn("触发熔断！");
                        sleep = rate * 10;
                        circuitBreakerMonitorCnt++;
                    } else {
                        sleep = 1;
                        monitorCnt++;
                    }
                } else {
                    sleep = 1;
                }
                SleepUtil.sleepMilliSecond(sleep);
            } else {
                logger.info("处理完成, 总耗时={} ms, 熔断次数={}, 未熔断次数={}", tc.stopAndGet(), circuitBreakerMonitorCnt, monitorCnt);
                stop();
            }
        }
    }

    public class DeamonSendV1 extends BaseCallableV1 {
        private int packageSize;
        private TimeCostUtil tc;
        private TimeCostUtil tcAll;

        DeamonSendV1(int packageSize) {
            tc = new TimeCostUtil();
            tcAll = new TimeCostUtil();
            tcAll.start();
            this.packageSize = packageSize;
            logger.info("init packageSize={}", packageSize);
        }

        @Override
        public void exec() throws Exception {
            if (packageSize-- > 0) {
                tc.start();
                SleepUtil.sleepMilliSecond(1);
                long cost = tc.stopAndGet();
            } else {
                logger.info("处理完成, 总耗时={} ms", tcAll.stopAndGet());
                stop();
            }
        }
    }

    public class DeamonSendV2 extends BaseCallableV1 {
        private TimeCostUtil sendTC;
        // 数据队列
        private LinkedBlockingDeque<String> queue;
        private Random rd;

        DeamonSendV2(int packageSize) {
            rd = new Random(System.currentTimeMillis());
            sendTC = new TimeCostUtil();
            queue = new LinkedBlockingDeque<>();
            for (int i = 0; i < packageSize; i++) {
                queue.add("1");
            }
        }

        /**
         * 模拟获取连接，可能是空，可能不是空
         *
         * @return
         */
        private Object connect() {
            if (rd.nextInt(10) >= 8) {
                logger.info("连接失败。");
                return null;
            }
            logger.info("连接成功。");
            return new Object();
        }

        /**
         * 模拟发送数据，可能发送时间过长，也可能发送时产生异常
         *
         * @throws IOException
         */
        private void send() throws IOException {
            int _rd = rd.nextInt(100);
            if (_rd >= 90) {
                throw new IOException("发送数据发生了异常。");
            } else if (_rd >= 80) {
                SleepUtil.sleepMilliSecond(_rd + 1000);
            } else {
                SleepUtil.sleepMilliSecond(_rd);
            }
        }

        @Override
        public void exec() throws Exception {
            AtomicBoolean firstGetCon = new AtomicBoolean(false);
            String _tmp;
            Object sdtpClient = null;
            while ((_tmp = queue.poll()) != null) {
                // 如果连接为空，SdtpClientUtil.reconnect_TIME 秒后尝试重连，一直重试直到连接成功
                while (sdtpClient == null) {
                    // 第一次尝试连接不需要等待
                    if (firstGetCon.getAndSet(true)) {
                        logger.warn("正在尝试重连……");
                        SleepUtil.sleepMilliSecond(5 * 1000L);
                    }
                    // 模拟获取连接，可能是空，可能不是空
                    sdtpClient = connect();
                }
                try {
                    //===============================
                    // 统计发送数据的单次耗时
                    //===============================
                    sendTC.start();
                    // 模拟发送数据，可能发送时间过长，也可能发送时产生异常
                    send();
                    long sendCost = sendTC.stopAndGet();

                    // 单次发送耗时太长，需要重新连接服务器
                    if (sendCost >= 1000) {
                        logger.warn("[处理任务模块] 数据发送超时, 超时时间={}", sendCost);
                        sdtpClient = null;
                    }
                } catch (IOException ioe) {
                    // io异常，先释放，再重连
                    logger.error("io异常. ", ioe);
                    // 把未收到应答的数据塞回栈顶
                    queue.addFirst(_tmp);
                    sdtpClient = null;
                }
            }
            logger.info("发送完成。");
            stop();
        }
    }
}
