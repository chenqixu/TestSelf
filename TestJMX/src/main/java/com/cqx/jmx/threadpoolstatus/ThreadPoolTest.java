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
		// �����߳���Ϊ3������߳���Ϊ10����ʱʱ��Ϊ5��
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
		System.out.println("---�ȿ�����---");
		System.out.println("�����߳���" + executor.getCorePoolSize());
		System.out.println("�̳߳���" + executor.getPoolSize());
		System.out.println("����������" + executor.getQueue().size());
		executor.execute(myRunnable);
		executor.execute(myRunnable);
		executor.execute(myRunnable);
		System.out.println("---�ٿ�����---");
		System.out.println("�����߳���" + executor.getCorePoolSize());
		System.out.println("�̳߳���" + executor.getPoolSize());
		System.out.println("����������" + executor.getQueue().size());
//		Thread.sleep(8000);
//		System.out.println("----8��֮��----");
//		System.out.println("�����߳���" + executor.getCorePoolSize());
//		System.out.println("�̳߳���" + executor.getPoolSize());
//		System.out.println("����������" + executor.getQueue().size());
		if (executor.isEndTask()) {
			executor.shutdown();
		}
	}
}
