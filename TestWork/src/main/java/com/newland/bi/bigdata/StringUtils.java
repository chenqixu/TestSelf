package com.newland.bi.bigdata;

import java.util.Properties;
import java.util.Set;

public class StringUtils {
	// 列分隔符 tab符
	public final static char COLUMN_SPLIT = (char)0x09;  // \t
	// 手机号码
	private String telnumber ;
	public String getTelnumber() {
		return telnumber;
	}
	public void setTelnumber(String telnumber) {
		this.telnumber = telnumber;
	}
	/**
	 * 号码处理，去除86开头
	 * */
	public void telnumberProcessing(){
		if(telnumber.startsWith("86")){
			telnumber = telnumber.substring(2);
		}
	}
	
	public void test(){
		Properties systemProps = System.getProperties();
		Set<String> keys = systemProps.stringPropertyNames();
		for (String key : keys) {
			System.out.println(key);
		}
	}
	
	public static void main(String[] args) {
//		StringBuffer _tmp = new StringBuffer("");
//		_tmp.append("1").append(COLUMN_SPLIT);
//		_tmp.append("2").append(COLUMN_SPLIT);
//		_tmp.append("3").append(COLUMN_SPLIT);
//		System.out.println(_tmp.toString());
//		_tmp.deleteCharAt(_tmp.length()-1);
//		System.out.println(_tmp.toString());
//		System.out.println(_tmp.toString().split(String.valueOf(COLUMN_SPLIT)).length);		
//		
//		new StringUtils().test();
//		String a = args[0];
//		a = a.replace("\\", "");
//		System.out.println(a);
//		String[] aa = a.split("\\+");
//		for(String s: aa){
//			System.out.println(s);
//		}
		StringUtils su = new StringUtils();
		su.setTelnumber("8613509323824");
		su.telnumberProcessing();
		System.out.println(su.getTelnumber());
	}
}
