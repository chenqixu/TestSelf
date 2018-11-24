package com.newland.bi.bigdata.bean;

import java.io.Serializable;

public class FtpHostInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String host;
	private long updateTime; // 本次更新时间
	private String currentFirstFileName; // 当前扫描时间最早的文件名
	private String currentFirstFileTime; // 当前扫描最早文件对应的文件时间
	private long currentTotelFilesNum; // 当前机器总数量

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long getCurrentTotelFilesNum() {
		return currentTotelFilesNum;
	}

	public void setCurrentTotelFilesNum(long currentTotelFilesNum) {
		this.currentTotelFilesNum = currentTotelFilesNum;
	}

	public String getCurrentFirstFileName() {
		return currentFirstFileName;
	}

	public void setCurrentFirstFileName(String currentFirstFileName) {
		this.currentFirstFileName = currentFirstFileName;
	}

	public String getCurrentFirstFileTime() {
		return currentFirstFileTime;
	}

	public void setCurrentFirstFileTime(String currentFirstFileTime) {
		this.currentFirstFileTime = currentFirstFileTime;
	}
}
