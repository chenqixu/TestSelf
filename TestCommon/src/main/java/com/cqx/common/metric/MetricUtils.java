package com.cqx.common.metric;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监控工具
 */
public class MetricUtils {
    private static final Logger logger = LoggerFactory.getLogger(MetricUtils.class);
    private static final Object buildLock = new Object();
    private static AtomicBoolean reset = new AtomicBoolean(true);
    private static MetricRegistry metricRegistry = new MetricRegistry();
    private static Slf4jReporter reporter;
    private static ConcurrentHashMap<String, Meter> metricCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Timer> timerCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Counter> counterCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Histogram> histogramCache = new ConcurrentHashMap<>();

    /**
     * 默认30秒打印一次指标
     */
//    static {
//        build();
//    }

    /**
     * 立即报告
     */
    public static synchronized void report() {
        if (reporter != null) reporter.report();
    }

    /**
     * 重置reporter为空
     */
    public static void reset() {
        if (reset.getAndSet(false)) {
            logger.info("停止并重置reporter");
            reporter.stop();
            reporter = null;
        }
    }

    /**
     * 如果需要手工调用build，需要先reset一下才可以
     *
     * @param period
     * @param unit
     */
    public static void build(long period, TimeUnit unit) {
        synchronized (buildLock) {
            if (reporter == null) {
                synchronized (buildLock) {
                    reporter = Slf4jReporter.forRegistry(metricRegistry).build();
                    logger.info("MetricUtils.build，period：{}，unit：{}", period, unit);
                    reporter.start(period, unit);
                }
            }
        }
    }

    /**
     * 默认build，30秒打印一次指标
     */
    public static void build() {
        build(30, TimeUnit.SECONDS);
    }

    /**
     * 重置Meter监控
     *
     * @param name 监控名称
     * @return Meter
     */
    public static synchronized Meter reRegisterMeter(String name) {
        metricRegistry.remove(name);
        metricCache.remove(name);
        return getMeter(name);
    }

    public static synchronized Meter getMeter(String name) {
        if (!metricCache.containsKey(name)) {
            Meter metric = metricRegistry.meter(name);
            metricCache.put(name, metric);
        }
        return metricCache.get(name);
    }

    public static synchronized Timer getTimer(String name) {
        if (!timerCache.containsKey(name)) {
            Timer metric = metricRegistry.timer(name);
            timerCache.put(name, metric);
        }
        return timerCache.get(name);
    }

    public static synchronized Counter getCounter(String name) {
        if (!counterCache.containsKey(name)) {
            Counter counter = metricRegistry.counter(name);
            counterCache.put(name, counter);
        }
        return counterCache.get(name);
    }

    public static synchronized Histogram getHistogram(String name) {
        if (!histogramCache.containsKey(name)) {
            Histogram counter = metricRegistry.histogram(name);
            histogramCache.put(name, counter);
        }
        return histogramCache.get(name);
    }
}
