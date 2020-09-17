package com.newland.bi.jkreport.bean;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * HdfsLSCheck
 *
 * @author chenqixu
 */
public class HdfsLSCheck {
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private AtomicInteger checkInteger = new AtomicInteger(0);
    private String type;
    private String date;

    public HdfsLSCheck(String type, String date) {
        this.type = type;
        this.date = date;
    }

    public void increment() {
        atomicInteger.incrementAndGet();
    }

    public void check() {
        int checkNum = checkInteger.incrementAndGet();
        int allNum = atomicInteger.get();
        if (checkNum == allNum) {
            System.out.println(String.format("%s %s 触发生成.complete文件", getType(), getDate()));
        }
    }

    @Override
    public String toString() {
        return "atomicInteger：" + atomicInteger.get() + "，checkInteger：" + checkInteger.get();
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }
}
