package com.cqx.sync.bean;

import com.cqx.common.utils.system.TimeUtil;

/**
 * ThroughputBean
 *
 * @author chenqixu
 */
public class ThroughputBean {
    private String sum_time = TimeUtil.getNow("yyyy-MM-dd HH:mm:ss");
    private int parallelism;
    private long sum_cnt;
    private long timeOutUpdate = System.currentTimeMillis();

    public String getSum_time() {
        return sum_time;
    }

    public void setSum_time(String sum_time) {
        this.sum_time = sum_time;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public long getSum_cnt() {
        return sum_cnt;
    }

    public void setSum_cnt(long sum_cnt) {
        this.sum_cnt = sum_cnt;
    }

    public ThroughputBean addSum_cnt(long sum_cnt) {
        this.sum_cnt = this.sum_cnt + sum_cnt;
        timeOutUpdate = System.currentTimeMillis();
        return this;
    }

    public ThroughputBean addSum_cnt(ThroughputBean throughputBean) {
        this.sum_cnt = this.sum_cnt + throughputBean.getSum_cnt();
        timeOutUpdate = System.currentTimeMillis();
        return this;
    }

    public boolean isTimeOut(long limitTime) {
        long now = System.currentTimeMillis();
        return (now - timeOutUpdate) > limitTime;
    }
}
