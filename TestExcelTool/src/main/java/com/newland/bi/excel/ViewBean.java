package com.newland.bi.excel;

import java.util.HashMap;
import java.util.Map;

public class ViewBean {
	private String tablename = "";
	private Map<String, ViewSubBean> tablefiled = new HashMap<String, ViewSubBean>();
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public Map<String, ViewSubBean> getTablefiled() {
		return tablefiled;
	}
	public void setTablefiled(Map<String, ViewSubBean> tablefiled) {
		this.tablefiled = tablefiled;
	}
}
