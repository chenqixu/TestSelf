package com.newland.bi.bigdata.lock.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockTest
 *
 * @author chenqixu
 */
public class LockTest {
    private final static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        try {
            boolean flag = lock.tryLock();
            System.out.println(flag);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("unlock");
        }
    }
}
