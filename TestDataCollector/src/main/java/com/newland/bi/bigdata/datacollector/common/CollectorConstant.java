
package com.newland.bi.bigdata.datacollector.common;

public class CollectorConstant {
	public static final String COLLECTOR_FILE_CONF_PATH =  "H:/Work/WorkSpace/MyEclipse10/self/TestSelf/TestDataCollector/src/main/resources/conf/data-collector-config.xml";
//	public static final String COLLECTOR_FILE_CONF_PATH = "../conf/data-collector-config.xml";
	public static final String LOG4J_CONF_FILE_PATH = "H:/Work/WorkSpace/MyEclipse10/self/TestSelf/TestDataCollector/src/main/resources/conf/log4j.properties";
//	public static final String LOG4J_CONF_FILE_PATH = "../conf/log4j.properties"; 
	//�ɼ������˳�����ļ� 
//	public static final String EXIT_FILE_FULL_NAME = "d:/1/exit.dat";
	public static final String EXIT_FILE_FULL_NAME = "../bin/exit.dat";
	//Դ����FTP��������IP
	public static final String FTP_SERVER_IP = "ftpServerIp";
	//Դ����FTP������FTP����˿�
	public static final String FTP_SERVER_PORT = "ftpServerPort";
	//Դ���ݷ�����FTP������û���
	public static final String FTP_SERVER_USER = "ftpServerUser";
	//Դ���ݷ�����FTP���������
	public static final String FTP_SERVER_PASSWORD = "ftpServerPassword";
	//FTP������Դ����·��
	public static final String SOURCE_DATA_PATH = "sourceDataPath";
	//�����ļ��Ϳ����ļ��Ƿ�ֿ����
	public static final String IF_SAVE_DIFFERENT_PATH = "ifSaveDifferentPath";
	//FTP�������ݺ����ݴ�ŵı���·��
	public static final String SAVE_DATA_PATH= "saveDataPath";
	//FTP�������ݺ�У���ļ���ŵı���·��
	public static final String SAVE_CTL_PATH= "saveCtlPath";
	//FTP���ش���ļ�����ʱ·��
	public static final String SAVE_TMP_PATH= "temporaryPath";
	//����Դ�ļ��ĺ�׺��
	public static final String DATA_SOURCE_FILE_SUFFIX_NAME = "dataSourceFileSuffixName";
	//ÿ�������ļ��Ƿ��ж�Ӧ�Ŀ����ļ�
	public static final String IF_HAS_CTL_SOURCE_FILE = "ifHasCtlSourceFile";
	//����Դ�ļ��ĺ�׺��
	public static final String CTL_SOUCE_FILE_SUFFIX_NAME = "ctlSourceFileSuffixName";
	//�Ƿ����ؿ����ļ�
	public static final String IF_DOWNLOAD_CTL_FILE = "ifDownloadCtlFile";
	//�Ƿ������ؿ����ļ�
	public static final String IF_FIRST_DOWNLOAD_CTL_FILE = "ifFirstDownloadCtlFile";
	//FTP���ع��̵��м����Ƶĺ�׺��
	public static final String FTP_TMP_FILE_NAME_SUFFIX = "ftpTmpFileNameSuffix";
	//�ɼ�����������һ���ļ�������Դ�ļ��Ƿ���Ҫɾ��
	public static final String IF_NEED_DELETE_SOURCE_DATA_FILE = "ifNeedDeleteSourceDataFile";
	//�ɼ�����������һ���ļ�������Դ�ļ��Ƿ���Ҫ�ƶ�Ŀ¼
	public static final String IF_NEED_MV_SOURCE_DATA_FILE = "ifNeedMvSourceDataFile";
	//�ɼ����������������ļ����ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static final  String  MV_SOURCE_DATA_FILE_PATH= "mvSourceDataFilePath";
	//�ɼ�����������һ���ļ��󣬿���Դ�ļ��Ƿ���Ҫɾ��
	public static final String IF_NEED_DELETE_SOURCE_CTL_FILE = "ifNeedDeleteSourceCtlFile";
	//�ɼ�����������һ���ļ��󣬿���Դ�ļ��Ƿ���Ҫ�ƶ�Ŀ¼
	public static final String IF_NEED_MV_SOURCE_CTL_FILE = "ifNeedMvSourceCtlFile";
	//�ɼ���������������ļ����ƶ�Դ�ļ���Ŀ��Ŀ¼
	public static final  String  MV_SOURCE_CTL_FILE_PATH= "mvSourceCtlFilePath";
	//���Ŀ¼��ÿ��Ŀ¼�����FTP�����߳���
	public static final String  FTP_THREAD_NUM_EVERY_DIR = "ftpThreadNumEveryDir";
	//��ȡ�ļ���ʱ���ķָ���
	public static final  String  SPLIT_FILE_NAME_FOR_DATE_TIME= "splitFileNameForDateTime";
	//Դ�����ļ����е�������Сʱ�Ƿ���ͬһ��λ��
	public static final  String  IF_DATE_AND_HOUR_THE_SAME_LOCATION= "ifDateAndHourTheSameLocation";
	//�ļ��������ַ������ļ����е�λ��
	public static final  String  DATE_LOCATION_AT_FILE_NAME= "dateLocationAtFileName";
	//��ȡ�ļ��������ַ����Ŀ�ʼλ�� 
	public static final  String  DATE_SUB_STRING_BEGIN= "dateSubStringBegin";
	//��ȡ�ļ��������ַ����Ľ���λ��
	public static final  String  DATE_SUB_STRING_END= "dateSubStringEnd";
	//�ļ���Сʱ�������ַ������ļ����е�λ��
	public static final  String  HOUR_LOCATION_AT_FILE_NAME= "hourLocationAtFileName";
	//��ȡ�ļ���Сʱ�ַ����Ŀ�ʼλ��
	public static final  String  HOUR_SUB_STRING_BEGIN= "hourSubStringBegin";
	//��ȡ�ļ���Сʱ�ַ����Ľ���λ��
	public static final  String  HOUR_SUB_STRING_END= "hourSubStringEnd";
	//FTP�����ļ�����������λM��
	public static final  String  DOWNLOAD_BUFFER_SIZE= "downloadBuffersize";
	//�Ƿ��ļ���ʱ������
	public static final  String  IF_SORT_BY_FILE_NAME= "ifSortByFileName";
	//�Ƿ�ɼ�����Сʱ���ļ�
	public static final  String  IF_DOWNLOAD_NEWEST_FILE= "ifDownloadNewestFile";
	//�Ƿ���˳��ض��ļ���
	public static final  String  IF_FILTER_SPECIFIC_FILE_NAME= "ifFilterSpecificFileName";
	//���˳��ض��ļ������ַ���
	public static final  String  FILTER_SPECIFIC_FILE_NAME= "filterSpecificFileName";
	//�Ƿ��ų�ָ���ļ������ļ�
	public static final  String  IF_EXCLUDE_SPECIFIC_FILE_NAME= "ifExcludeSpecificFileName";
	//�ų�ָ���ļ������ļ�
	public static final  String  EXCLUDE_SPECIFIC_FILE_NAME= "excludeSpecificFileName";
	//�Ƿ���˳��ض�ʱ�����ļ���
	public static final  String  IF_FILTER_SPECIFIC_TIME_FILE_NAME= "ifFilterSpecificTimeFileName";
	//���˳��ض�ʱ����ļ������ַ���
	public static final  String  FILTER_SPECIFIC_TIME_FILE_NAME= "filterSpecificTimeFileName";
	//�Ƿ�����ض�Сʱ�ļ�������
	public static final  String  IF_FILTER_SPECIFIC_HOUR_INCREMENT= "ifFilterSpecificHourIncrement";
	//�ɼ��ض�ʱ�����ļ���ʱ����������ֵ��1��-1��
	public static final  String  INCREMENT_HOUR_VALUE= "incrementHourValue";
	//ʱ����ʽ
	public static final String FORMAT_0F_HOUR = "yyyyMMdd_HH";
	//�Ƿ����SFTP����
	public static final String IF_CONNECT_BY_SFTP = "ifConnectBySftp";
	//sftp channel
	public static final String SFTP_CHANNEL = "sftp";
	//���ŷָ���
	public static final String COMMA_SPLIT_STRING = ",";
	
	//�Ƿ�Դ���ݻ��ֳɼ��ݽ��вɼ�
	public static final String IF_COLLECT_BY_PARTITION = "ifCollectByPartition";
	//��Դ���ݻ��ֳɼ���
	public static final String PARTITIONS = "partitions";
	//���ɼ�����ɼ��ڼ�������
	public static final String COLLECT_PARTION = "collectPartion";
	//�����Ǹ�λ�ý��л���Դ����
	public static final String LOCATION_TO_PARTITION = "locationToPartition";
	
    
}