package com.cqx.jmx;

public interface HelloWorldMBean {
	String getGreeting();
	void setGreeting(String greeting);
	void printGreeting();
}
