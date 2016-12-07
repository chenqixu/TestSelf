package com.newland.bi.bigdata.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class A1 extends ASupper{
	private String a1;
	private String time;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getA1() {
		return a1;
	}
	public void setA1(String a1) {
		this.a1 = a1;
	}
	
	public static void main(String[] args) {
		A1 a = new A1();
		a.setA1("123");
		a.setTime("234");
		Object obj = a;	
		try {
			System.out.println(getValueByNameFromObj(obj, "A1.a1"));
			System.out.println(getValueByNameFromObj(obj, "A1.time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Object getValueByNameFromObj(Object obj, String property){
		Object result = null;
		try {
			Field fields[] = obj.getClass().getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				if(fields[i].toString().contains(property)){
					result = fields[i].get(obj);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return result;
	}
}
