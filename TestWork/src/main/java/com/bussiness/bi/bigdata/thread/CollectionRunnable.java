package com.bussiness.bi.bigdata.thread;

public abstract class CollectionRunnable implements Runnable {
	// UUID，线程唯一标志
	protected String uuid;
	// 线程状态标志位
	protected boolean flag = true;
	
	public CollectionRunnable(String uuid){
		this.uuid = uuid;
	}

	@Override
	public abstract void run();
	
	public void close() {
		flag = false;
	}
	
	public boolean isClose() {
		return flag;
	}
}
