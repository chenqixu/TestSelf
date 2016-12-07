package com.newland.bi.bigdata.hbase;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class Split {
	// 字段切割符
	public static String SPLIT_VALUE = "\\|";
	
	private String apn;	
	public String getApn() {
		return apn;
	}
	public void setApn(String apn) {
		this.apn = apn;
	}

	public static void main(String[] args) throws Exception {
		String str = "a|b|c|d";
		String[] arr = str.split(SPLIT_VALUE);		
		for(String s:arr){
			System.out.println(s);
		}
		String sss = "apn";
		Split s = new Split();
		Method[] sourceMethods = s.getClass().getMethods();
		for(int i=0;i<sourceMethods.length;i++){
			System.out.println(sourceMethods[i].getName());			
		}
		Class clazz = s.getClass(); 
		Method m2 = clazz.getDeclaredMethod("set"+captureName(sss), String.class);
		m2.invoke(s, "重新设置msg信息！");
		System.out.println(s.getApn());
		
	    DecimalFormat formater = new DecimalFormat("#0.###");
	    double _d = 1.10;
	    System.out.println(formater.format(_d));
	}

	/**
	 * 首字母大写
	 * */
	private static String captureName(String name){
		if(name!=null && name.length()>0){
			char[] cs = name.toCharArray();
			cs[0] -= 32;
			return String.valueOf(cs);
		}else{
			return "";
		}
	}
}
