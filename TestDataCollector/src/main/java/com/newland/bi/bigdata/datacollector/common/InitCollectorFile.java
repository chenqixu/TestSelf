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
 * @description:��ʼ�������࣬�����ʼ��ȫ�ֱ�����
 * @author:xixg
 * @date:2014-02-19
 */
public class InitCollectorFile {
	//��־��¼��
	private static Logger logger = Logger.getLogger(InitCollectorFile.class);
	//����ļ����Ķ��У�����Ϊ�̰߳�ȫ��
//	public static Queue<String> fileNameQueue;
	//��Ŷ��е�Map����ͬ��Ŀ¼��Ӧ��ͬ�Ķ���
	public static Map<String,Queue<String>> fileNameQueueMap;
	//�ɼ�����ɨ��Ŀ¼����
	public static long currCycleNum = 1;
	//�ɼ������Ƿ�Ҫ�˳���־
	public static boolean ifNeedExitFlag;
	//�ļ��ɼ����߳��б�
	public static List<Thread> dataCollectorThreadList;
	//�ɼ�����ɨ��Ŀ¼ʱ���ļ�����
	public static long currScanDirHasFile = 0;
	public static boolean isExit = false;
	public static String currFilterSpecificFileName;
	//Դ���ݷ�����������·��
	public static String[] sourceDataPathAraay;
	//ÿ��Ŀ¼���ص��߳���
	public static String[] ftpThreadNumEveryDirArray;
	//�ɼ���������������ļ����ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static  String[]  mvSourceCtlFilePathArray;
	//�ɼ������������ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static String[] mvSourceDataFilePathArray;
	//�����ļ��ɼ�����ʱ��ŵ��̰߳�ȫ��ConcurrentHashMap
	public static Map<String, Integer> errFileMap ;
	//��¼��մ�Ųɼ������concurrentHashMap��ʱ��
	public static long clearTime = 0;
	
	/**
	 * 
	 * @description:��ʼ�����������磺��ȡ�����ļ���Ϣ����ʼ��ȫ�ֱ����Ȳ���
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
	public static void initDataCollector(){
		try {
			//����log4j�����ļ�
			PropertyConfigurator.configure(CollectorConstant.LOG4J_CONF_FILE_PATH);
			//��ȡ�����ļ���parameter_config.xml��
			CollectorConfInfo.getConfInfo(CollectorConstant.COLLECTOR_FILE_CONF_PATH);
//			fileNameQueue = new ConcurrentLinkedQueue<String>();
			fileNameQueueMap = new ConcurrentHashMap<String, Queue<String>>();
			dataCollectorThreadList = new ArrayList<Thread>();
			errFileMap = new ConcurrentHashMap<String, Integer>();
			//ʵ�����˳�����ļ�����
			File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);//�ɼ������˳�����ļ�
			//�����������ʼ��ʱ������˳�����ļ����ڣ���ɾ��
			if(f.exists()) f.delete();
			ifNeedExitFlag = false;
			sourceDataPathAraay = CollectorConfInfo.sourceDataPath.split(CollectorConstant.COMMA_SPLIT_STRING);
			ftpThreadNumEveryDirArray = CollectorConfInfo.ftpThreadNumEveryDir.split(CollectorConstant.COMMA_SPLIT_STRING);
			mvSourceCtlFilePathArray = CollectorConfInfo.mvSourceCtlFilePath.split(CollectorConstant.COMMA_SPLIT_STRING);
			mvSourceDataFilePathArray = CollectorConfInfo.mvSourceDataFilePath.split(CollectorConstant.COMMA_SPLIT_STRING);
		} catch (Exception e) {
			logger.error("%%%%%��ʼ��������������",e);
		}
	}
	
}
