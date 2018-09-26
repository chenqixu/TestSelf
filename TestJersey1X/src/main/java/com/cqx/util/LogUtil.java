package com.cqx.util;

public class LogUtil {
	private static LogUtil log = new LogUtil();
	
	private LogUtil(){}	
	
	public static LogUtil getInstance(){
		if(log==null) log= new LogUtil();
		return log;
	}
	
	public void error(String content){
		error(content, null);
	}
	
	public void error(String content, Exception ex){
		System.out.println(content);
		if(ex!=null) ex.printStackTrace();
	}
}
