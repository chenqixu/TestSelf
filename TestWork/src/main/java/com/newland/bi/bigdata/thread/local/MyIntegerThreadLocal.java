package com.newland.bi.bigdata.thread.local;

/**
 * MyIntegerThreadLocal
 *
 * @author chenqixu
 */
public class MyIntegerThreadLocal<T> extends ThreadLocal<T> {

    private final int value;

    public MyIntegerThreadLocal(int value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}
