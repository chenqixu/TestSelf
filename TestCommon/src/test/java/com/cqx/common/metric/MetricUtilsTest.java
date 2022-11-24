package com.cqx.common.metric;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Snapshot;
import com.cqx.common.utils.system.SleepUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(MetricUtilsTest.class);
    private final Object lock = new Object();
    private Meter producer = MetricUtils.getMeter("producer");
    private Meter consumer = MetricUtils.getMeter("consumer");
    private Histogram h1 = MetricUtils.getHistogram("h1");

    @Before
    public void setUp() throws Exception {
//        MetricUtils.build(3, TimeUnit.SECONDS);
    }

    @Test
    public void report() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        Thread prouducer = new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                while (true) {
//                    if (cnt % 1000 == 0) {
//                        SleepUtil.sleepMilliSecond(3000);
//                    }
                    try {
                        queue.put("" + cnt++);
                        producer.mark(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadList.add(prouducer);
        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String content;
                    while (true) {
                        while ((content = queue.poll()) != null) {
                            synchronized (lock) {
                                consumer.mark(1);
                            }
                        }
                    }
                }
            });
            threadList.add(t);
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
    }

    @Test
    public void timerTest() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String[] dates = {"2022-11-09 15:01:01.000"
                , "2022-11-09 15:01:59.000"
                , "2022-11-09 15:01:00.000"
                , "2022-11-09 15:02:00.000"};
        for (String date : dates) {
            System.out.println(String.format("[dates]%s, %s", date, sdf.parse(date).getTime()));
        }
        long current = System.currentTimeMillis();
        System.out.println(String.format("[current]%s, %s", sdf.format(new Date(current)), current));
    }

    @Test
    public void meterTest() {
        final AtomicInteger FACTORY_ID = new AtomicInteger();
        String name = "meterTest";
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
                new MetricUtilsTest.NamedThreadFactory(name + '-' + FACTORY_ID.incrementAndGet()));
        // start
        long period = 3L;
        TimeUnit unit = TimeUnit.SECONDS;
        final double rateFactor = unit.toSeconds(1);
        executor.scheduleAtFixedRate(new Runnable() {
            Random random = new Random();

            @Override
            public void run() {
                try {
                    int r = random.nextInt(10);
                    producer.mark(r);
                    int h = r < 4 ? 1 : r < 7 ? 2 : 3;
                    h1.update(h);
                    logger.info("random={}, count={}, mean_rate={}, m1={}, m5={}, m15={}, rate_unit={}",
                            r,
                            producer.getCount(),
                            convertRate(producer.getMeanRate(), rateFactor),
                            convertRate(producer.getOneMinuteRate(), rateFactor),
                            convertRate(producer.getFiveMinuteRate(), rateFactor),
                            convertRate(producer.getFifteenMinuteRate(), rateFactor),
                            "events/" + unit);

                    final Snapshot snapshot = h1.getSnapshot();
                    logger.info("type=HISTOGRAM, count={}, min={}, max={}, mean={}, stddev={}, " +
                                    "median={}, p75={}, p95={}, p98={}, p99={}, p999={}",
                            h1.getCount(),
                            snapshot.getMin(),
                            snapshot.getMax(),
                            snapshot.getMean(),
                            snapshot.getStdDev(),
                            snapshot.getMedian(),
                            snapshot.get75thPercentile(),
                            snapshot.get95thPercentile(),
                            snapshot.get98thPercentile(),
                            snapshot.get99thPercentile(),
                            snapshot.get999thPercentile());
                } catch (RuntimeException ex) {
                    logger.error("RuntimeException thrown from {}#report. Exception was suppressed."
                            , MetricUtilsTest.this.getClass().getSimpleName(), ex);
                }
            }
        }, period, period, unit);
        SleepUtil.sleepSecond(120);
    }

    private double convertRate(double rate, double rateFactor) {
        return rate * rateFactor;
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private NamedThreadFactory(String name) {
            final SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "metrics-" + name + "-thread-";
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}