package com.newland.bi.excel;

import java.util.List;

public class ViewSubBean {
	public static final String EXCEL_SPLIT = "	";
	private String filename = ""; //字段名
	private String type = ""; //类型
	private String length = ""; //长度
	private String default_value = ""; //默认值
	private String desc = ""; //说明
	public String toString(){
		return "\""+this.filename+"\""+EXCEL_SPLIT+"\""+this.type+"\""+EXCEL_SPLIT
				+"\""+this.length+"\""+EXCEL_SPLIT+"\""+this.default_value+"\""+EXCEL_SPLIT+"\""+this.desc+"\"";
	}
	public void setValue(int Field_position, List<String> list){
		if(Field_position==5){
			this.filename = list.get(1);
			this.type = list.get(2);
			this.length = list.get(3);
			this.default_value = list.get(4);
		}else if(Field_position==6){
			this.filename = list.get(1);
			this.type = list.get(2);
			this.length = list.get(3);
			this.default_value = list.get(4);
			this.desc =  list.get(5);
		}
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getDefault_value() {
		return default_value;
	}
	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}	
}
