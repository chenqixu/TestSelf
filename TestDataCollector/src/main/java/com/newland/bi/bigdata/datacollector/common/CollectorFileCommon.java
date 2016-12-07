package com.newland.bi.bigdata.datacollector.common;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;
import com.newland.bi.bigdata.datacollector.filter.DataColectorFTPFileFilter;
import com.newland.bi.bigdata.datacollector.thread.FtpDownloadFileThread;




public class CollectorFileCommon {
	//日志记录器
	private static Logger logger = Logger.getLogger(CollectorFileCommon.class);
	/**
	 * 
	 * @description: 删除totalThreadList中所有的线程
	 * （循环删除List中工作完成的线程，直到所有的线程工作完，那么List中所有的线程也就清除完）
	 * @author:xixg
	 * @date:2013-11-23
	 * @param inFileNameMap map中存放的文件名
	 * @return boolean  当返回true是，则List中所有的线程都删除完
	 */
	 public boolean clearTotalThreadList(List<Thread> totalThreadList){
		 	boolean  exitFlag = false;
	    	//退出前List里需要清除的线程（线程工作完毕且队列中没有要上传的文件，则List中的线程要清除）
	    	List<Thread>  deleteList = new ArrayList<Thread>();
	    	try {
	    		//当totalThreadList中有线程时，重复执行循环，直到把totalThreadList所有的线程清除
				while(totalThreadList.size()>0){
					//循环取出totalThreadList中的线程
					for(Thread itemT : totalThreadList){
						//判断线程是否还在工作
						if(!itemT.isAlive()){
							//把工作完毕的线程放入删除List中
							deleteList.add(itemT);
						} 
					}
					//删除工作完成的线程
					totalThreadList.removeAll(deleteList);
					//删除线程休眠2000毫秒
					Thread.sleep(2000);
				}
				exitFlag = true;
			} catch (InterruptedException e) {
				logger.error("%%%%%清理线程列表，把不活动的线程删除出错！！",e);
			}
			return exitFlag;
	    }
	 
