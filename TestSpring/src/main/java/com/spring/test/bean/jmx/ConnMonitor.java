package com.spring.test.bean.jmx;

import java.util.List;

import com.spring.test.util.DBUtilFactory;

public class ConnMonitor implements ConnMonitorMBean {

	@Override
	public int getPoolSize() {
		return DBUtilFactory.getPoolSize();
	}

	@Override
	public int getActivePoolSize() {
		return DBUtilFactory.getActiveSize();
	}

	@Override
	public List<String> getConnTimeList() {
		return DBUtilFactory.getConnTimeList();
	}

}
