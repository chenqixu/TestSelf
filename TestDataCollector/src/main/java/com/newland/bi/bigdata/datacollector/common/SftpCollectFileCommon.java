package com.newland.bi.bigdata.datacollector.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;
import com.newland.bi.bigdata.datacollector.entity.SftpConnection;
import com.newland.bi.bigdata.datacollector.thread.FtpDownloadFileThread;
import com.newland.bi.bigdata.datacollector.thread.SftpDownloadFileThread;

public class SftpCollectFileCommon {
	//日志记录器
	private static Logger logger = Logger.getLogger(SftpCollectFileCommon.class);
	/** 
	 * 
	 * @description:公用方法，创建SFTP连接
	 * @author:xixg
	 * @date:2014-01-18
	 * @return SftpConnection sftp连接实体类
	 */
    public static SftpConnection getSftpConnection(){
    	SftpConnection sftpConnection = new SftpConnection();
    	try {
    		JSch jsch = new JSch();
    		jsch.getSession(CollectorConfInfo.ftpServerUser, CollectorConfInfo.ftpServerIp, CollectorConfInfo.ftpServerPort);
    		Session sshSession = jsch.getSession(CollectorConfInfo.ftpServerUser, CollectorConfInfo.ftpServerIp, CollectorConfInfo.ftpServerPort);
    		sshSession.setPassword(CollectorConfInfo.ftpServerPassword);
    		Properties sshConfig = new Properties();
    		sshConfig.put("StrictHostKeyChecking", "no");
    		sshSession.setConfig(sshConfig);
    		sshSession.connect();
    		Channel channel = sshSession.openChannel(CollectorConstant.SFTP_CHANNEL);
    		channel.connect();
    		ChannelSftp channelSftp = (ChannelSftp) channel;
    		sftpConnection.setChannelSftp(channelSftp);
    		sftpConnection.setSshSession(sshSession);
		} catch (Exception e) {
			logger.error("%%%%%失败连接SFTP服务器："+CollectorConfInfo.ftpServerIp+"，端口号："+CollectorConfInfo.ftpServerPort+"，SFTP用户名："
					+CollectorConfInfo.ftpServerUser+"，SFTP密码："+CollectorConfInfo.ftpServerPassword,e);
		}
		return sftpConnection;
    }
    
