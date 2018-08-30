package com.cqx.jmx.threadpoolstatus;

public interface ThreadPoolStatusMBean {
	public int getActiveThreads();
	public int getActiveTasks();
	public int getTotalTasks();
	public int getQueuedTasks();
	public double getAverageTaskTime();
	public String[] getActiveTaskNames();
	public String[] getQueuedTaskNames();
}
