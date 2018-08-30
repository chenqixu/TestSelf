package com.cqx.jmx.realtime.bean;

public interface ThreadUtilStatusMBean {
	public String[] getDaoThreadList(int i);
	public String[] getAllListDealTaskTime();
	public long getAllListTotalDealTaskTime();
	public int getAllDealTaskCount();
	public long getAvgDealTaskTime();
}
