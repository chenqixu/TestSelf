package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.Utils;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SumBean
 *
 * @author chenqixu
 */
public class SumBean implements Comparable<SumBean> {
    private String time;
    private Long sum;

    public SumBean() {
    }

    public SumBean(Map.Entry<String, AtomicLong> entry) {
        this.time = entry.getKey();
        this.sum = entry.getValue().get();
    }

    @Override
    public String toString() {
        return String.format("[time]%s, [sum]%s", getTime(), getSum());
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    @Override
    public int compareTo(SumBean o) {
        try {
            return Long.compare(Utils.formatTime(o.getTime()), Utils.formatTime(getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
