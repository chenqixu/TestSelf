package com.cqx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class InitCollectorFile {
	// 日志记录器
	private static Logger logger = Logger.getLogger(InitCollectorFile.class);
	// 文件采集的线程列表
	public static List<Thread> TestThreadList = null;
	// 存放队列的Map，不同的目录对应不同的队列
	public static Map<String,Queue<String>> fileNameQueueMap;
	// 退出文件
	public static String exitFile = "";
	// 初始化
	public static void initDataCollector(String conf, String exit){
		System.out.println("加载log4j配置文件:"+conf);
		// 加载log4j配置文件
		PropertyConfigurator.configure(conf);
		logger.info("initDataCollector");
		fileNameQueueMap = new ConcurrentHashMap<String, Queue<String>>();
		TestThreadList = new ArrayList<Thread>();
		exitFile = exit;
	}
	
	/**
	 * 检测是否需要退出
	 * */
	public static boolean exit(){
		boolean flag = false;
		// 实例化退出检测文件
		File f = new File(InitCollectorFile.exitFile);
		// 如果退出文件存在，则退出
		if (f.exists()) {
			flag = true;
		}
		return flag;
	}
}
