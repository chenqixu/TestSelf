package com.newland.bi.bigdata.datacollector.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.common.CollectorConstant;

public class CollectorConfInfo {
	//日志记录器
	private static Logger logger = Logger.getLogger(CollectorConfInfo.class);
	//采集程序配置文件实例
	private static Configuration conf;
	//源数据服务器
	public static String ftpServerIp ;
	//源数据服务器FTP服务端口
	public static int ftpServerPort ;
	//源数据服务器FTP服务用户名
	public static String ftpServerUser ;
	//源数据服务器FTP服务密码
	public static String ftpServerPassword ;
	//源数据服务器的数据路径
	public static String sourceDataPath;
	//数据文件和控制文件是否分开存放
	public static boolean ifSaveDifferentPath;
	//数据存放的临时路径(Linux系统路径)
	public static String saveTmpPath ;
	//数据存放的本地路径(Linux系统路径)
	public static String saveDataPath ;
	//数据存放的本地路径(Linux系统路径)
	public static String saveCtlPath ;
	//数据源文件的后缀名
	public static String dataSourceFileSuffixName;
	//每个数据文件是否含有对应的控制文件
	public static boolean ifHasCtlSourceFile;
	//控制源文件的后缀名
	public static String ctlSourceFileSuffixName;
	//是否下载控制文件
	public static boolean ifDownloadCtlFile;
	//是否先下载控制文件
	public static boolean ifFirstDownloadCtlFile;
	//FTP下载文件的中间文件名后缀
	public static String ftpTmpFileNameSuffix;
	//FTP下载数据文件后，是否删除源文件
	public static boolean ifNeedDeleteSourceDataFile;
	//FTP下载控制文件后，是否移动源文件
	public static boolean ifNeedMvSourceDataFile;
	//采集程序下载完移动源文件到目的目录
	public static String mvSourceDataFilePath;
	//采集程序下载完一个文件后，控制源文件是否需要删除
	public static boolean ifNeedDeleteSourceCtlFile;
	//采集程序下载完一个文件后，控制源文件是否需要移动目录
	public static boolean ifNeedMvSourceCtlFile;
	//采集程序下载完控制文件后移动源文件到目的目录
	public static  String  mvSourceCtlFilePath;
	//采集程序退出检测文件
//	public static String exitFileFullName;
	//每个目录下载的线程数
	public static String ftpThreadNumEveryDir;
	//截取文件名时间点的分隔符
	public static String splitFileNameForDateTime ;
	//源数据文件名中的日期与小时是否在同一个位置
	public static boolean ifDateAndHourTheSameLocation;
	//文件名日期字符串在文件名中的位置
	public static int dateLocationAtFileName;
	//截取文件名日期字符串的开始位置 
	public static int  dateSubStringBegin;
	//截取文件名日期字符串的结束位置
	public static int  dateSubStringEnd;
	//文件名小时分钟秒字符串在文件名中的位置
	public static int  hourLocationAtFileName;
	//截取文件名小时字符串的开始位置
	public static int  hourSubStringBegin;
	//截取文件名小时字符串的结束位置
	public static int  hourSubStringEnd;
	//FTP下载文件缓冲区（单位M）
	public static int  downloadBuffersize;
	//是否按文件名时间排序
	public static  boolean  ifSortByFileName;
	//是否采集最新小时的文件
	public static   boolean  ifDownloadNewestFile;
	//是否过滤出特定时间点文件名
	public static  boolean  ifFilterSpecificTimeFileName;
	//过滤出特定时间点的文件名的字符串
	public static  String  filterSpecificTimeFileName;
	//是否过滤出特定文件名
	public static  boolean  ifFilterSpecificFileName;
	//过滤出特定文件名的字符串
	public static  String  filterSpecificFileName;
	//是否排除指定文件名的文件
	public static  boolean  ifExcludeSpecificFileName;
	//排除指定文件名的文件
	public static  String  excludeSpecificFileName;
	//是否过滤特定小时文件名自增
	public static  boolean  ifFilterSpecificHourIncrement;
	//采集特定时间点的文件后，时间自增的数值（1、-1）
	public static  int  incrementHourValue;
	//是否采用SFTP连接
	public static boolean ifConnectBySftp;
	//是否将源数据划分成几份进行采集
	public static  boolean ifCollectByPartition;
	//将源数据划分成几份
	public static  int partitions;
	//本采集程序采集第几份数据
	public static  int  collectPartion;
	//根据那个位置进行划分源数据
	public static  int locationToPartition;
	/**
	 * 
	 * @description: 读取配置文件中的值
	 * @author:xixg
	 * @date:2014-02-19
	 * @return void
	 */
	public static void getConfInfo(String collectorConfFilePath){
		try {
			//实例化采集程序配置文件实例
			Path collectorFilePath = new Path(collectorConfFilePath);
			conf = new Configuration();
			//加载采集程序自己的配置文件
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
			logger.error("%%%%%获取采集程序配置文件中的配置信息出错！！！",e);
		}
	}
	
	/**
	 * 
	 * @description:获取配置文件实例
	 * @author:xixg
	 * @date:2013-11-26
	 * @return Configuration
	 */
	public static Configuration getConfInstance() {
		return conf;
	}
}
