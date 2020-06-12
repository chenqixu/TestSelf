package com.cqx.jmx.demo;

public interface HelloWorldMBean {
	String getGreeting();
	void setGreeting(String greeting);
	void printGreeting();
	boolean isPaused();
	void pause(boolean paused);
	String exec(String cmd);
	int size();
}
