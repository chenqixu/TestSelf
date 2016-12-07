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
	// ��־��¼��
	private static Logger logger = Logger.getLogger(InitCollectorFile.class);
	// �ļ��ɼ����߳��б�
	public static List<Thread> TestThreadList = null;
	// ��Ŷ��е�Map����ͬ��Ŀ¼��Ӧ��ͬ�Ķ���
	public static Map<String,Queue<String>> fileNameQueueMap;
	// �˳��ļ�
	public static String exitFile = "";
	// ��ʼ��
	public static void initDataCollector(String conf, String exit){
		System.out.println("����log4j�����ļ�:"+conf);
		// ����log4j�����ļ�
		PropertyConfigurator.configure(conf);
		logger.info("initDataCollector");
		fileNameQueueMap = new ConcurrentHashMap<String, Queue<String>>();
		TestThreadList = new ArrayList<Thread>();
		exitFile = exit;
	}
	
	/**
	 * ����Ƿ���Ҫ�˳�
	 * */
	public static boolean exit(){
		boolean flag = false;
		// ʵ�����˳�����ļ�
		File f = new File(InitCollectorFile.exitFile);
		// ����˳��ļ����ڣ����˳�
		if (f.exists()) {
			flag = true;
		}
		return flag;
	}
}
