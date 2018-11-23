package com.newland.bi.bigdata.utils;

import java.lang.management.ManagementFactory;

public class OtherUtils {
	public static String getCurrentPid() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0]; 
		return pid;
	}
	
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
