package com.newland.bi.bigdata.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


/**
 * 线程池测试
 */
public class MyExecutors {
	// 日志类
	private static MyLogger logger = MyLoggerFactory.getLogger(MyExecutors.class);
	// 封闭线程池
	private ExecutorService executor;
	// 延迟启动线程池
	private ScheduledExecutorService schexecutor;
	// 线程列表
	private List<MyRunnable> threadlist;
	// 多久执行一次
	public static final int SCAN_INTERVAL = 2;
	// 线程
	private MyRunnable my;
	// 有返回值得线程
	private MyCallable myCallable;
	
	public MyExecutors() {
		threadlist = new ArrayList<MyRunnable>();
		executor = Executors.newFixedThreadPool(2);
		schexecutor = Executors.newScheduledThreadPool(2);
	}
	
	public MyExecutors(int executorsSize) {
		executor = Executors.newFixedThreadPool(executorsSize);
	}
	
	public void init() {
		my = new MyRunnable();
		myCallable = new MyCallable(); 
	}
	
	public void execSingle() {
		executor.execute(my);
		threadlist.add(my);
	}
	
	/**
	 * 提交有返回值的线程
	 * @return
	 */
	private Future<Integer> submit() {
		return executor.submit(myCallable);
	}
	
	/**
	 * 打印有返回值的线程的结果
	 */
	private void futureGet(Future<Integer> future) {
		try {
			logger.info("future.get：{} ", future.get());
		} catch (InterruptedException | ExecutionException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void futureGetSubmit() {
		futureGet(submit());
	}
	
	/**
	 * 新增一个线程到线程池中运行
	 */
	public void exec() {
		try {
			MyRunnable my = new MyRunnable();
			executor.execute(my);
			threadlist.add(my);
		} catch (Exception e) {
			logger.error("线程池满了，无法新增："+e.getMessage(), e);
		}
	}
	
	/**
	 * 禁止继续往线程池里增加线程
	 */
	public void shutdown() {
		logger.info("executor.shutdown");
		executor.shutdown();
		logger.info("schexecutor.shutdown");
		schexecutor.shutdown();
	}
	
	/**
	 * 真正停止线程
	 */
	public void stoptask() {
		for(MyRunnable task : threadlist) {
			logger.info(task+" stop");
			task.stop();
		}
	}
	
	/**
	 * 延迟2秒启动线程，间隔{@code SCAN_INTERVAL}再执行
	 */
	public void scheduleWithFixedDelay() {
		MyRunnable my = new MyRunnable(true);
		threadlist.add(my);
		schexecutor.scheduleWithFixedDelay(my, 2, SCAN_INTERVAL, TimeUnit.SECONDS);
	}
	
	/**
	 * 我的线程，不停执行
	 */
	class MyRunnable implements Runnable {
		private boolean isonce = false;
		private int index = 0;
		
		/**
		 * 默认是不停执行的
		 */
		public MyRunnable() {
			index++;
			logger.info(this + " index {} ", index);
		}
		
		/**
		 * 可以只执行一次
		 * @param isonce
		 */
		public MyRunnable(boolean isonce) {
			this.isonce = isonce;
		}
		
		@Override
		public void run() {
			int count = 0;
			do {
				logger.info(this+" exec "+(count++));
				try {
					// 随机休眠
					TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			} while (!isonce);
		}
		
		public void stop() {
			this.isonce = true;
		}
	}
	
	class MyCallable implements Callable<Integer> {
		private int index = 0;
		
		public MyCallable() {
			index++;
			logger.info(this + " index {} ", index);
		}
		
		@Override
		public Integer call() throws Exception {
			int count = 0;
			logger.info(this+" call "+(count++));
			return count;
		}
	}
	
}
