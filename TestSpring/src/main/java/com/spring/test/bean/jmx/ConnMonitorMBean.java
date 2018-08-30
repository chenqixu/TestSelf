package com.spring.test.bean.jmx;

import java.util.List;

public interface ConnMonitorMBean {
	public int getPoolSize();
	public int getActivePoolSize();
	public List<String> getConnTimeList();
}
