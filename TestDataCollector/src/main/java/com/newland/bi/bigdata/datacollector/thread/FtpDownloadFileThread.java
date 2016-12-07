package com.newland.bi.bigdata.datacollector.thread;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Queue;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.log4j.Logger;

import com.newland.bi.bigdata.datacollector.common.CollectorConstant;
import com.newland.bi.bigdata.datacollector.common.CollectorFileCommon;
import com.newland.bi.bigdata.datacollector.common.InitCollectorFile;
import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;

/**
 * 
 * @description:FTP下载文件线程
 * @author:xixg
 * @date:2014-02-19
 */
public class FtpDownloadFileThread extends Thread{
	//日志记录器
	private static Logger logger = Logger.getLogger(FtpDownloadFileThread.class);
	//线程安全队列，存放当前要下载的所有文件名
	private Queue<String> fileNameQueue;
	private String sourceDataPathString;
	//FTP客户端连接对象
	private FTPClient ftpClient;
	//队列中的文件名
	private String fileName ;
	//数据文件名
	private String dataFileName;
	//控制文件名
	private String ctlFileName;
	//控制文件下载是否成功标志
	private boolean ctlDownFlag;
	//数据文件下载是否成功标志
	private boolean dataDownFlag;
	//线程名
	private String threadName;
	private String mvSourceCtlFilePath;
	private String mvSourceDataFilePath;
	public FtpDownloadFileThread(Queue<String> fileNameQueue,FTPClient ftpClient,
			String sourceDataPathString,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		this.fileNameQueue = fileNameQueue;
		this.ftpClient = ftpClient;
		this.sourceDataPathString = sourceDataPathString;
		this.mvSourceCtlFilePath = mvSourceCtlFilePath;
		this.mvSourceDataFilePath = mvSourceDataFilePath;
	}
	/**
	 * 
	 * @description:下载线程具体运行的方法
	 * @author:xixg
	 * @date:2014-02-19
	 */
	@Override
	public void run() {
		//获取当前线程名称
		threadName = Thread.currentThread().getName();
		boolean flag = true;
		try {
			while(flag){
				synchronized (fileNameQueue) {
					if((fileName = fileNameQueue.poll()) != null){
					}else{
						flag = false;
						break;
					}
				}
				//如果有控制文件
				if(CollectorConfInfo.ifHasCtlSourceFile){
					ctlFileName = fileName;
					//是否下载控制文件
					if(CollectorConfInfo.ifDownloadCtlFile){
						//如果需要先下载控制文件
						if(CollectorConfInfo.ifFirstDownloadCtlFile){
							//下载控制文件
							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
							//数据文件全名，把控制文件的后缀名替换为数据文件的后缀名
							dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
							//下载数据文件
							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
							
							//下载成功后移动或者删除源控制文件和数据文件
							if(ctlDownFlag && dataDownFlag){
								//是否分开存放数据文件和校验文件
								if(CollectorConfInfo.ifSaveDifferentPath){
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}else{
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}
//								//移动或者删除源数据文件
//								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
							}
						}else{//先下载数据文件
							dataFileName = fileName;
							//下载数据文件
							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
							//控制文件全名，把数据文件的后缀名替换为控制文件的后缀名
							ctlFileName = dataFileName.replace(CollectorConfInfo.dataSourceFileSuffixName, CollectorConfInfo.ctlSourceFileSuffixName);
							//下载控制文件
							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
							//下载成功后移动或者删除源控制文件和数据文件
							if(ctlDownFlag && dataDownFlag ){
								//是否分开存放数据文件和校验文件
								if(CollectorConfInfo.ifSaveDifferentPath){
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}else{
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}
//								//移动或者删除源数据文件
//								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
							}
						}
					}else{//不下载控制文件、直接删除控制文件或者移动控制文件
//						//移动或者删除源控制文件
//						CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
						//数据文件全名，把控制文件的后缀名替换为数据文件的后缀名
						dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
						//下载数据文件
						dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
						if(dataDownFlag){
							CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//							CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
						}
					}
				}else{//没有控制文件，则只有数据文件
					dataFileName = fileName;
					//下载数据文件
					dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName,CollectorConfInfo.saveTmpPath);
					//移动或者删除源数据文件
					if(dataDownFlag){
						CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//						CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
					}
				}
				
				//实例化退出检测文件
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//检查全局变量程序退出标识是否为真，如为真，则程序退出
				if(InitCollectorFile.ifNeedExitFlag 
						//退出文件存在，则执行退出处理
						||f.exists()){
					if(f.exists())
						logger.info("#####采集程序中线程" + threadName + "下载文件后，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，线程结束退出>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####采集程序中线程" + threadName + "下载文件后，全局变量退出标志为真，线程结束退出>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
			}			
			
//			while((fileName = fileNameQueue.poll()) != null){
//				//如果有控制文件
//				if(CollectorConfInfo.ifHasCtlSourceFile){
//					ctlFileName = fileName;
//					//是否下载控制文件
//					if(CollectorConfInfo.ifDownloadCtlFile){
//						//如果需要先下载控制文件
//						if(CollectorConfInfo.ifFirstDownloadCtlFile){
//							//下载控制文件
//							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
//							//数据文件全名，把控制文件的后缀名替换为数据文件的后缀名
//							dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
//							//下载数据文件
//							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//							
//							//下载成功后移动或者删除源控制文件和数据文件
//							if(ctlDownFlag && dataDownFlag){
//								//是否分开存放数据文件和校验文件
//								if(CollectorConfInfo.ifSaveDifferentPath){
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}else{
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}
////								//移动或者删除源数据文件
////								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
////								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//							}
//						}else{//先下载数据文件
//							dataFileName = fileName;
//							//下载数据文件
//							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//							//控制文件全名，把数据文件的后缀名替换为控制文件的后缀名
//							ctlFileName = dataFileName.replace(CollectorConfInfo.dataSourceFileSuffixName, CollectorConfInfo.ctlSourceFileSuffixName);
//							//下载控制文件
//							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
//							//下载成功后移动或者删除源控制文件和数据文件
//							if(ctlDownFlag && dataDownFlag ){
//								//是否分开存放数据文件和校验文件
//								if(CollectorConfInfo.ifSaveDifferentPath){
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}else{
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}
////								//移动或者删除源数据文件
////								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
////								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//							}
//						}
//					}else{//不下载控制文件、直接删除控制文件或者移动控制文件
////						//移动或者删除源控制文件
////						CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//						//数据文件全名，把控制文件的后缀名替换为数据文件的后缀名
//						dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
//						//下载数据文件
//						dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//						if(dataDownFlag){
//							CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
////							CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//						}
//					}
//				}else{//没有控制文件，则只有数据文件
//					dataFileName = fileName;
//					//下载数据文件
//					dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName,CollectorConfInfo.saveTmpPath);
//					//移动或者删除源数据文件
//					if(dataDownFlag){
//						CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
////						CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//					}
//				}
//				
//				//实例化退出检测文件
//				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
//				//检查全局变量程序退出标识是否为真，如为真，则程序退出
//				if(InitCollectorFile.ifNeedExitFlag 
//						//退出文件存在，则执行退出处理
//						||f.exists()){
//					if(f.exists())
//						logger.info("#####采集程序中线程" + threadName + "下载文件后，检测到退出文件："+CollectorConstant.EXIT_FILE_FULL_NAME+" 存在，线程结束退出>>>>>>>>>>>>>>>>");
//					if(InitCollectorFile.ifNeedExitFlag)
//						logger.info("#####采集程序中线程" + threadName + "下载文件后，全局变量退出标志为真，线程结束退出>>>>>>>>>>>>>>>>");
//					InitCollectorFile.isExit = true;
//					return;
//				}
//			}
		} catch (FTPConnectionClosedException e1) {
			if(ftpClient != null){
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
				ftpClient = CollectorFileCommon.getFtpConnect();
			}else{
				ftpClient = CollectorFileCommon.getFtpConnect();
			}
		} catch (SocketException e2) {
			if(ftpClient != null){
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ftpClient = null;
				ftpClient = CollectorFileCommon.getFtpConnect();
			}else{
				ftpClient = CollectorFileCommon.getFtpConnect();
			}
		} catch (Exception e) {
			logger.error("%%%%%线程：" + threadName + "下载文件出错！！！", e);

		} finally {
			CollectorFileCommon.closeFtpConnect(ftpClient);
		}
	}
}
