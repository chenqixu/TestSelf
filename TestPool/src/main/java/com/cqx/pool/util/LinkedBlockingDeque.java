package com.cqx.pool.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LinkedBlockingDeque
 *
 * @author chenqixu
 */
public class LinkedBlockingDeque<E> {

    /**
     * Main lock guarding all access
     */
    private ReentrantLock lock;

    /**
     * Condition for waiting takes
     */
    private Condition notEmpty;

    /** Condition for waiting puts */
    private Condition notFull;

    /**
     * Removes and returns the first element, or null if empty.
     *
     * @return The first element or {@code null} if empty
     */
    private E unlinkFirst() {
//        // assert lock.isHeldByCurrentThread();
//        org.apache.commons.pool2.impl.LinkedBlockingDeque.Node<E> f = first;
//        if (f == null) {
//            return null;
//        }
//        org.apache.commons.pool2.impl.LinkedBlockingDeque.Node<E> n = f.next;
//        E item = f.item;
//        f.item = null;
//        f.next = f; // help GC
//        first = n;
//        if (n == null) {
//            last = null;
//        } else {
//            n.prev = null;
//        }
//        --count;
//        notFull.signal();
//        return item;
        return null;
    }

    public E pollFirst(long timeout, TimeUnit unit)
            throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        System.out.println("nanos：" + nanos);
        lock.lockInterruptibly();
        try {
            E x;
            while ((x = unlinkFirst()) == null) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            return x;
        } finally {
            lock.unlock();
        }
    }

    public void nanoTime() {
        while (true) {
            long start = System.nanoTime();
            for (int i = 0; i < 10000; i++) ;
            long end = System.nanoTime();
            long cost = end - start;
            if (cost < 0) {
                System.out.println("start: " + start + ", end: " + end + ", cost: " + cost);
            }
        }
    }
}
