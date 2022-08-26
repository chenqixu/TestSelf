package com.cqx.finance.bean;

import com.alibaba.fastjson.JSON;

/**
 * StockBean
 *
 * @author chenqixu
 */
public class StockOrderBean implements BeanComparable {
    private float hopePrice;
    private float finalPrice;
    private int count;
    private long hopeTime;
    private long finalTime;
    private StockOrderType type;
    private String cusName;

    public StockOrderBean(float hopePrice, int count) {
        this("测试用户", StockOrderType.BUY, hopePrice, count);
    }

    public StockOrderBean(String cusName, StockOrderType type, float hopePrice, int count) {
        setCusName(cusName);
        setType(type);
        setHopePrice(hopePrice);
        setCount(count);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof StockOrderBean) {
            StockOrderBean _o = (StockOrderBean) o;
            long ret = this.getHopeTime() - _o.getHopeTime();
            if (ret < 0) {
                return -1;
            } else if (ret > 0) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public float getHopePrice() {
        return hopePrice;
    }

    public void setHopePrice(float hopePrice) {
        this.hopePrice = hopePrice;
        setHopeTime(System.currentTimeMillis());
    }

    public float getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(float finalPrice) {
        this.finalPrice = finalPrice;
        setFinalTime(System.currentTimeMillis());
    }

    @Override
    public long getHopeTime() {
        return hopeTime;
    }

    public void setHopeTime(long hopeTime) {
        this.hopeTime = hopeTime;
    }

    public long getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }

    public StockOrderType isType() {
        return type;
    }

    public void setType(StockOrderType type) {
        this.type = type;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }
}
