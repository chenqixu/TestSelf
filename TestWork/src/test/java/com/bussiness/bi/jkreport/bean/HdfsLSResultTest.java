package com.bussiness.bi.jkreport.bean;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class HdfsLSResultTest {

    private static final Logger logger = LoggerFactory.getLogger(HdfsLSResultTest.class);
    private HdfsLSResult hdfsLSResult;
    private Map<String, Long> count = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        hdfsLSResult = new HdfsLSResult();
        hdfsLSResult.setFilterKey(".ok");
        hdfsLSResult.setExclusionKey(".complete");
    }

    private void scan() {
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "/nat/202006170000/00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "/nat/202006170000/202006170000.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170000", "/nat/202006170000/202006170000.complete"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "/nat/202006170015/00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "/nat/202006170015/01"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "/nat/202006170015/02"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170015", "/nat/202006170015/202006170015.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "/nat/202006170030/00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "/nat/202006170030/01"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170030", "/nat/202006170030/202006170030.ok"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170045", "/nat/202006170045/00"));
        hdfsLSResult.addSource(new HdfsLSBean("nat", "202006170045", "/nat/202006170045/01"));
        hdfsLSResult.addSource(new HdfsLSBean("notnat", "202006170000", "/notnat/202006170000/00"));
    }

    @Test
    public void exclusion() throws InterruptedException {
        scan();//扫描
        hdfsLSResult.sourceToType();
        hdfsLSResult.typeToDate();
        hdfsLSResult.exclusion();
        hdfsLSResult.addCheck();
        for (Map.Entry<String, Map<String, List<HdfsLSBean>>> type : hdfsLSResult.getTypeDateMap().entrySet()) {
            logger.info("type：{}，cycle_size：{}", type.getKey(), type.getValue().size());
            for (Map.Entry<String, List<HdfsLSBean>> cycle : type.getValue().entrySet()) {
                logger.info("cycle：{}，file_size：{}", cycle.getKey(), cycle.getValue().size());
                for (HdfsLSBean hdfsLSBean : cycle.getValue()) {
                    logger.info("content：{}，check：{}", hdfsLSBean.getContent(), hdfsLSBean.getHdfsLSCheck());
                }
            }
        }
        BlockingQueue<HdfsLSResult.FastFailureTask> scanQueue = new LinkedBlockingQueue<>();
        String dataForamt = "yyyyMMddHHmmss";
        hdfsLSResult.addQueue(scanQueue, dataForamt);
        run(scanQueue);
    }

    private void run(BlockingQueue<HdfsLSResult.FastFailureTask> scanQueue) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        HdfsLSResult.FastFailureTask failureTask;
        while ((failureTask = scanQueue.poll()) != null) {
            final HdfsLSResult.FastFailureTask finalFailureTask = failureTask;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HdfsLSBean hdfsLSBean = (HdfsLSBean) finalFailureTask;
                    String taskName = hdfsLSBean.getTaskName();
                    String date = hdfsLSBean.getDate();
                    String type = hdfsLSBean.getType();
                    Random random = new Random();
                    int r = random.nextInt(500);
                    logger.info("taskName：{}，date：{}，type：{}，sleep {}.", taskName, date, type, r);
                    SleepUtil.sleepMilliSecond(r);
                    hdfsLSBean.check();
                }
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void listTest() {
        List<Map<String, List<String>>> listall = new ArrayList<>();
        Map<String, List<String>> m1 = new HashMap<>();
        List<String> l1 = new ArrayList<>();
        l1.add("nat-2020070100");
        l1.add("nat-2020070115");
        m1.put("20200701", l1);
        List<String> l2 = new ArrayList<>();
        l2.add("nat-2020070200");
        l2.add("nat-2020070215");
        m1.put("20200702", l2);
        listall.add(m1);
        Map<String, List<String>> m2 = new HashMap<>();
        List<String> l3 = new ArrayList<>();
        l3.add("nonat-2020070100");
        l3.add("nonat-2020070115");
        m2.put("20200701", l3);
        listall.add(m2);

        Map<String, List<String>> mall = new HashMap<>();
        for (Map<String, List<String>> m : listall) {
            for (Map.Entry<String, List<String>> entry : m.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                List<String> mall_get = mall.get(key);
                if (mall_get != null) {
                    mall_get.addAll(value);
                } else {
                    mall.put(key, value);
                }
            }
        }

        for (Map.Entry<String, List<String>> entry : mall.entrySet()) {
            logger.info("key：{}，size：{}", entry.getKey(), entry.getValue().size());
        }
    }

    @Test
    public void jsonData() {
        List<Alarm> beans = new ArrayList<>();
        Alarm alarm = new Alarm("N001", "无法上网", "2020-08-06 16:00:00");
        beans.add(alarm);
        String value = JSON.toJSONString(beans);
        logger.info("{}", value);
    }

    @Test
    public void futureTest() throws InterruptedException, ExecutionException {
        FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Random random = new Random();
                int sleep = random.nextInt(1000);
                SleepUtil.sleepMilliSecond(sleep);
                return sleep + "";
            }
        });
        Thread thread = new Thread(futureTask);
        thread.start();
        thread.join();
        logger.info("{}", futureTask.get());
    }

    @Test
    public void nullArrayTest() {
        File[] fs = null;
        if (fs != null)
            for (File file : fs) {
                System.out.println(file.getName());
            }
        System.out.println(getCount("aaa"));
    }

    private long getCount(String name) {
        Long value = count.get(name);
        return value == null ? 0L : value;
    }
}