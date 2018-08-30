package com.cqx.process;

public class LogInfoFactory {
	private static String LEVEL = "INFO";
	
	public static void setLevel(int levelcode){
		switch (levelcode) {
		case 0:
			LEVEL = "INFO";
			break;
		case 1:
			LEVEL = "ERR";
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
	
	public static String getLEVEL() {
		return LEVEL;
	}

	public static void info(String str){
		if(getLEVEL().equals("INFO")){
			System.out.println("##"+new java.util.Date()+"##"+getLEVEL()+"##"+str);
		}
	}
	
	public static void warn(String str){
		if(getLEVEL().equals("WARN")){
			System.out.println("##"+new java.util.Date()+"##"+getLEVEL()+"##"+str);
		}
	}
	
	public static void err(String str, Exception e){
		if(getLEVEL().equals("ERR")){
			System.out.println("##"+new java.util.Date()+"##"+getLEVEL()+"##"+str);
			e.printStackTrace();
		}
	}
	
	public static void debug(String str){
		if(getLEVEL().equals("DEBUG")){
			System.out.println("##"+new java.util.Date()+"##"+getLEVEL()+"##"+str);
		}
	}
}
