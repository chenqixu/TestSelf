package com.newland.bi.bigdata.bean;

import java.util.List;

public class ExcelSheetList {
	private String sheetName;// sheetName
	private List<List<String>> sheetList; // sheet内容,多行组成一个list,每行单独一个list
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public List<List<String>> getSheetList() {
		return sheetList;
	}
	public void setSheetList(List<List<String>> sheetList) {
		this.sheetList = sheetList;
	}	
}
