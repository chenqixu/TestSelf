package com.cqx.jmx.threadpoolstatus;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.cqx.jmx.util.JMXFactory;

public class ThreadPoolJMX {
	public static String getRandomStr() {
		String abc = "abcdefghijklmnopqrstuvwxyz";
		char[] arr = abc.toCharArray();
		return String.valueOf(arr[new Random().nextInt(26)]);
	}
	
	public static void main(String[] args) throws Exception {
		final TrackingThreadPool executor = new TrackingThreadPool(3, 10, 5, TimeUnit.SECONDS,
//				new SynchronousQueue<Runnable>());
				new LinkedBlockingDeque<Runnable>());
		//每2秒来3个请求
		Runnable myRunnable = new Runnable() {
			public void run() {
				try {
					while (true) {
//						Thread.sleep(2000);
						System.out.println(Thread.currentThread().getName() + " run");
						FetchTaskResult ftr = new FetchTaskResult();
						executor.execute(new FetchTask(getRandomStr(), ftr));
						executor.execute(new FetchTask(getRandomStr(), ftr));
						executor.execute(new FetchTask(getRandomStr(), ftr));
						if(executor.isEndTask())
							System.out.println(ftr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(myRunnable).start();
		//监控
		JMXFactory.startJMX("ThreadPoolStatus", new ThreadPoolStatus(executor));
	}
}
