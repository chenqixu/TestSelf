package com.newland.bi.excel;

import java.util.List;

public class ExcelSheetList {
	private String sheetName;// sheetName
	private List<List<String>> sheetList; // sheet����,�������һ��list,ÿ�е���һ��list
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
