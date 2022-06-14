package com.bussiness.bi.bigdata.xml;

import java.util.List;

/**
 * 返回的对象
 * */
public class IXmlBatchMsgs {
	private String dateType;
	private List<String> records;
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	public List<String> getRecords() {
		return records;
	}
	public void setRecords(List<String> records) {
		this.records = records;
	}
	public String toString() {		
		return "["+this.dateType+"]size:"+(this.records!=null?this.records.size():"0");
	}
}
