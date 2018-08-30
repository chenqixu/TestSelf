package com.cqx.jmx.threadpoolstatus;

import java.util.ArrayList;
import java.util.Collection;

public class ThreadPoolStatus implements ThreadPoolStatusMBean {
	private final TrackingThreadPool pool;
	
	public ThreadPoolStatus(TrackingThreadPool pool) {
		this.pool = pool;
	}
	
	@Override
	public int getActiveThreads() {
		return pool.getPoolSize();
	}

	@Override
	public int getActiveTasks() {
		return pool.getActiveCount();
	}

	@Override
	public int getTotalTasks() {
		return pool.getTotalTasks();
	}

	@Override
	public int getQueuedTasks() {
		return pool.getQueue().size();
	}

	@Override
	public double getAverageTaskTime() {
		return pool.getAverageTaskTime();
	}

	@Override
	public String[] getActiveTaskNames() {
		return toStringArray(pool.getInProgressTasks());
	}

	@Override
	public String[] getQueuedTaskNames() {
		return toStringArray(pool.getQueue());
	}

	private String[] toStringArray(Collection<Runnable> collection) {
		ArrayList<String> list = new ArrayList<String>();
		for (Runnable r : collection)
			list.add(r.toString());
		return list.toArray(new String[0]);
	}
}
