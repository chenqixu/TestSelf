package com.cqx.common.utils.system;

/**
 * 时间间隔是否满足
 *
 * @author chenqixu
 */
public class TimeInterval {
    // 时间间隔
    private long commitInterval = 30000L;
    // 上一次处理时间
    private long lastDealTime = 0L;

    public TimeInterval() {
    }

    public TimeInterval(long commitInterval) {
        this.commitInterval = commitInterval;
    }

    /**
     * 本次距离上一次处理时间是否超过允许的间隔
     *
     * @return
     */
    public boolean isTimeOut() {
        if (lastDealTime == 0) {
            newLastDealTime();
        }
        return (System.currentTimeMillis() - lastDealTime) > commitInterval;
    }

    /**
     * 重置时间判断
     */
    public void newLastDealTime() {
        lastDealTime = System.currentTimeMillis();
    }
}
