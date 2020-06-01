package com.cqx.common.utils.system;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeCostUtil
 *
 * @author chenqixu
 */
public class TimeCostUtil<T> {
    private long start;
    private long end;
    private boolean isNanoTime = false;//是否纳秒，默认是毫秒
    private long incrementCost = 0;
    private long lastCheckTime = getCurrentTime();
    private T lastCheckValue;

    public TimeCostUtil() {
    }

    public TimeCostUtil(boolean isNanoTime) {
        this.isNanoTime = isNanoTime;
    }

    public TimeCostUtil(T lastCheckValue) {
        this.lastCheckValue = lastCheckValue;
    }

    public void start() {
        start = getCurrentTime();
    }

    public void stop() {
        end = getCurrentTime();
    }

    /**
     * 间隔多久触发一次
     *
     * @param limitTime
     * @return
     */
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

    /**
     * 判断在时间范围内值是否有变化
     *
     * @param limitTime
     * @param nowCheckValue
     * @return
     */
    public boolean tag(long limitTime, T nowCheckValue) {
        if (getCurrentTime() - lastCheckTime > limitTime) {
            lastCheckTime = getCurrentTime();
            if (nowCheckValue.equals(lastCheckValue)) {
                //说明值在时间段内没有更新
                return true;
            } else {
                //说明值在时间段内有更新
                lastCheckValue = nowCheckValue;
                return false;
            }
        }
        return false;
    }

    private long getCurrentTime() {
        if (isNanoTime) {
            return System.nanoTime();//纳秒
        } else {
            return System.currentTimeMillis();
        }
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
