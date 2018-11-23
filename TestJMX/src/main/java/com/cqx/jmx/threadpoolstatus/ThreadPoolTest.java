package com.cqx.jmx.threadpoolstatus;

import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.management.ObjectName;

public class ThreadPoolTest {
	public static void main(String[] args) throws Exception {
		Runnable myRunnable = new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					System.out.println(Thread.currentThread().getName() + " run");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		// 核心线程数为3，最大线程数为10。超时时间为5秒
		TrackingThreadPool executor = new TrackingThreadPool(3, 10, 5, TimeUnit.SECONDS,
//				new SynchronousQueue<Runnable>());
//				new LinkedBlockingDeque<Runnable>());
				new LinkedBlockingDeque<Runnable>(2));
		executor.execute(myRunnable);
		executor.execute(myRunnable);
		executor.execute(myRunnable);
//		executor.awaitTermination(timeout, unit);
//		executor.submit(task);
//		executor.invokeAll(tasks);
		System.out.println("---先开三个---");
		System.out.println("核心线程数" + executor.getCorePoolSize());
		System.out.println("线程池数" + executor.getPoolSize());
		System.out.println("队列任务数" + executor.getQueue().size());
		executor.execute(myRunnable);
		executor.execute(myRunnable);
		executor.execute(myRunnable);
		System.out.println("---再开三个---");
		System.out.println("核心线程数" + executor.getCorePoolSize());
		System.out.println("线程池数" + executor.getPoolSize());
		System.out.println("队列任务数" + executor.getQueue().size());
//		Thread.sleep(8000);
//		System.out.println("----8秒之后----");
//		System.out.println("核心线程数" + executor.getCorePoolSize());
//		System.out.println("线程池数" + executor.getPoolSize());
//		System.out.println("队列任务数" + executor.getQueue().size());
		if (executor.isEndTask()) {
			executor.shutdown();
		}
	}
}
