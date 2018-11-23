package com.mr.comm;

public class MoveRecord {
//	   msisdn,
	private String msisdn;
//	   vlr_imei,
	private String vlr_imei;
//	   vlr_event_type,
	private String vlr_event_type;
//	   old_lac,
	private String old_lac;
//	   old_cell,
	private String old_cell;
//	   old_time,
	private String old_time;
//	   vlr_lac,
	private String vlr_lac;
//	   vlr_cell,
	private String vlr_cell;
//	   vlr_report_time,
	private String vlr_report_time;
//	   seu_type,
	private String seu_type;
//	   is_exception 
	private String is_exception;
//	数据类型，用来标志是MC还是LTE
	private String dataType;
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setVlr_imei(String vlr_imei) {
		this.vlr_imei = vlr_imei;
	}
	public String getVlr_imei() {
		return vlr_imei;
	}
	public void setVlr_event_type(String vlr_event_type) {
		this.vlr_event_type = vlr_event_type;
	}
	public String getVlr_event_type() {
		return vlr_event_type;
	}
	public void setOld_lac(String old_lac) {
		this.old_lac = old_lac;
	}
	public String getOld_lac() {
		return old_lac;
	}
	public void setOld_cell(String old_cell) {
		this.old_cell = old_cell;
	}
	public String getOld_cell() {
		return old_cell;
	}
	public void setOld_time(String old_time) {
		this.old_time = old_time;
	}
	public String getOld_time() {
		return old_time;
	}
	public void setVlr_lac(String vlr_lac) {
		this.vlr_lac = vlr_lac;
	}
	public String getVlr_lac() {
		return vlr_lac;
	}
	public void setVlr_cell(String vlr_cell) {
		this.vlr_cell = vlr_cell;
	}
	public String getVlr_cell() {
		return vlr_cell;
	}
	public void setVlr_report_time(String vlr_report_time) {
		this.vlr_report_time = vlr_report_time;
	}
	public String getVlr_report_time() {
		return vlr_report_time;
	}
	public void setSeu_type(String seu_type) {
		this.seu_type = seu_type;
	}
	public String getSeu_type() {
		return seu_type;
	}
	public void setIs_exception(String is_exception) {
		this.is_exception = is_exception;
	}
	public String getIs_exception() {
		return is_exception;
	}
	
	
	
	
	
	
	
	
}
