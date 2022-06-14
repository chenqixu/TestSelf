package com.bussiness.bi.bigdata.lock.test;

import com.cqx.common.utils.system.SleepUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LockTest
 *
 * @author chenqixu
 */
public class LockTest {
    private static volatile LockTest lockTest;
    private Lock lock = new ReentrantLock();

    public LockTest() {
        System.out.println("初始化，" + this);
    }

    public static void main(String[] args) {
        final LockTest test = new LockTest();
        new Thread() {
            public void run() {
                LockTest lockTest = test.getInstance(Thread.currentThread());
                System.out.println(String.format("%s %s %s", System.currentTimeMillis(), Thread.currentThread().getName(), lockTest));
            }
        }.start();
        new Thread() {
            public void run() {
                LockTest lockTest = test.getInstance(Thread.currentThread());
                System.out.println(String.format("%s %s %s", System.currentTimeMillis(), Thread.currentThread().getName(), lockTest));
            }
        }.start();
    }

    //        public void insert(Thread thread) {
//            lock.lock();
//            try {
//                System.out.println(thread.getName() + "得到了锁");
//                for (int i = 0; i < 5; i++) {
//                    arrayList.add(i);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                System.out.println(thread.getName() + "释放了锁");
//                lock.unlock();
//            }
//        }
    public void insert(Thread thread) {
        if (lock.tryLock()) {
            try {
                System.out.println(thread.getName() + "得到了锁");
                SleepUtil.sleepMilliSecond(5);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(thread.getName() + "释放了锁");
                lock.unlock();
            }
        } else {
            System.out.println(thread.getName() + "获取锁失败");
        }
    }

    public LockTest getInstance(Thread thread) {
        if (lockTest == null) {
            synchronized (LockTest.class) {
                System.out.println(System.currentTimeMillis() + " " + thread.getName() + "得到了同步锁");
                if (lockTest == null) {
                    SleepUtil.sleepMilliSecond(5);
                    lockTest = new LockTest();
                } else {
                    System.out.println(System.currentTimeMillis() + " " + thread.getName() + " 第二重检查，lockTest已经初始化。");
                }
                System.out.println(System.currentTimeMillis() + " " + thread.getName() + "释放了同步锁");
            }
        } else {
            System.out.println(System.currentTimeMillis() + " " + thread.getName() + " 第一重检查，lockTest已经初始化。");
        }
        return lockTest;
    }

    public LockTest getLockTest(Thread thread) {
        if (lockTest == null) {
//            if (lock.tryLock()) {
            try {
                lock.lock();
                if (lockTest == null) {
                    lockTest = new LockTest();
                }
                System.out.println(System.currentTimeMillis() + " " + thread.getName() + "得到了锁");
                SleepUtil.sleepMilliSecond(5);
            } finally {
                System.out.println(System.currentTimeMillis() + " " + thread.getName() + "释放了锁");
                lock.unlock();
            }
//            } else {
//                System.out.println(System.currentTimeMillis() + " " + thread.getName() + "获取锁失败");
//            }
        } else {
            System.out.println(System.currentTimeMillis() + " " + thread.getName() + " lockTest已经初始化。");
        }
        return lockTest;
    }
}
