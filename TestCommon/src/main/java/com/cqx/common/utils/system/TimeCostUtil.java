package com.cqx.common.utils.system;

/**
 * TimeCostUtil
 *
 * @author chenqixu
 */
public class TimeCostUtil<T> {
    private long start;
    private long end;
    private long lastCheckTime = System.currentTimeMillis();
    private T lastCheckValue;

    public TimeCostUtil() {
    }

    public TimeCostUtil(T lastCheckValue) {
        this.lastCheckValue = lastCheckValue;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void end() {
        end = System.currentTimeMillis();
    }

    /**
     * 间隔多久触发一次
     *
     * @param limitTime
     * @return
     */
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

    /**
     * 判断在时间范围内值是否有变化
     *
     * @param limitTime
     * @param nowCheckValue
     * @return
     */
    public boolean tag(long limitTime, T nowCheckValue) {
        if (System.currentTimeMillis() - lastCheckTime > limitTime) {
            lastCheckTime = System.currentTimeMillis();
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
}
