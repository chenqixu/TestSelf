package com.cqx.process;

public class LogInfoFactory {
	private static LogInfoFactory log = new LogInfoFactory();
	private static String LEVEL = "ERR";
	private boolean isNeedTime = true;
	
	private LogInfoFactory(){}
	
	public static LogInfoFactory getInstance(){
		return log!=null?log:new LogInfoFactory();
	}
	
	public void setNeedTime(boolean isNeedTime) {
		this.isNeedTime = isNeedTime;
	}

	/**
	 * <pre>
	 * 0:ERR
	 * 1:INFO
	 * 2:WARN
	 * 3:DEBUG
	 * </pre>
	 * */
	public void setLevel(int levelcode){
		switch (levelcode) {
		case 0:
			LEVEL = "ERR";
			break;
		case 1:
			LEVEL = "INFO";
			break;
		case 2:
			LEVEL = "WARN";
			break;
		case 3:
			LEVEL = "DEBUG";
			break;			
		default:
			LEVEL = "INFO";
			break;
		}
	}
	
	public String getLEVEL() {
		return LEVEL;
	}
	
	private void print(String str){
		String output = "";
		if(isNeedTime)
			output = "##"+new java.util.Date()+"##"+getLEVEL()+"##";
		System.out.println(output+str);
	}

	public void info(String str){
//		setLevel(1);
		if(getLEVEL().equals("INFO")){
			print(str);
		}
	}
	
	public void warn(String str){
//		setLevel(2);
		if(getLEVEL().equals("WARN")){
			print(str);
		}
	}
	
	public void err(String str, Exception e){
//		setLevel(0);
		if(getLEVEL().equals("ERR")){
			print(str);
			e.printStackTrace();
		}
	}
	
	public void debug(String str){
//		setLevel(3);
		if(getLEVEL().equals("DEBUG")){
			print(str);
		}
	}
}
