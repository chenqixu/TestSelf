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
    long incrementCost = 0;
    boolean isNanoTime = false;
    long lastCheckTime = getCurrentTime();

    public TimeCostUtil() {
    }

    public TimeCostUtil(boolean isNanoTime) {
        this.isNanoTime = isNanoTime;
    }

    public static String getNow(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }

    private long getCurrentTime() {
        if (isNanoTime) {
            return System.nanoTime();//纳秒
        } else {
            return System.currentTimeMillis();
        }
    }

    public void start() {
        start = getCurrentTime();
    }

    public void stop() {
        end = getCurrentTime();
    }

    public boolean tag(long limitTime) {
        if (getCurrentTime() - lastCheckTime > limitTime) {
            lastCheckTime = getCurrentTime();
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
        if (start == 0) return 0;
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

    public void stopAndIncrementCost() {
        stop();
        incrementCost += getCost();
    }

    public long getIncrementCost() {
        if (isNanoTime)
            return incrementCost / 1000000;
        else
            return incrementCost;
    }

    public void resetIncrementCost() {
        incrementCost = 0;
    }
}
