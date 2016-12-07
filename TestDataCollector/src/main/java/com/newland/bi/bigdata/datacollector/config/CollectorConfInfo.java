package com.newland.bi.bigdata.datacollector.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.common.CollectorConstant;

public class CollectorConfInfo {
	//��־��¼��
	private static Logger logger = Logger.getLogger(CollectorConfInfo.class);
	//�ɼ����������ļ�ʵ��
	private static Configuration conf;
	//Դ���ݷ�����
	public static String ftpServerIp ;
	//Դ���ݷ�����FTP����˿�
	public static int ftpServerPort ;
	//Դ���ݷ�����FTP�����û���
	public static String ftpServerUser ;
	//Դ���ݷ�����FTP��������
	public static String ftpServerPassword ;
	//Դ���ݷ�����������·��
	public static String sourceDataPath;
	//�����ļ��Ϳ����ļ��Ƿ�ֿ����
	public static boolean ifSaveDifferentPath;
	//���ݴ�ŵ���ʱ·��(Linuxϵͳ·��)
	public static String saveTmpPath ;
	//���ݴ�ŵı���·��(Linuxϵͳ·��)
	public static String saveDataPath ;
	//���ݴ�ŵı���·��(Linuxϵͳ·��)
	public static String saveCtlPath ;
	//����Դ�ļ��ĺ�׺��
	public static String dataSourceFileSuffixName;
	//ÿ�������ļ��Ƿ��ж�Ӧ�Ŀ����ļ�
	public static boolean ifHasCtlSourceFile;
	//����Դ�ļ��ĺ�׺��
	public static String ctlSourceFileSuffixName;
	//�Ƿ����ؿ����ļ�
	public static boolean ifDownloadCtlFile;
	//�Ƿ������ؿ����ļ�
	public static boolean ifFirstDownloadCtlFile;
	//FTP�����ļ����м��ļ�����׺
	public static String ftpTmpFileNameSuffix;
	//FTP���������ļ����Ƿ�ɾ��Դ�ļ�
	public static boolean ifNeedDeleteSourceDataFile;
	//FTP���ؿ����ļ����Ƿ��ƶ�Դ�ļ�
	public static boolean ifNeedMvSourceDataFile;
	//�ɼ������������ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static String mvSourceDataFilePath;
	//�ɼ�����������һ���ļ��󣬿���Դ�ļ��Ƿ���Ҫɾ��
	public static boolean ifNeedDeleteSourceCtlFile;
	//�ɼ�����������һ���ļ��󣬿���Դ�ļ��Ƿ���Ҫ�ƶ�Ŀ¼
	public static boolean ifNeedMvSourceCtlFile;
	//�ɼ���������������ļ����ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static  String  mvSourceCtlFilePath;
	//�ɼ������˳�����ļ�
//	public static String exitFileFullName;
	//ÿ��Ŀ¼���ص��߳���
	public static String ftpThreadNumEveryDir;
	//��ȡ�ļ���ʱ���ķָ���
	public static String splitFileNameForDateTime ;
	//Դ�����ļ����е�������Сʱ�Ƿ���ͬһ��λ��
	public static boolean ifDateAndHourTheSameLocation;
	//�ļ��������ַ������ļ����е�λ��
	public static int dateLocationAtFileName;
	//��ȡ�ļ��������ַ����Ŀ�ʼλ�� 
	public static int  dateSubStringBegin;
	//��ȡ�ļ��������ַ����Ľ���λ��
	public static int  dateSubStringEnd;
	//�ļ���Сʱ�������ַ������ļ����е�λ��
	public static int  hourLocationAtFileName;
	//��ȡ�ļ���Сʱ�ַ����Ŀ�ʼλ��
	public static int  hourSubStringBegin;
	//��ȡ�ļ���Сʱ�ַ����Ľ���λ��
	public static int  hourSubStringEnd;
	//FTP�����ļ�����������λM��
	public static int  downloadBuffersize;
	//�Ƿ��ļ���ʱ������
	public static  boolean  ifSortByFileName;
	//�Ƿ�ɼ�����Сʱ���ļ�
	public static   boolean  ifDownloadNewestFile;
	//�Ƿ���˳��ض�ʱ����ļ���
	public static  boolean  ifFilterSpecificTimeFileName;
	//���˳��ض�ʱ�����ļ������ַ���
	public static  String  filterSpecificTimeFileName;
	//�Ƿ���˳��ض��ļ���
	public static  boolean  ifFilterSpecificFileName;
	//���˳��ض��ļ������ַ���
	public static  String  filterSpecificFileName;
	//�Ƿ��ų�ָ���ļ������ļ�
	public static  boolean  ifExcludeSpecificFileName;
	//�ų�ָ���ļ������ļ�
	public static  String  excludeSpecificFileName;
	//�Ƿ�����ض�Сʱ�ļ�������
	public static  boolean  ifFilterSpecificHourIncrement;
	//�ɼ��ض�ʱ�����ļ���ʱ����������ֵ��1��-1��
	public static  int  incrementHourValue;
	//�Ƿ����SFTP����
	public static boolean ifConnectBySftp;
	//�Ƿ�Դ���ݻ��ֳɼ��ݽ��вɼ�
	public static  boolean ifCollectByPartition;
	//��Դ���ݻ��ֳɼ���
	public static  int partitions;
	//���ɼ�����ɼ��ڼ�������
	public static  int  collectPartion;
	//�����Ǹ�λ�ý��л���Դ����
	public static  int locationToPartition;
	/**
	 * 
	 * @description: ��ȡ�����ļ��е�ֵ
	 * @author:xixg
	 * @date:2014-02-19
	 * @return void
	 */
	public static void getConfInfo(String collectorConfFilePath){
		try {
			//ʵ�����ɼ����������ļ�ʵ��
			Path collectorFilePath = new Path(collectorConfFilePath);
			conf = new Configuration();
			//���زɼ������Լ��������ļ�
			conf.addResource(collectorFilePath);
			ftpServerIp = conf.get(CollectorConstant.FTP_SERVER_IP);
			ftpServerPort = conf.getInt(CollectorConstant.FTP_SERVER_PORT, 21);
			ftpServerUser = conf.get(CollectorConstant.FTP_SERVER_USER);
			ftpServerPassword = conf.get(CollectorConstant.FTP_SERVER_PASSWORD);
			sourceDataPath = conf.get(CollectorConstant.SOURCE_DATA_PATH);
			ifSaveDifferentPath = conf.getBoolean(CollectorConstant.IF_SAVE_DIFFERENT_PATH, false);
			saveTmpPath = conf.get(CollectorConstant.SAVE_TMP_PATH);
			saveDataPath = conf.get(CollectorConstant.SAVE_DATA_PATH);
			saveCtlPath = conf.get(CollectorConstant.SAVE_CTL_PATH);
			dataSourceFileSuffixName = conf.get(CollectorConstant.DATA_SOURCE_FILE_SUFFIX_NAME);
			ifHasCtlSourceFile = conf.getBoolean(CollectorConstant.IF_HAS_CTL_SOURCE_FILE, true);
			ctlSourceFileSuffixName = conf.get(CollectorConstant.CTL_SOUCE_FILE_SUFFIX_NAME);
			ifDownloadCtlFile = conf.getBoolean(CollectorConstant.IF_DOWNLOAD_CTL_FILE,false);
			ifFirstDownloadCtlFile = conf.getBoolean(CollectorConstant.IF_FIRST_DOWNLOAD_CTL_FILE,false);
			ftpTmpFileNameSuffix = conf.get(CollectorConstant.FTP_TMP_FILE_NAME_SUFFIX);
			ifNeedDeleteSourceDataFile = conf.getBoolean(CollectorConstant.IF_NEED_DELETE_SOURCE_DATA_FILE, true);
			ifNeedMvSourceDataFile = conf.getBoolean(CollectorConstant.IF_NEED_MV_SOURCE_DATA_FILE, true);
			mvSourceDataFilePath = conf.get(CollectorConstant.MV_SOURCE_DATA_FILE_PATH);
			ifNeedDeleteSourceCtlFile = conf.getBoolean(CollectorConstant.IF_NEED_DELETE_SOURCE_CTL_FILE, true);
			ifNeedMvSourceCtlFile = conf.getBoolean(CollectorConstant.IF_NEED_MV_SOURCE_CTL_FILE, true);
			mvSourceCtlFilePath = conf.get(CollectorConstant.MV_SOURCE_CTL_FILE_PATH);
//			exitFileFullName = conf.get(CollectorConstant.EXIT_FILE_FULL_NAME);
			ftpThreadNumEveryDir = conf.get(CollectorConstant.FTP_THREAD_NUM_EVERY_DIR);
			splitFileNameForDateTime = conf.get(CollectorConstant.SPLIT_FILE_NAME_FOR_DATE_TIME);
			ifDateAndHourTheSameLocation = conf.getBoolean(CollectorConstant.IF_DATE_AND_HOUR_THE_SAME_LOCATION, false);
			dateLocationAtFileName = conf.getInt(CollectorConstant.DATE_LOCATION_AT_FILE_NAME,4);
			dateSubStringBegin = conf.getInt(CollectorConstant.DATE_SUB_STRING_BEGIN, 0);
			dateSubStringEnd = conf.getInt(CollectorConstant.DATE_SUB_STRING_END, 8);
			hourLocationAtFileName = conf.getInt(CollectorConstant.HOUR_LOCATION_AT_FILE_NAME, 5);
			hourSubStringBegin = conf.getInt(CollectorConstant.HOUR_SUB_STRING_BEGIN, 8);
			hourSubStringEnd = conf.getInt(CollectorConstant.HOUR_SUB_STRING_END, 10);
			downloadBuffersize = conf.getInt(CollectorConstant.DOWNLOAD_BUFFER_SIZE, 10);
			ifSortByFileName = conf.getBoolean(CollectorConstant.IF_SORT_BY_FILE_NAME, true);
			ifDownloadNewestFile = conf.getBoolean(CollectorConstant.IF_DOWNLOAD_NEWEST_FILE, false);
			ifFilterSpecificFileName = conf.getBoolean(CollectorConstant.IF_FILTER_SPECIFIC_FILE_NAME, false);
			filterSpecificFileName = conf.get(CollectorConstant.FILTER_SPECIFIC_FILE_NAME);
			ifFilterSpecificTimeFileName = conf.getBoolean(CollectorConstant.IF_FILTER_SPECIFIC_TIME_FILE_NAME, false);
			filterSpecificTimeFileName = conf.get(CollectorConstant.FILTER_SPECIFIC_TIME_FILE_NAME);
			ifExcludeSpecificFileName = conf.getBoolean(CollectorConstant.IF_EXCLUDE_SPECIFIC_FILE_NAME,false);
			excludeSpecificFileName = conf.get(CollectorConstant.EXCLUDE_SPECIFIC_FILE_NAME);
			ifFilterSpecificHourIncrement = conf.getBoolean(CollectorConstant.IF_FILTER_SPECIFIC_HOUR_INCREMENT, false);
			incrementHourValue = conf.getInt(CollectorConstant.INCREMENT_HOUR_VALUE, 1);
			ifConnectBySftp = conf.getBoolean(CollectorConstant.IF_CONNECT_BY_SFTP, false);
			ifCollectByPartition = conf.getBoolean(CollectorConstant.IF_COLLECT_BY_PARTITION, false);
			partitions = conf.getInt(CollectorConstant.PARTITIONS, 1);
			collectPartion = conf.getInt(CollectorConstant.COLLECT_PARTION, 1);
			locationToPartition = conf.getInt(CollectorConstant.LOCATION_TO_PARTITION, 1);
		} catch (Exception e) {
			logger.error("%%%%%��ȡ�ɼ����������ļ��е�������Ϣ��������",e);
		}
	}
	
	/**
	 * 
	 * @description:��ȡ�����ļ�ʵ��
	 * @author:xixg
	 * @date:2013-11-26
	 * @return Configuration
	 */
	public static Configuration getConfInstance() {
		return conf;
	}
}
