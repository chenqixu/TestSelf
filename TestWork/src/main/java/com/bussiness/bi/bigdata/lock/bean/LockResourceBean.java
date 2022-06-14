package com.bussiness.bi.bigdata.lock.bean;

public class LockResourceBean {
	private int count = 0;

	/**
	 * read
	 * */
	public synchronized int getCount() {
		return count;
	}

	/**
	 * write
	 * */
	public synchronized void increase() {
		this.count++;
	}
}
