
package com.newland.bi.bigdata.datacollector.common;

public class CollectorConstant {
	public static final String COLLECTOR_FILE_CONF_PATH =  "H:/Work/WorkSpace/MyEclipse10/self/TestSelf/TestDataCollector/src/main/resources/conf/data-collector-config.xml";
//	public static final String COLLECTOR_FILE_CONF_PATH = "../conf/data-collector-config.xml";
	public static final String LOG4J_CONF_FILE_PATH = "H:/Work/WorkSpace/MyEclipse10/self/TestSelf/TestDataCollector/src/main/resources/conf/log4j.properties";
//	public static final String LOG4J_CONF_FILE_PATH = "../conf/log4j.properties"; 
	//采集程序退出检测文件 
//	public static final String EXIT_FILE_FULL_NAME = "d:/1/exit.dat";
	public static final String EXIT_FILE_FULL_NAME = "../bin/exit.dat";
	//源数据FTP服务器的IP
	public static final String FTP_SERVER_IP = "ftpServerIp";
	//源数据FTP服务器FTP服务端口
	public static final String FTP_SERVER_PORT = "ftpServerPort";
	//源数据服务器FTP服务的用户名
	public static final String FTP_SERVER_USER = "ftpServerUser";
	//源数据服务器FTP服务的密码
	public static final String FTP_SERVER_PASSWORD = "ftpServerPassword";
	//FTP服务器源数据路径
	public static final String SOURCE_DATA_PATH = "sourceDataPath";
	//数据文件和控制文件是否分开存放
	public static final String IF_SAVE_DIFFERENT_PATH = "ifSaveDifferentPath";
	//FTP下载数据后，数据存放的本地路径
	public static final String SAVE_DATA_PATH= "saveDataPath";
	//FTP下载数据后，校验文件存放的本地路径
	public static final String SAVE_CTL_PATH= "saveCtlPath";
	//FTP下载存放文件的临时路径
	public static final String SAVE_TMP_PATH= "temporaryPath";
	//数据源文件的后缀名
	public static final String DATA_SOURCE_FILE_SUFFIX_NAME = "dataSourceFileSuffixName";
	//每个数据文件是否含有对应的控制文件
	public static final String IF_HAS_CTL_SOURCE_FILE = "ifHasCtlSourceFile";
	//控制源文件的后缀名
	public static final String CTL_SOUCE_FILE_SUFFIX_NAME = "ctlSourceFileSuffixName";
	//是否下载控制文件
	public static final String IF_DOWNLOAD_CTL_FILE = "ifDownloadCtlFile";
	//是否先下载控制文件
	public static final String IF_FIRST_DOWNLOAD_CTL_FILE = "ifFirstDownloadCtlFile";
	//FTP下载过程的中间名称的后缀名
	public static final String FTP_TMP_FILE_NAME_SUFFIX = "ftpTmpFileNameSuffix";
	//采集程序下载完一个文件后，数据源文件是否需要删除
	public static final String IF_NEED_DELETE_SOURCE_DATA_FILE = "ifNeedDeleteSourceDataFile";
	//采集程序下载完一个文件后，数据源文件是否需要移动目录
	public static final String IF_NEED_MV_SOURCE_DATA_FILE = "ifNeedMvSourceDataFile";
	//采集程序下载完数据文件后移动源文件到目的目录
	public static final  String  MV_SOURCE_DATA_FILE_PATH= "mvSourceDataFilePath";
	//采集程序下载完一个文件后，控制源文件是否需要删除
	public static final String IF_NEED_DELETE_SOURCE_CTL_FILE = "ifNeedDeleteSourceCtlFile";
	//采集程序下载完一个文件后，控制源文件是否需要移动目录
	public static final String IF_NEED_MV_SOURCE_CTL_FILE = "ifNeedMvSourceCtlFile";
	//采集程序下载完控制文件后移动源文件到目的目录
	public static final  String  MV_SOURCE_CTL_FILE_PATH= "mvSourceCtlFilePath";
	//多个目录，每个目录分配的FTP下载线程数
	public static final String  FTP_THREAD_NUM_EVERY_DIR = "ftpThreadNumEveryDir";
	//截取文件名时间点的分隔符
	public static final  String  SPLIT_FILE_NAME_FOR_DATE_TIME= "splitFileNameForDateTime";
	//源数据文件名中的日期与小时是否在同一个位置
	public static final  String  IF_DATE_AND_HOUR_THE_SAME_LOCATION= "ifDateAndHourTheSameLocation";
	//文件名日期字符串在文件名中的位置
	public static final  String  DATE_LOCATION_AT_FILE_NAME= "dateLocationAtFileName";
	//截取文件名日期字符串的开始位置 
	public static final  String  DATE_SUB_STRING_BEGIN= "dateSubStringBegin";
	//截取文件名日期字符串的结束位置
	public static final  String  DATE_SUB_STRING_END= "dateSubStringEnd";
	//文件名小时分钟秒字符串在文件名中的位置
	public static final  String  HOUR_LOCATION_AT_FILE_NAME= "hourLocationAtFileName";
	//截取文件名小时字符串的开始位置
	public static final  String  HOUR_SUB_STRING_BEGIN= "hourSubStringBegin";
	//截取文件名小时字符串的结束位置
	public static final  String  HOUR_SUB_STRING_END= "hourSubStringEnd";
	//FTP下载文件缓冲区（单位M）
	public static final  String  DOWNLOAD_BUFFER_SIZE= "downloadBuffersize";
	//是否按文件名时间排序
	public static final  String  IF_SORT_BY_FILE_NAME= "ifSortByFileName";
	//是否采集最新小时的文件
	public static final  String  IF_DOWNLOAD_NEWEST_FILE= "ifDownloadNewestFile";
	//是否过滤出特定文件名
	public static final  String  IF_FILTER_SPECIFIC_FILE_NAME= "ifFilterSpecificFileName";
	//过滤出特定文件名的字符串
	public static final  String  FILTER_SPECIFIC_FILE_NAME= "filterSpecificFileName";
	//是否排除指定文件名的文件
	public static final  String  IF_EXCLUDE_SPECIFIC_FILE_NAME= "ifExcludeSpecificFileName";
	//排除指定文件名的文件
	public static final  String  EXCLUDE_SPECIFIC_FILE_NAME= "excludeSpecificFileName";
	//是否过滤出特定时间点的文件名
	public static final  String  IF_FILTER_SPECIFIC_TIME_FILE_NAME= "ifFilterSpecificTimeFileName";
	//过滤出特定时间点文件名的字符串
	public static final  String  FILTER_SPECIFIC_TIME_FILE_NAME= "filterSpecificTimeFileName";
	//是否过滤特定小时文件名自增
	public static final  String  IF_FILTER_SPECIFIC_HOUR_INCREMENT= "ifFilterSpecificHourIncrement";
	//采集特定时间点的文件后，时间自增的数值（1、-1）
	public static final  String  INCREMENT_HOUR_VALUE= "incrementHourValue";
	//时间点格式
	public static final String FORMAT_0F_HOUR = "yyyyMMdd_HH";
	//是否采用SFTP连接
	public static final String IF_CONNECT_BY_SFTP = "ifConnectBySftp";
	//sftp channel
	public static final String SFTP_CHANNEL = "sftp";
	//逗号分隔符
	public static final String COMMA_SPLIT_STRING = ",";
	
	//是否将源数据划分成几份进行采集
	public static final String IF_COLLECT_BY_PARTITION = "ifCollectByPartition";
	//将源数据划分成几份
	public static final String PARTITIONS = "partitions";
	//本采集程序采集第几份数据
	public static final String COLLECT_PARTION = "collectPartion";
	//根据那个位置进行划分源数据
	public static final String LOCATION_TO_PARTITION = "locationToPartition";
	
    
}
