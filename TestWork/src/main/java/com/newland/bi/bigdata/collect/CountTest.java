package com.newland.bi.bigdata.collect;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数测试
 *
 * @author chenqixu
 */
public class CountTest {

    public static void main(String[] args) {
        new CountTest().count();
    }

    public void count() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        atomicInteger.incrementAndGet();
        atomicInteger.incrementAndGet();
        int old = atomicInteger.getAndSet(0);
        System.out.println(old + " " + atomicInteger.get());
    }
}
