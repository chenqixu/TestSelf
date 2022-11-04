package com.cqx.common.utils.system;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * TimeCostUtil
 *
 * @author chenqixu
 */
public class TimeCostUtil<T> implements Serializable {
    private long start;
    private long end;
    private boolean isNanoTime = false;//是否纳秒，默认是毫秒
    private long incrementCost = 0;
    private long lastCheckTime = getCurrentTime();
    private long lastCheckByTime = getCurrentTime();
    private T lastCheckValue;

    public TimeCostUtil() {
    }

    public TimeCostUtil(boolean isNanoTime) {
        this.isNanoTime = isNanoTime;
    }

    public TimeCostUtil(T lastCheckValue) {
        this.lastCheckValue = lastCheckValue;
    }

    public static String getNow(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }

    public static String getLastDate(String format) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
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

    /**
     * 重置检查时间
     */
    public void resetLastCheckTime() {
        lastCheckTime = getCurrentTime();
    }

    /**
     * 按时间的格式进行校验，判断系统时间是否在分钟、小时级别上进行切换，频率不能太高，因为时间格式化比较耗时
     * <pre>
     *     判断系统时间是否在分钟、小时级别上进行切换，间隔时间反而要求不是那么精确
     *     1分钟级别切换：checkByTimeFormat(yyyyMMddHHmm)
     *     1小时级别切换：checkByTimeFormat(yyyyMMddHH)
     *     比如输入的时间格式是yyyyMMddHHmm
     *     上次检查时间是2022-11-04 15:01:55
     *     下次检查时间是2022-11-04 15:02:13，此时检查发现，分钟已经进行切换
     * </pre>
     *
     * @param format
     * @return
     */
    public boolean checkByTimeFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String _lastCheckByTime = sdf.format(new Date(lastCheckByTime));
        long _tmpCurrentTime = getCurrentTime();
        String _nowCheckByTime = sdf.format(new Date(_tmpCurrentTime));
        if (!_lastCheckByTime.equals(_nowCheckByTime)) {
            lastCheckByTime = _tmpCurrentTime;
            return true;
        }
        return false;
    }
}
