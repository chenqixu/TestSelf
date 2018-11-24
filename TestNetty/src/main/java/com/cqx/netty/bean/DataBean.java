package com.cqx.netty.bean;

import com.alibaba.fastjson.JSON;

public class DataBean {
	/**
	 * 线程名称
	 */
	private String threadName;
	/**
	 * 值
	 */
	private int value;
	/**
	 * 时间
	 */
	private String date;

	public DataBean() {
	}

	public DataBean(String threadName, int value, String date) {
		this.threadName = threadName;
		this.value = value;
		this.date = date;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String serializableToString() {
		return JSON.toJSONString(this);
	}

	public static DataBean deserializableFromString(String str) {
		DataBean info = JSON.parseObject(str, DataBean.class);
		return info;
	}
	
	@Override
	public String toString() {
		return serializableToString();
	}
}