    /** 
	 * 
	 * @description:公用方法，关闭SFTP连接
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void closeSftpConnection(SftpConnection sftpConnection){
    	try {
    		ChannelSftp channelSftp = sftpConnection.getChannelSftp();
    		if(channelSftp != null){
    			channelSftp.disconnect();
    		}
    		Session sshSession = sftpConnection.getSshSession();
    		if(sshSession != null){
    			sshSession.disconnect();
    		}
		} catch (Exception e) {
			logger.error("%%%%%关闭SFTP连接出错！！！", e);
		}
    }
    
    /**
     * 
     * @description:  采集文件总方法
     * @author:xixg
     * @date:2014-02-13
     */
	public static void sftpCollectDirsFileService(){
		try {
			//创建SFTP连接，进行SFTP扫描目录
			SftpConnection sftpConnection = getSftpConnection();
			//需要采集的源数据目录个数
			int sourceDataPathAraayLength = InitCollectorFile.sourceDataPathAraay.length;
			//循环下载SFTP服务器上的多个目录
			for(int i=0;i<sourceDataPathAraayLength;i++){
				//文件采集的时候检测是否要退出
				//实例化退出检测文件
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//检查全局变量程序退出标识是否为真，如为真，则程序退出
				if(InitCollectorFile.ifNeedExitFlag 
						//退出文件存在，则执行退出处理
						||f.exists()){
					//退出文件存在,执行退出事件
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，采集程序退出>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，全局变量退出标志为真，采集程序退出>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//调用采集一个目录文件的业务方法
				sftpCollectOneDirFileService(sftpConnection,InitCollectorFile.sourceDataPathAraay[i],InitCollectorFile.ftpThreadNumEveryDirArray[i],
						InitCollectorFile.mvSourceCtlFilePathArray[i],InitCollectorFile.mvSourceDataFilePathArray[i]);
			}
			 //扫描目录有文件时的次数
	        InitCollectorFile.currScanDirHasFile++;
	        //关闭扫描目录的SFTP连接
	        closeSftpConnection(sftpConnection);
		} catch(Exception e){
			logger.error("%%%%%采集多个目录文件的业务方法出现异常!!!!",e);
		}
	}
	
	 /**
     * 
     * @description:  列出FTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     * @param remoteFilePath 远端文件路径
     * @return List<String>
     */
    public static List<String> sftpListFiles(SftpConnection sftpConnection,String remoteFilePath) {
    	List<String> fileList = new ArrayList<String>();
        try {
        	ChannelSftp channelSftp = sftpConnection.getChannelSftp();
        	//改变SFTP的工作目录
        	channelSftp.cd(remoteFilePath);
        	//列出当前目录的所有文件，存放在Vector中
        	Vector fileVector = channelSftp.ls(remoteFilePath);
        	//迭代Vector
        	Iterator it = fileVector.iterator(); 
        	//循环取出Vector中的文件名
        	while(it.hasNext()) 
        	{ 
        		//取出文件名
	            String fileName = ((LsEntry)it.next()).getFilename(); 
	            //过滤出符合要求的文件名
	            if(filterFileName(fileName,InitCollectorFile.currFilterSpecificFileName)){
	            	fileList.add(fileName);
	            }
        	}
        }catch (Exception e) {
        	logger.error("%%%%%列出SFTP服务器上目录："+remoteFilePath+"的文件时出错！！", e);
        }
        return fileList;
    }
    
    /**
     * 
     * @description:  过滤出SFTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     * @param remoteFilePath 远端文件路径
     * @return boolean 过滤后是否符合要求
     */
    public static boolean filterFileName(String fileName,String fileNameInclude){

		boolean returnFlag = false;
		try {
			//是否排除指定文件名的文件
			if(CollectorConfInfo.ifExcludeSpecificFileName){
				//排除指定文件名的文件
				if(fileName.indexOf(CollectorConfInfo.excludeSpecificFileName) > -1) return false;
			}
			//是否过滤出文件名包含特定字符串
			if(CollectorConfInfo.ifFilterSpecificFileName){
				//过滤出文件名包含特定字符串
				if(fileName.indexOf(CollectorConfInfo.filterSpecificFileName) < 0) return false;
			}
			//如果需要过滤特定文件名
			if(CollectorConfInfo.ifFilterSpecificTimeFileName
					//过滤特定文件名不为空
					&& fileNameInclude != null && !"".equals(fileNameInclude)
					//不包含特定文件名的去除
					&& fileName.indexOf(fileNameInclude) < 0) return false;
			//源文件是否有控制文件
			if(CollectorConfInfo.ifHasCtlSourceFile ){
				if(CollectorConfInfo.ifDownloadCtlFile){
					//如果需要先下载控制文件
					if(CollectorConfInfo.ifFirstDownloadCtlFile){
						//滤出控制文件后缀名为CTL的文件
						if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
					}else{
						//滤出数据文件后缀名为配置文件配置的值的文件
						if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
					}
				}else{
					//滤出控制文件后缀名为CTL的文件
					if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
				}
			}else{
				//滤出数据文件后缀名为配置文件配置的值的文件
				if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
			}
		} catch (Exception e) {
			logger.info("%%%%%SFTP过滤文件名出错！！！",e);
		}
		return returnFlag;
	
    }
    
	
	 /**
     * 
     * @description:  采集文件总方法
     * @author:xixg
     * @date:2014-02-13
     */
	public static void sftpCollectOneDirFileService(SftpConnection sftpConnection,String sourceDataPathString,
			String ftpThreadNumStr,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		try {
			//如果SFTP连接为空，则重新创建FTP连接
			if(sftpConnection == null ){
				//创建SFTP连接，进行FTP扫描目录
				sftpConnection = getSftpConnection();
			}
			//所有时间点的文件List
			List<String> allHourFileList = null;
			int allHourFileListSize = 0;
			//当前小时要下载的所有文件
			List<String> currHourFileList = null;
			int currHourFileTotal = 0;
			//当前目录分配的下载线程数
			int ftpThreadNum = Integer.parseInt(ftpThreadNumStr);
			//最早时间点
			String firstFileDate = "";
			//采集文件是否按照文件名的时间顺序
			if(CollectorConfInfo.ifSortByFileName){
				//列出指定目录下的所有文件
				allHourFileList = sftpListFiles(sftpConnection,sourceDataPathString);
				allHourFileListSize = allHourFileList.size();
				//如果目录中的文件为零，程序休眠一定时间后则进行下一次循环描述目录
		        if(allHourFileListSize == 0) {
		        	logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次扫描源数据目录："+sourceDataPathString+"时没有文件，程序进行下一次的目录扫描>>>>>>>>>>");
		        	Thread.sleep(2000);
		        	return ;
		        }
		        //对ctlFileList中的文件按时间进行排序GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv
		        Collections.sort(allHourFileList, new TimeOfFileComparator());
		        String firstFileName = "";
		        //是否采集最新时间点的文件
				if(CollectorConfInfo.ifDownloadNewestFile){
					//取排序后的最后一个文件，则为最新时间点的文件
					firstFileName = allHourFileList.get(allHourFileListSize-1);
				}else{
					//文件名排序后取出最早时间的文件
			        firstFileName = allHourFileList.get(0);
				}
				//获取文件名中的时间点
		        //例如GN口数据：GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv  时间点为20131218_10
		        //例如MC口数据：FuJianYiDong-A-IuCS-4-201408061520.txt  时间点为20140806-15
				firstFileDate = CollectorFileCommon.getTimeByFileName(firstFileName);
		        logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次扫描目录："+sourceDataPathString+"时有文件，所有时间点的文件总数为："+allHourFileListSize+",文件排序后最早（或最新）时间点为："+firstFileDate);
		        //获取目录中与所需时间相同时间点的所有CSV文件
		        currHourFileList = CollectorFileCommon.getSameTimeCsvFilesByList(allHourFileList, firstFileDate,CollectorConfInfo.ifDateAndHourTheSameLocation);
			}else{//采集文件不按照文件名的时间顺序，采集指定时间的文件
				if(0 == InitCollectorFile.currScanDirHasFile){//如果程序是第一次描述目录，则过滤出指定时间点的文件为配置文件的初始值
					InitCollectorFile.currFilterSpecificFileName = CollectorConfInfo.filterSpecificTimeFileName;
				}else{//如果程序是不是第一次描述目录，则过滤出指定时间点要自增
					String currSystemDate = CollectorFileCommon.getCurrentDate(CollectorConstant.FORMAT_0F_HOUR);
					//如果全局变量中的当前时间小于系统时间点，则时间点小时自增
					if(InitCollectorFile.currFilterSpecificFileName.compareTo(currSystemDate) < 0){
						//小时自增
						InitCollectorFile.currFilterSpecificFileName = CollectorFileCommon.incrementHourStr(InitCollectorFile.currFilterSpecificFileName);
					}
				}
				firstFileDate = InitCollectorFile.currFilterSpecificFileName;
				//文件过滤器
//	            DataColectorFTPFileFilter fileFilter = new DataColectorFTPFileFilter(InitCollectorFile.currFilterSpecificFileName);
				//列出指定目录下的所有文件
				currHourFileList = sftpListFiles(sftpConnection,sourceDataPathString);
				currHourFileTotal = currHourFileList.size();
				//如果目录中的文件为零，程序休眠一定时间后则进行下一次循环描述目录
		        if(currHourFileTotal == 0) {
		        	logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次扫描源数据目录："+sourceDataPathString+"时没有文件，程序进行下一次的目录扫描>>>>>>>>>>");
		        	Thread.sleep(2000);
		        	return ;
		        }
			}
			currHourFileTotal = currHourFileList.size();
	        logger.info("#####正在采集时间点："+firstFileDate+"的文件，本时间点的文件总数为："+currHourFileTotal);
	        
	        //某个目录存放文件名的队列
	        Queue<String> fileNameQueue = null;
	        //如果全局变量Map中存在此目录对应的文件名队列，则取出本对象
	        if(InitCollectorFile.fileNameQueueMap.containsKey(sourceDataPathString)){
	        	//取出本目录的文件名队列
	        	fileNameQueue = InitCollectorFile.fileNameQueueMap.get(sourceDataPathString);
	        }else{//如果不存在本目录对应的文件名队列，则创建此队列
	        	fileNameQueue = new ConcurrentLinkedQueue<String>();
	        	//把新创建的文件名队列放入全局变量map中
	        	InitCollectorFile.fileNameQueueMap.put(sourceDataPathString, fileNameQueue);
	        }
			//循环取出currCtlFileTotal中文件放入线程进行采集
			for (int i = 0; i < currHourFileTotal; i++) {
				//文件采集的时候检测是否要退出
				//实例化退出检测文件
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//检查全局变量程序退出标识是否为真，如为真，则程序退出
				if(InitCollectorFile.ifNeedExitFlag 
						//退出文件存在，则执行退出处理
						||f.exists()){
					//退出文件存在,执行退出事件
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，采集程序退出>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，全局变量退出标志为真，采集程序退出>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//循环取出文件放入全局的安全队列中
				String fileName = currHourFileList.get(i);
				fileNameQueue.offer(fileName);
			}
			//根据配置文件配置的FTP线程数，创建相应的FTP线程进行下载文件
			for(int i=0;i<ftpThreadNum;i++){
				//文件采集的时候检测是否要退出
				//实例化退出检测文件
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//检查全局变量程序退出标识是否为真，如为真，则程序退出
				if(InitCollectorFile.ifNeedExitFlag 
						//退出文件存在，则执行退出处理
						||f.exists()){
					//退出文件存在,执行退出事件
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，采集程序退出>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####采集程序第"+InitCollectorFile.currCycleNum+"次循环扫描目录时，全局变量退出标志为真，采集程序退出>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//当前线程等于最大线程时，程序进入睡眠休息清除不活动的线程
				while(InitCollectorFile.dataCollectorThreadList.size() >= ftpThreadNum){
					//清除不活动的线程
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
				//创建SFTP连接，进行FTP扫描目录
				SftpConnection sftpConnectionThread = getSftpConnection();
				SftpDownloadFileThread downloadFileThread = new SftpDownloadFileThread(fileNameQueue,sftpConnectionThread,sourceDataPathString,mvSourceCtlFilePath,mvSourceDataFilePath);
		        InitCollectorFile.dataCollectorThreadList.add(downloadFileThread);
		        downloadFileThread.start();
			}
		} catch (Exception e) {
			logger.error("%%%%%采集SFTP服务器目录："+sourceDataPathString+"业务出错！！",e);
		}
	}
	
	 /**
	 * 
	 * @description: 公用方法，以SFTP方式下载文件
	 * @author:xixg
	 * @date:2013-11-23
	 * @param directory 下载目录
	 * @param downloadFile 下载的文件
	 * @param saveFile 存在本地的路径
	 * @param sftp
	 * @return String  返回需要的文件名格式
	 */
	 public static void sftpDownloadOneFile(ChannelSftp channelSftp,String sourceDirectory, String downloadFile, String threadName,String savePath){
		 OutputStream outputStream = null;
    	try {
    		channelSftp.cd(sourceDirectory);
    		//下载文件的开始时间
			long startTime = System.currentTimeMillis();
			//中间文件全名
			String tmpFileName = savePath  + downloadFile + CollectorConfInfo.ftpTmpFileNameSuffix;
			//实例化文件实例
			File tmpFile = new File(tmpFileName);
			//实例化文件输出流
			outputStream = new FileOutputStream(tmpFile);
    		channelSftp.get(downloadFile, outputStream);
    		//文件写完后，关闭输出流对象
            if(outputStream != null){
            	outputStream.flush();
				outputStream.close();
			}
            //下载文件的结束时间
    		long endTime = System.currentTimeMillis();
			//下载文件总共用时
			long totalTime = endTime - startTime;
			logger.info("#####线程："+threadName+"下载文件:"+downloadFile+"完成,总耗时:"+totalTime);
			//下载完成后的文件全名
			String finishFileName = savePath  + downloadFile;
			//实例化文件实例
			File finishFile = new File(finishFileName);
			//控制文件下载完成后去掉TMP后缀
			tmpFile.renameTo(finishFile);
//			System.out.println("#####线程："+threadName+"rename over=");
		} catch (Exception e) {
			logger.error("#####SFTP下载文件："+sourceDirectory+downloadFile+"出错！！！",e);
		} finally{
			//文件写完后，关闭输出流对象
            if(outputStream != null){
            	try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 }
	 
		/**
		 * 
		 * @description:公用方法  移动或者删除FTP控制文件方法
		 * @author:xixg
		 * @date:2013-11-29
		 * @param ftpClient FTP连接
		 * @param dataFileName 文件名
		 * @param threadName 线程名
		 * @return String
		 */
		public static boolean mvOrDeleteSftpSourceCtlFile(ChannelSftp channelSftp,String sourceDataPathString,String ctlFileName,
				String mvSourceCtlFilePath,String threadName){
			boolean returnFlag = false;
			try {
				//读取配置文件中配置的是否删除源控制文件标识
				if(CollectorConfInfo.ifNeedDeleteSourceCtlFile){
					//删除远程FTP服务器的控制文件
					channelSftp.rm(ctlFileName);
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地删除SFTP源控制文件:"+ctlFileName);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%线程："+threadName+"失败地删除SFTP源控制文件:"+ctlFileName);
			}
			try {
				//读取配置文件中配置的是否移动源数据标识
				if(CollectorConfInfo.ifNeedMvSourceCtlFile){
					//移动远程FTP服务器的控制文件到目的目录
					channelSftp.rename(sourceDataPathString+ctlFileName, mvSourceCtlFilePath+ctlFileName);
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地将控制文件:"+ctlFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceCtlFilePath);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%线程："+threadName+"失败地将控制文件:"+ctlFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceCtlFilePath);
			}
			return returnFlag ;
		}
		
		/**
		 * 
		 * @description:公用方法  移动或者删除源数据文件方法
		 * @author:xixg
		 * @date:2013-11-29
		 * @param ftpClient FTP连接
		 * @param dataFileName 文件名
		 * @param threadName 线程名
		 * @return String
		 */
		public static boolean mvOrDeleteSftpSourceDataFile(ChannelSftp channelSftp,String sourceDataPathString,String dataFileName,
				String mvSourceDataFilePath,String threadName){
			boolean returnFlag = false;
			try {
				//读取配置文件中配置的是否删除源控制文件标识
				if(CollectorConfInfo.ifNeedDeleteSourceDataFile){
					//删除远程FTP服务器的控制文件
					channelSftp.rm(dataFileName);
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地删除SFTP源数据文件:"+dataFileName);
				}
			} catch (Exception e) {
				returnFlag = false; 
				logger.error("%%%%%线程："+threadName+"失败地删除SFTP源数据文件:"+dataFileName);
			}
			try {			
				//读取配置文件中配置的是否移动源数据标识
				if(CollectorConfInfo.ifNeedMvSourceDataFile){
					//移动远程FTP服务器的控制文件到目的目录
					channelSftp.rename(sourceDataPathString+dataFileName, mvSourceDataFilePath+dataFileName);
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地将数据文件:"+dataFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceDataFilePath);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%线程："+threadName+"失败地将数据文件:"+dataFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceDataFilePath);
			
			}
			return returnFlag ;
		}
    public static void main(String[] args) {
//    	CollectorConfInfo.ftpServerUser = "app-dev";
//    	CollectorConfInfo.ftpServerIp = "10.1.4.53";
//    	CollectorConfInfo.ftpServerPort = 22;
//    	CollectorConfInfo.ftpServerPassword = "dev123";
//    	ChannelSftp channelSftp = getSftpConnect();
//    	listSftpFiles("/home/app-dev/gidata/",channelSftp);
	}

}
