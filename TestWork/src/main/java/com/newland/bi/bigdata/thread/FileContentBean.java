package com.newland.bi.bigdata.thread;

import java.util.List;

import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;

public class FileContentBean {
	// 文件信息
	private ExtractFileInfo fileInfo;
	// 文件记录
	private List<String> records;
	// 文件是否到末尾
	private boolean isEnd;
	// 当前已读取的字节数
	private long bytePosition;
	// 当前已读取到多少行
	private long linePosition;
	// ACK对象
	private FileMessageId fileMessageId;

	public FileMessageId getFileMessageId() {
		return fileMessageId;
	}

	public void setFileMessageId(FileMessageId fileMessageId) {
		this.fileMessageId = fileMessageId;
	}

	public ExtractFileInfo getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(ExtractFileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	public List<String> getRecords() {
		return records;
	}

	public void setRecords(List<String> records) {
		this.records = records;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public long getBytePosition() {
		return bytePosition;
	}

	public void setBytePosition(long bytePosition) {
		this.bytePosition = bytePosition;
	}

	public long getLinePosition() {
		return linePosition;
	}

	public void setLinePosition(long linePosition) {
		this.linePosition = linePosition;
	}
}
