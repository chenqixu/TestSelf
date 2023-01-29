package com.cqx.finance.bean;

/**
 * StockData
 *
 * @author chenqixu
 */
public class StockData {
    private long time;
    private float price;
    private int count;

    public StockData() {
    }

    public StockData(long time, float price, int count) {
        setTime(time);
        setPrice(price);
        setCount(count);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
