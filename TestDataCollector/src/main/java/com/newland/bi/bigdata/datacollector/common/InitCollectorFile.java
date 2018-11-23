package com.newland.bi.bigdata.datacollector.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

/**
 * 
 * @description:初始化操作类，例如初始化全局变量等
 * @author:xixg
 * @date:2014-02-19
 */
public class InitCollectorFile {
	//日志记录器
	private static Logger logger = Logger.getLogger(InitCollectorFile.class);
	//存放文件名的队列，队列为线程安全的
//	public static Queue<String> fileNameQueue;
	//存放队列的Map，不同的目录对应不同的队列
	public static Map<String,Queue<String>> fileNameQueueMap;
	//采集程序扫描目录次数
	public static long currCycleNum = 1;
	//采集程序是否要退出标志
	public static boolean ifNeedExitFlag;
	//文件采集的线程列表
	public static List<Thread> dataCollectorThreadList;
	//采集程序扫描目录时有文件次数
	public static long currScanDirHasFile = 0;
	public static boolean isExit = false;
	public static String currFilterSpecificFileName;
	//源数据服务器的数据路径
	public static String[] sourceDataPathAraay;
	//每个目录下载的线程数
	public static String[] ftpThreadNumEveryDirArray;
	//采集程序下载完控制文件后移动源文件到目的目录
	public static  String[]  mvSourceCtlFilePathArray;
	//采集程序下载完移动源文件到目的目录
	public static String[] mvSourceDataFilePathArray;
	//数据文件采集出错时存放的线程安全的ConcurrentHashMap
	public static Map<String, Integer> errFileMap ;
	//记录清空存放采集错误的concurrentHashMap的时间
	public static long clearTime = 0;
	
	/**
	 * 
	 * @description:初始化方法，例如：获取配置文件信息，初始化全局变量等操作
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
	public static void initDataCollector(){
		try {
			//加载log4j配置文件
			PropertyConfigurator.configure(CollectorConstant.LOG4J_CONF_FILE_PATH);
			//读取配置文件（parameter_config.xml）
			CollectorConfInfo.getConfInfo(CollectorConstant.COLLECTOR_FILE_CONF_PATH);
//			fileNameQueue = new ConcurrentLinkedQueue<String>();
			fileNameQueueMap = new ConcurrentHashMap<String, Queue<String>>();
			dataCollectorThreadList = new ArrayList<Thread>();
			errFileMap = new ConcurrentHashMap<String, Integer>();
			//实例化退出检测文件对象
			File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);//采集程序退出检测文件
			//程序启动后初始化时，如果退出检测文件存在，则删除
			if(f.exists()) f.delete();
			ifNeedExitFlag = false;
			sourceDataPathAraay = CollectorConfInfo.sourceDataPath.split(CollectorConstant.COMMA_SPLIT_STRING);
			ftpThreadNumEveryDirArray = CollectorConfInfo.ftpThreadNumEveryDir.split(CollectorConstant.COMMA_SPLIT_STRING);
			mvSourceCtlFilePathArray = CollectorConfInfo.mvSourceCtlFilePath.split(CollectorConstant.COMMA_SPLIT_STRING);
			mvSourceDataFilePathArray = CollectorConfInfo.mvSourceDataFilePath.split(CollectorConstant.COMMA_SPLIT_STRING);
		} catch (Exception e) {
			logger.error("%%%%%初始化方法出错！！！",e);
		}
	}
	
}
