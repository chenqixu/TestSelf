package com.cqx.finance.bean;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * StockCompany
 *
 * @author chenqixu
 */
public class StockCompany {
    private final Object lock = new Object();
    private String companyName;
    private float currentPrice;
    private float maxUpPrice;
    private float maxDownPrice;
    private int maxCount = 0;
    private int minCount = 0;
    private AtomicBoolean first = new AtomicBoolean(true);

    public StockCompany(String companyName, float openPrice) {
        this.companyName = companyName;
        this.currentPrice = openPrice;
        float max = Float.valueOf(String.format("%.2f", Math.round(openPrice * 100) / 1000f));
        this.maxUpPrice = openPrice + max;
        this.maxDownPrice = openPrice - max;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public float getCurrentPrice() {
        synchronized (lock) {
            return currentPrice;
        }
    }

    public void setCurrentPrice(float currentPrice) {
        synchronized (lock) {
            this.currentPrice = currentPrice;
        }
    }

    public float getMaxUpPrice() {
        return maxUpPrice;
    }

    public float getMaxDownPrice() {
        return maxDownPrice;
    }

    public boolean isMaxUp() {
        synchronized (lock) {
            return currentPrice == maxUpPrice;
        }
    }

    public boolean isMaxDown() {
        synchronized (lock) {
            return currentPrice == maxDownPrice;
        }
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setCount(int count) {
        if (first.getAndSet(false)) {
            this.minCount = count;
            this.maxCount = count;
        } else if (count > this.maxCount) {
            this.maxCount = count;
        } else if (count < this.minCount) {
            this.minCount = count;
        }
    }
}
