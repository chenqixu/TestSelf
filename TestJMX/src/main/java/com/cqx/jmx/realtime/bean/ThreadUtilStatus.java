package com.cqx.jmx.realtime.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cqx.jmx.realtime.util.ThreadUtil;

public class ThreadUtilStatus implements ThreadUtilStatusMBean {
	
	private final List<ThreadUtil> tulist = new ArrayList<ThreadUtil>();
	
	public ThreadUtilStatus(){}
	
	public void add(ThreadUtil tu) {
		tulist.add(tu);
	}
	
	private String[] toStringArray(Collection<?> collection) {
		ArrayList<String> list = new ArrayList<String>();
		for (Object r : collection)
			list.add(r.toString());
		return list.toArray(new String[0]);
	}

	@Override
	public String[] getDaoThreadList(int i) {
		return toStringArray(tulist.get(i).getDaoThreadList());
	}

	@Override
	public int getAllDealTaskCount() {
		return tulist.size();
	}

	@Override
	public long getAvgDealTaskTime() {		
		return getAllListTotalDealTaskTime()/getAllDealTaskCount();
	}

	@Override
	public String[] getAllListDealTaskTime() {
		List<Long>  AllListDealTaskTime = new ArrayList<Long>();
		for(ThreadUtil tu : tulist) {
			AllListDealTaskTime.add(tu.getDealtime());
		}
		return toStringArray(AllListDealTaskTime);
	}

	@Override
	public long getAllListTotalDealTaskTime() {
		long AllListTotalDealTaskTime = 0;
		for(ThreadUtil tu : tulist) {
			AllListTotalDealTaskTime += tu.getDealtime();
		}
		return AllListTotalDealTaskTime;
	}
}
