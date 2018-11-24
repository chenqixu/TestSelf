package com.cqx.jmx.message;

public interface MessageEngineMXBean {
	//结束程序
	public void stop();
	//查看程序是否暂停
	public boolean isPaused();
	//暂停程序或者继续程序
	public void pause(boolean pause);
	public Message getMessage();
	//修改message
	public void changeMessage(Message m);
}
