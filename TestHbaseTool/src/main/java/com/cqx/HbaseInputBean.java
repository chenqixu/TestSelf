package com.cqx;

public class HbaseInputBean {
	public HbaseInputBean(){		
	}
	public HbaseInputBean(String _confpath, String _tableName,
			String _msisdn, String _starttime_s,
			String _starttime_e, String _filepath){
		this.confpath = _confpath;
		this.tableName = _tableName;
		this.msisdn = _msisdn;
		this.starttime_s = _starttime_s;
		this.starttime_e = _starttime_e;
		this.filepath = _filepath;
	}
	//  ‰»Î≤Œ ˝
	private String tableName = null;
	private String msisdn = null;
	private String starttime_s = null;
	private String starttime_e = null;
	private String filepath = null;
	private String confpath = null;
	public String getConfpath() {
		return confpath;
	}
	public void setConfpath(String confpath) {
		this.confpath = confpath;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getStarttime_s() {
		return starttime_s;
	}
	public void setStarttime_s(String starttime_s) {
		this.starttime_s = starttime_s;
	}
	public String getStarttime_e() {
		return starttime_e;
	}
	public void setStarttime_e(String starttime_e) {
		this.starttime_e = starttime_e;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
