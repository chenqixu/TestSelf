package com.newland.bi.bigdata.thread;

import com.newland.storm.component.etl.common.model.IMessageId;

public class FileMessageId implements Comparable<FileMessageId>, IMessageId {
	// public long msgNumber; // tracks order in which msg came in
	// public String sourceId;
	public String fileName;
	public long charOffset;
	public long lineNumber;
	public boolean fileReadCompletely = false;// 读取完成标志
	public long readComplateTime;// 读取完成时间

	public FileMessageId() {
	}

	/**
	 * 返回完成标志
	 * */
	public boolean isFileReadCompletely() {
		return fileReadCompletely;
	}
	
	/**
	 * 读取完成
	 * */
	public void setFileReadCompletely() {
		this.fileReadCompletely = true;
		this.readComplateTime = System.currentTimeMillis();
	}

	/**
	 * 消息
	 * 
	 * @param msgNumber
	 *            消息序号
	 * @param sourceId
	 * @param fileName
	 *            所属文件名
	 * @param offset
	 */
	public FileMessageId(long lineNumber, String fileName) {
		// this.msgNumber = msgNumber;
		this.fileName = fileName;
		// this.sourceId = sourceId;

		// this.charOffset = ((FileOffset) offset).charOffset;
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "{" + fileName + ":" + lineNumber + "}";
	}

	@Override
	public int compareTo(FileMessageId rhs) {
		if (lineNumber < rhs.lineNumber) {
			return -1;
		}
		if (lineNumber > rhs.lineNumber) {
			return 1;
		}
		return fileName.compareTo(rhs.fileName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileMessageId) {
			FileMessageId rhs = (FileMessageId) obj;
			if (lineNumber == rhs.lineNumber && fileName.equals(rhs.fileName)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public String getSourceKey() {
		return fileName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public long getLineNum() {
		return lineNumber;
	}

	public long getCharOffset() {
		return charOffset;
	}

	public void setCharOffset(long charOffset) {
		this.charOffset = charOffset;
	}
}
