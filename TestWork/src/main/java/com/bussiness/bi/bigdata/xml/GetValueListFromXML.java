package com.bussiness.bi.bigdata.xml;

import java.util.ArrayList;
import java.util.List;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

/**
 * 从XML文件中读取VALUE
 * */
public class GetValueListFromXML extends SumConfigFile {
	private List<String> valueList = new ArrayList<String>();
	
	/**
	 * 解析XML内容
	 * */
	@Override
	protected void dealXml(String xmlPath){
		String xml = readXml(xmlPath);
		ResultXML rx = new ResultXML();
		XMLData xd = new XMLData(xml);
		rx.rtFlag = true;
		rx.bXmldata = true;
		rx.xmldata = xd;
		rx.setbFlag(false);
//		rx.resetParent().node("configuration").setParentPointer();
		rx.setRowFlagInfo("property");
		rx.First();
		while(!rx.isEof()){
//			String name = rx.getColumnsValue("name");
			String value = rx.getColumnsValue("value");
//			String description = rx.getColumnsValue("description");
			// 加入到bean
			addBean(value);
			rx.Next();
		}		
	}

	protected void addBean(String value) {
		valueList.add(value);
	}
	
	public void printValueList(){
		for(String str : valueList){
			System.out.println(str);
		}
	}	

	@Override
	public void process(String path){
		// 处理
		dealXml(path);
		// 打印
		printValueList();
	}
	
	public static void main(String[] args) {
		new GetValueListFromXML().process("H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\conf\\joingnxdr.xml");
	}
}

class PropertyBean {
	private String name;
	private String value;
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}