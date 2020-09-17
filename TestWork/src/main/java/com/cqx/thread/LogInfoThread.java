package com.cqx.thread;

import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * LogInfoThread
 *
 * @author chenqixu
 */
public class LogInfoThread {
    private static ConcurrentMap<String, Logger> loggermap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, String> idmap = new ConcurrentHashMap<>();

    public static Logger getCurrentLogger() {
        return loggermap.get(Thread.currentThread().getName());
    }

    public static String getCurrentId() {
        return idmap.get(Thread.currentThread().getName());
    }

    public static void init(Logger _logger, String _id) {
        loggermap.put(Thread.currentThread().getName(), _logger);
        idmap.put(Thread.currentThread().getName(), _id);
    }

    public static void info(String str) {
        getCurrentLogger().info("【" + getCurrentId() + "】" + str);
    }
}