	/** 
	 * 
	 * @description:清理线程列表，把不活动的线程移动
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void clearThreadList(List<Thread> sourceThreadList){
    	try {
			//需要删除的线程列表
			List<Thread> needDelThreadList = new ArrayList<Thread>();
			//循环取出线程
			for(Thread t:sourceThreadList){
				//如果线程是非活动的，则放入待删除的List中
				if(!t.isAlive()) needDelThreadList.add(t);
			}
			sourceThreadList.removeAll(needDelThreadList);
		} catch (Exception e) {
			logger.error("%%%%%清除全局变量threadList中的线程出错！！", e);
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
    public static List<String> listFtpFiles(FTPClient ftpClient,String remoteFilePath,FTPFileFilter fileFilter) {
    	List<String> fileList = new ArrayList<String>();
        try {
        	//改变FTP的工作目录
        	ftpClient.changeWorkingDirectory(remoteFilePath);
            
            //描述指定目录，取出指定后缀名的文件，放入数组中
            FTPFile[] fileArrays = ftpClient.listFiles(remoteFilePath,fileFilter);
            //把数组转为List
            for (int i = 0; i < fileArrays.length; i++) {
            	//转换成List
                if (fileArrays[i].isFile()) {
                	//取出文件名
                    String fileName = fileArrays[i].getName().toString();
                    fileName = new String(fileName.getBytes(), ftpClient.getControlEncoding());
                    //把文件名放入List中
                    fileList.add(fileName);
                }
            }
            
        }catch (Exception e) {
        	logger.error("%%%%%列出FTP服务器上目录："+remoteFilePath+"的文件时出错！！", e);
        }
        return fileList;
    }
    
    /**
	 * 
	 * @description:公用方法  采集程序退出前做的工作
	 * @author:xixg
	 * @date:2014-03-26
	 */ 
	public static void exitHandle() {
		try{
			synchronized (InitCollectorFile.dataCollectorThreadList) {
				//采集程序退出前，等待正在工作的线程完成
				while(InitCollectorFile.dataCollectorThreadList.size()>0){
					logger.info("#####采集程序退出前等待正在工作的线程完成，剩余采集线程数为:"+InitCollectorFile.dataCollectorThreadList.size());
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
			}
		}catch(Exception e){
			logger.error("%%%%%采集程序退出异常！！！",e);
		}
	}
	
	
	/**
     * 
     * @description:  采集文件总方法
     * @author:xixg
     * @date:2014-02-13
     */
	public static void CollectorDirsFtpFileService(){
		try {
			//存放采集错误文件的Map每隔2个小时清空一次
			long nowTime = System.currentTimeMillis();
			if(nowTime - InitCollectorFile.clearTime > 7200000){
				InitCollectorFile.errFileMap.clear();
				InitCollectorFile.clearTime = nowTime;
			}
			
			//创建FTP连接，进行FTP扫描目录
			FTPClient ftpClient = getFtpConnect();
			int sourceDataPathAraayLength = InitCollectorFile.sourceDataPathAraay.length;
			//文件过滤器
            DataColectorFTPFileFilter fileFilter = new DataColectorFTPFileFilter(InitCollectorFile.currFilterSpecificFileName);
			//循环下载FTP服务器上的多个目录
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
				CollectorFtpOneDirFileService(ftpClient,InitCollectorFile.sourceDataPathAraay[i],fileFilter,
						InitCollectorFile.ftpThreadNumEveryDirArray[i],InitCollectorFile.mvSourceCtlFilePathArray[i],InitCollectorFile.mvSourceDataFilePathArray[i]);
			}
			 //扫描目录有文件时的次数
	        InitCollectorFile.currScanDirHasFile++;
	        //关闭扫描目录的FTP连接
	        CollectorFileCommon.closeFtpConnect(ftpClient);
		} catch(Exception e){
			logger.error("%%%%%采集多个目录文件的业务方法出现异常!!!!",e);
		}
	}
	
	
	 /**
     * 
     * @description:  采集文件总方法
     * @author:xixg
     * @date:2014-02-13
     */
	public static void CollectorFtpOneDirFileService(FTPClient ftpClient,String sourceDataPathString,DataColectorFTPFileFilter fileFilter,
			String ftpThreadNumStr,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		try {
			//如果FTP连接为空，则重新创建FTP连接
			if(ftpClient == null ){
				//创建FTP连接，进行FTP扫描目录
				ftpClient = getFtpConnect();
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
				allHourFileList = CollectorFileCommon.listFtpFiles(ftpClient,sourceDataPathString,fileFilter);
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
				currHourFileList = CollectorFileCommon.listFtpFiles(ftpClient,sourceDataPathString,fileFilter);
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
			int fq = fileNameQueue.size();
			boolean fqf = false;
			if(fq!=currHourFileTotal) {
				fqf = true;
				logger.error("#####正在采集时间点："+firstFileDate+"的文件，本时间点的增加到队列中的文件总数为："+fq+"，和原先扫描的"+currHourFileTotal+"不一致："+fqf);
			}else{
				logger.info("#####正在采集时间点："+firstFileDate+"的文件，本时间点的增加到队列中的文件总数为："+fq+"，和原先扫描的"+currHourFileTotal+"一致："+fqf);
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
//				//当前线程等于最大线程时，程序进入睡眠休息清除不活动的线程
//				while(InitCollectorFile.dataCollectorThreadList.size() >= ftpThreadNum){
//					//清除不活动的线程
//					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
//					Thread.sleep(1000);
//				}
				//创建FTP连接，一个线程使用一个FTP连接
				FTPClient ftpClientForThread = getFtpConnect();
				//创建FTP连接后，改变工作目录到要下载的源数据目录
				ftpClientForThread.changeWorkingDirectory(sourceDataPathString);
		        FtpDownloadFileThread downloadFileThread = new FtpDownloadFileThread(fileNameQueue,ftpClientForThread,sourceDataPathString,mvSourceCtlFilePath,mvSourceDataFilePath);
		        InitCollectorFile.dataCollectorThreadList.add(downloadFileThread);
		        downloadFileThread.start();
			}
		} catch (Exception e) {
			logger.error("%%%%%采集FTP服务器目录："+sourceDataPathString+"业务出错！！",e);
		}
	}
	
	 
	
	/** 
	 * 
	 * @description:清理线程列表，把不活动的线程移除
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void clearNoAliveThread(List<Thread> sourceThreadList){
    	try {
			//需要删除的线程列表
			List<Thread> needDelThreadList = new ArrayList<Thread>();
			//循环取出线程
			for(Thread t:sourceThreadList){
				//如果线程是非活动的，则放入待删除的List中
				if(!t.isAlive()) needDelThreadList.add(t);
			}
			sourceThreadList.removeAll(needDelThreadList);
		} catch (Exception e) {
			logger.error("%%%%%清理线程列表，把不活动的线程删除出错！！",e);
		}
    }
    
    /** 
	 * 
	 * @description:公用方法，创建FTP连接
	 * @author:xixg
	 * @date:2014-01-18
	 * @return FTPClient
	 */
    public static FTPClient getFtpConnect(){
    	FTPClient ftpClient = null;
    	try {
    		ftpClient = new FTPClient();
    		ftpClient.connect(CollectorConfInfo.ftpServerIp, CollectorConfInfo.ftpServerPort);
			ftpClient.login(CollectorConfInfo.ftpServerUser, CollectorConfInfo.ftpServerPassword);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setDataTimeout(300000);
//	        logger.info("#####成功：连接FTP服务器："+CollectorConfInfo.ftpServerIp+"，端口号："+CollectorConfInfo.ftpServerPort+"，FTP用户名："
//					+CollectorConfInfo.ftpServerUser+"，FTP密码："+CollectorConfInfo.ftpServerPassword);
		} catch (Exception e) {
			logger.error("%%%%%失败连接FTP服务器："+CollectorConfInfo.ftpServerIp+"，端口号："+CollectorConfInfo.ftpServerPort+"，FTP用户名："
					+CollectorConfInfo.ftpServerUser+"，FTP密码："+CollectorConfInfo.ftpServerPassword,e);
		}
		return ftpClient;
    }
    
    /** 
	 * 
	 * @description:公用方法，关闭FTP连接
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void closeFtpConnect(FTPClient ftpClient){
    	try {
			if(ftpClient != null && ftpClient.isConnected()){
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			logger.error("%%%%%关闭FTP连接出错！！！", e);
		}
    }
    
    
    
    /**
	 * 
	 * @description:公用方法  根据文件名，获取文件所属时间点，如下文件名，返回时间点：20131218_10
	 * 文件名实例：
	 * 		GN口数据：GnC64_http_dnssession_60_20131218_105600_20131218_105659.csv	时间点：20131218_10
	 * 		MC口数据：FuJianYiDong-A-IuCS-4-201408061520.txt  时间点为20140806-15
	 * @author:xixg
	 * @date:2014-03-26
	 * @param fileName 文件名
	 * @return String 时间点
	 */
	public static String getTimeByFileName(String fileName){
		String retStr = "";
		StringBuffer sb = new StringBuffer();
		try {
			//对源文件名以"_"分隔，取出时间串
			String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
			//取文件名日期，如：20131218
			String fileNameDate = fileNameArray[CollectorConfInfo.dateLocationAtFileName];
			//截取需要的时间字符串
			if(fileNameDate != null && fileNameDate.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDate = fileNameDate.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//取文件名小时与分钟，如：105600
			String fileNameHourTime = fileNameArray[CollectorConfInfo.hourLocationAtFileName];
			//截取需要的小时字符串
			if(fileNameHourTime != null && fileNameHourTime.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTime = fileNameHourTime.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sb.append(fileNameDate);
			sb.append(CollectorConfInfo.splitFileNameForDateTime);
			sb.append(fileNameHourTime);
			retStr = sb.toString();
		} catch (Exception e) {
			logger.error("%%%%%根据文件名："+fileName+" 获取时间点出错！！！",e);
		}
		return retStr;
	}
	
	/**
	 * 
	 * @description:公用方法  从List中过滤出同一时间点的CSV文件
	 * @author:xixg
	 * @date:2014-03-26
	 * @param hisPathCtlFileList 历史目录中的CTL文件
	 * @param firstFileName 最早时间点的文件
	 * @return List 
	 */    
	public static List<String> getSameTimeCsvFilesByList(List<String> allHourFileList,String inFirstFileNameDate,boolean ifDateAndHourTheSameLocation) {
		String firstFileNameDate = "";
		//与最早时间相同时间点的文件list
		List<String> sameTimeList = new ArrayList<String>();
		if(allHourFileList==null || allHourFileList.size()==0 || inFirstFileNameDate == null || "".equals(inFirstFileNameDate)) return allHourFileList;
		firstFileNameDate = inFirstFileNameDate;
		try {
			//配置文件中配置的源数据文件名中的日期与小时是否在同一个位置，如果在同一个位置，则对时间点格式要进行转化
			//例如 MC接口文件true：FuJianYiDong-A-IuCS-4-201408061520.txt   过滤同一时间点的文件时，时间点格式要与文件名一致
			if(ifDateAndHourTheSameLocation){
				//转化文件名时间点格式，例如从20140324_11转为2014032411，为后面文件名过滤时间点准备
				firstFileNameDate = formatFileNameDate(firstFileNameDate);
			}
			//循环取出List中的文件名
			for(int i=0;i<allHourFileList.size();i++){
				String fileName = allHourFileList.get(i);
				//如果文件名中不包括时间点，则从List中删除
				if(fileName.contains(firstFileNameDate)) {
					//把过滤出的同一个时间点的CSV文件放入List
					sameTimeList.add(fileName);
				}
			}
		} catch (Exception e) {
			logger.error("%%%%%从List中过滤出同一时间点："+firstFileNameDate+" 的所有文件出错！！！",e);
		}   
	    return sameTimeList;
	}
	
	
	 /**
	 * 
	 * @description: 转化文件名时间点格式，例如从20140324_11转为2014032411
	 * @author:xixg
	 * @date:2013-11-23
	 * @param fileNameDate 传入的文件名时间格式
	 * @return String  返回需要的文件名格式
	 */
	 public static String formatFileNameDate(String fileNameDate){
		//文件名时间格式2014032411
		String timeOfFileName = "";
    	try {
		    if(fileNameDate!=null && !"".equals(fileNameDate)){
		    	String[] timeOfFileNameArray = fileNameDate.split(CollectorConfInfo.splitFileNameForDateTime);
		    	//文件名时间点20140324_11转为2014032411
		    	if(timeOfFileNameArray.length>1){
		    		timeOfFileName = timeOfFileNameArray[0]+timeOfFileNameArray[1];
		    	}
		    }
		} catch (Exception e) {
			logger.error("#####转化文件名时间点格式出错，例如从20140324_11转为2014032411，传为的文件名时间点字符串为："+fileNameDate,e);
		}
		return timeOfFileName;
	 }
	 
	
	 
	 /**
	 * 
	 * @description: 下载一个文件方法
	 * @author:xixg
	 * @date:2013-11-23
	 * @param fileNameDate 传入的文件名时间格式
	 * @return String  返回需要的文件名格式
	 * @throws SocketException 
	 * @throws FTPConnectionClosedException 
	 */
	 public static boolean ftpDownloadOneFile (FTPClient ftpClient,String fileName,String threadName,String savePath) throws  SocketException, FTPConnectionClosedException{
		 boolean downloadFlag = false;
		 OutputStream outputStream = null;
		 InputStream inputStream = null;
		 try {
			//下载文件的开始时间
			long startTime = System.currentTimeMillis();
			//中间文件全名
			String tmpFileName = savePath  + fileName + CollectorConfInfo.ftpTmpFileNameSuffix;
			//实例化文件实例
			File tmpFile = new File(tmpFileName);
			//实例化文件输出流
			outputStream = new FileOutputStream(tmpFile);
			//FTP下载
			inputStream = ftpClient.retrieveFileStream(fileName);
    		if(inputStream == null){
    			logger.error("%%%%%线程："+threadName+"获取FTP文件"+fileName+"的流为空，下载失败！");
    			if(tmpFile.exists()){
    				tmpFile.delete();
    			}
    			
    			if(CollectorConfInfo.ifHasCtlSourceFile){
	    			if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)){
	    				if(!InitCollectorFile.errFileMap.containsKey(fileName)){
	    					InitCollectorFile.errFileMap.put(fileName, 1);
	    				}else{
	    					InitCollectorFile.errFileMap.put(fileName, InitCollectorFile.errFileMap.get(fileName)+1);
	    					if(InitCollectorFile.errFileMap.get(fileName) > 3){
	    						logger.info("%%%%%扫描三次发现数据文件" + fileName + "不存在，删除对应的检验文件");
	    	    				String ctlFileName = fileName.replace(CollectorConfInfo.dataSourceFileSuffixName,CollectorConfInfo.ctlSourceFileSuffixName);
	    	    				ftpClient.deleteFile(ctlFileName);
	    					}
	    				}
	    			}
    			}
    			return false;
    		}
    		//设置FTP下载缓冲区
    		byte[] buffer = new byte[CollectorConfInfo.downloadBuffersize*1024*1024];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
            	//本地流写文件
            	outputStream.write(buffer, 0, c);
            }
            ftpClient.getReply();
            //文件写完后，关闭输出流对象
            if(outputStream != null){
				outputStream.close();
			}
            //文件写完后，关闭输入流对象
			if(inputStream != null){
				inputStream.close();
			}
			//下载文件的结束时间
    		long endTime = System.currentTimeMillis();
			//下载文件总共用时
			long totalTime = endTime - startTime;
			logger.info("#####线程："+threadName+"下载文件:"+fileName+"完成,总耗时:"+totalTime);
			downloadFlag = true;
			//下载完成后的文件全名
			String finishFileName = savePath  + fileName;
			//实例化控制文件实例
			File finishFile = new File(finishFileName);
			//控制文件下载完成后去掉TMP后缀
			boolean renameFlag = tmpFile.renameTo(finishFile);
			if(renameFlag){
//				logger.info("#####线程："+threadName+"成功地去除文件:"+tmpFileName+"的后缀名："+CollectorConfInfo.ftpTmpFileNameSuffix);
			}else{
//				logger.info("#####线程："+threadName+"失败地去除文件:"+tmpFileName+"的后缀名："+CollectorConfInfo.ftpTmpFileNameSuffix);
			}
		 }catch(FTPConnectionClosedException e1){
			 throw e1;
		 }catch(SocketException e2){
			 throw e2;
		 }catch (Exception e) {
			 downloadFlag = false;
			logger.error("%%%%%线程："+threadName+"下载文件"+fileName+"出错！",e);
		 }
		 return downloadFlag;
	 }
	 
	 
	 /**
	 * 
	 * @description: 时间字符串自增小时
	 * @author:xixg
	 * @date:2013-11-23
	 * @param dateStr 时间串
	 * @return String 自增后的时间串
	 */
	 public static String incrementHourStr(String dateStr){
		//文件名时间格式2014032411
		String returnHourStr = "";
		try {
			//创建时间格式化实例
	       DateFormat format = new SimpleDateFormat(CollectorConstant.FORMAT_0F_HOUR);   
           Date date = format.parse(dateStr);   
           Calendar calendar = Calendar.getInstance();
           //设置传入的时间
           calendar.setTime(date);
           //自增小时
           calendar.add(calendar.HOUR, CollectorConfInfo.incrementHourValue);
           returnHourStr = format.format(calendar.getTime());
		} catch (Exception e) {
			logger.error("%%%%%时间："+dateStr+"自增"+CollectorConfInfo.incrementHourValue+"小时出错！",e);
		}
		return returnHourStr;
	 }
	 
