package com.newland.bi.bigdata.time;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeCostUtil
 *
 * @author chenqixu
 */
public class TimeCostUtil {
    long start;
    long end;
    long lastCheckTime = System.currentTimeMillis();

    public static String getNow(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
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

    public long stopAndGet() {
        stop();
        return getCost();
    }

    public String getStart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(start));
    }

    public String getEnd() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(end));
    }
}
