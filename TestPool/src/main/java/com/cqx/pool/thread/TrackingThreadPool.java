package com.cqx.pool.thread;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TrackingThreadPool extends ThreadPoolExecutor {
	private final Map<Runnable, Boolean> inProgress = new ConcurrentHashMap<Runnable,Boolean>();
	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private long totalTime;
	private int totalTasks;
	private boolean hasFinish = false;
	
	public TrackingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
		TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
//		synchronized (this) {
//			this.hasFinish = false;
//		}
		super.beforeExecute(t, r);
		inProgress.put(r, Boolean.TRUE);
		startTime.set(new Long(System.currentTimeMillis()));
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		long time = System.currentTimeMillis() - startTime.get().longValue();
		synchronized (this) {
			totalTime += time;
			++totalTasks;
		}
		inProgress.remove(r);
		super.afterExecute(r, t);
		/**
		 * wait for all
		 * */
		synchronized (this) {
			System.out.println("auto call...afterExecute,now getActiveCount() is:"+this.getActiveCount());
			if (this.getActiveCount() == 1) { //last process execute ok
				this.hasFinish = true;
				this.notify();
			}
		}
	}
	
	/**
	 * is end task
	 * <pre>
	 * wait for all process execute
	 * </pre>
	 * */
	public boolean isEndTask() {
		synchronized (this) {
			while (this.hasFinish == false) {
				System.out.println("wait for all process execute ...");
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.hasFinish = false;
		}
		return !this.hasFinish;
	}
	
	public Set<Runnable> getInProgressTasks() {
		return Collections.unmodifiableSet(inProgress.keySet());
	}
	
	public synchronized int getTotalTasks() {
		return totalTasks;
	}
	
	public synchronized double getAverageTaskTime() {
		return (totalTasks == 0) ? 0 : totalTime / totalTasks;
	}
}