	 /**
	 * 
	 * @description:公用方法  根据指定的时间格式 获得当前系统时间
	 * @author:xixg
	 * @date:2013-11-29
	 * @param dataFormat
	 * @return String
	 */
	public static String getCurrentDate(String dataFormat){
		String dataStr = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
			dataStr  = sdf.format(new Date());
		} catch (Exception e) {
			logger.error("%%%%%获取当前系统时间出错！！",e);
		}
		return dataStr ;
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
	 * @throws FTPConnectionClosedException 
	 * @throws SocketException 
	 */
	public static boolean mvOrDeleteFtpSourceDataFile(FTPClient ftpClient,String sourceDataPathString,String dataFileName,
			String mvSourceDataFilePath,String threadName) throws FTPConnectionClosedException, SocketException{
		boolean returnFlag = false;
		try {
			//读取配置文件中配置的是否删除源控制文件标识
			if(CollectorConfInfo.ifNeedDeleteSourceDataFile){
				//删除远程FTP服务器的控制文件
				boolean deleteDataFlag = ftpClient.deleteFile(dataFileName);
				if(deleteDataFlag ){
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地删除FTP源数据文件:"+dataFileName);
				}else{
					returnFlag = false; 
					logger.error("%%%%%线程："+threadName+"失败地删除FTP源数据文件:"+dataFileName);
				}
			}
			//读取配置文件中配置的是否移动源数据标识
			if(CollectorConfInfo.ifNeedMvSourceDataFile){
				//移动远程FTP服务器的控制文件到目的目录
				boolean renameDataFlag = ftpClient.rename(sourceDataPathString+dataFileName, mvSourceDataFilePath+dataFileName);
				if(renameDataFlag){
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地将数据文件:"+dataFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceDataFilePath);
				}else{
					returnFlag = false;
					logger.error("%%%%%线程："+threadName+"失败地将数据文件:"+dataFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceDataFilePath);
				}
			}
		}catch(FTPConnectionClosedException e1){
			 throw e1;
		 }catch(SocketException e2){
			 throw e2;
		 } catch (Exception e) {
			returnFlag = false;
			logger.error("%%%%%移动或者删除源控制文件方法出错！！",e);
		}
		return returnFlag ;
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
	public static boolean mvOrDeleteFtpSourceCtlFile(FTPClient ftpClient,String sourceDataPathString,String ctlFileName,
			String mvSourceCtlFilePath,String threadName){
		boolean returnFlag = false;
		try {
			//读取配置文件中配置的是否删除源控制文件标识
			if(CollectorConfInfo.ifNeedDeleteSourceCtlFile){
				//删除远程FTP服务器的控制文件
				boolean deleteCtlFlag = ftpClient.deleteFile(ctlFileName);
				if(deleteCtlFlag ){
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地删除FTP源控制文件:"+ctlFileName);
				}else{
					returnFlag = false;
					logger.error("%%%%%线程："+threadName+"失败地删除FTP源控制文件:"+ctlFileName);
				}
			}
			//读取配置文件中配置的是否移动源数据标识
			if(CollectorConfInfo.ifNeedMvSourceCtlFile){
				//移动远程FTP服务器的控制文件到目的目录
				boolean renameCtlFlag = ftpClient.rename(sourceDataPathString+ctlFileName, mvSourceCtlFilePath+ctlFileName);
				if(renameCtlFlag){
					returnFlag = true;
					logger.info("#####线程："+threadName+"成功地将控制文件:"+ctlFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceCtlFilePath);
				}else{
					returnFlag = false;
					logger.error("%%%%%线程："+threadName+"失败地将控制文件:"+ctlFileName+"从目录："+sourceDataPathString+"移动到目录："+mvSourceCtlFilePath);
				}
			}
		} catch (Exception e) {
			returnFlag = false;
			logger.error("%%%%%移动或者删除FTP控制文件方法出错！！",e);
		}
		return returnFlag ;
	}
	/**
	 * @description:将本地文件从一个路径移动到另一个路径
	 * @param fromPath：文件原来的路径
	 * @param ToPath：文件需要移动到的目的路径
	 * @param fileName：文件名
	 */
	public static void mvFile(String fromPath,String toPath, String fileName) {
		
		File fromFile = new File(fromPath + fileName);
		File toFile = new File(toPath + fileName);
		
		boolean mvFlag = fromFile.renameTo(toFile);
		if(mvFlag){
			logger.info("成功地将文件：" + fileName + "从" + fromPath + "移动到路径"  + toPath);
		}else{
			logger.info("失败地将文件：" + fileName + "从" + fromPath + "移动到路径"  + toPath);
		}
		
	}
	
}





