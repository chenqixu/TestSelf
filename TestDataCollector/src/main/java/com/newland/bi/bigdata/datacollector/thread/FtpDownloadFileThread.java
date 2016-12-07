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
 * @description:FTP�����ļ��߳�
 * @author:xixg
 * @date:2014-02-19
 */
public class FtpDownloadFileThread extends Thread{
	//��־��¼��
	private static Logger logger = Logger.getLogger(FtpDownloadFileThread.class);
	//�̰߳�ȫ���У���ŵ�ǰҪ���ص������ļ���
	private Queue<String> fileNameQueue;
	private String sourceDataPathString;
	//FTP�ͻ������Ӷ���
	private FTPClient ftpClient;
	//�����е��ļ���
	private String fileName ;
	//�����ļ���
	private String dataFileName;
	//�����ļ���
	private String ctlFileName;
	//�����ļ������Ƿ�ɹ���־
	private boolean ctlDownFlag;
	//�����ļ������Ƿ�ɹ���־
	private boolean dataDownFlag;
	//�߳���
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
	 * @description:�����߳̾������еķ���
	 * @author:xixg
	 * @date:2014-02-19
	 */
	@Override
	public void run() {
		//��ȡ��ǰ�߳�����
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
				//����п����ļ�
				if(CollectorConfInfo.ifHasCtlSourceFile){
					ctlFileName = fileName;
					//�Ƿ����ؿ����ļ�
					if(CollectorConfInfo.ifDownloadCtlFile){
						//�����Ҫ�����ؿ����ļ�
						if(CollectorConfInfo.ifFirstDownloadCtlFile){
							//���ؿ����ļ�
							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
							//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
							dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
							//���������ļ�
							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
							
							//���سɹ����ƶ�����ɾ��Դ�����ļ��������ļ�
							if(ctlDownFlag && dataDownFlag){
								//�Ƿ�ֿ���������ļ���У���ļ�
								if(CollectorConfInfo.ifSaveDifferentPath){
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}else{
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}
//								//�ƶ�����ɾ��Դ�����ļ�
//								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
							}
						}else{//�����������ļ�
							dataFileName = fileName;
							//���������ļ�
							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
							//�����ļ�ȫ�����������ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
							ctlFileName = dataFileName.replace(CollectorConfInfo.dataSourceFileSuffixName, CollectorConfInfo.ctlSourceFileSuffixName);
							//���ؿ����ļ�
							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
							//���سɹ����ƶ�����ɾ��Դ�����ļ��������ļ�
							if(ctlDownFlag && dataDownFlag ){
								//�Ƿ�ֿ���������ļ���У���ļ�
								if(CollectorConfInfo.ifSaveDifferentPath){
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}else{
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
								}
//								//�ƶ�����ɾ��Դ�����ļ�
//								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
							}
						}
					}else{//�����ؿ����ļ���ֱ��ɾ�������ļ������ƶ������ļ�
//						//�ƶ�����ɾ��Դ�����ļ�
//						CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
						//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
						dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
						//���������ļ�
						dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
						if(dataDownFlag){
							CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//							CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
						}
					}
				}else{//û�п����ļ�����ֻ�������ļ�
					dataFileName = fileName;
					//���������ļ�
					dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName,CollectorConfInfo.saveTmpPath);
					//�ƶ�����ɾ��Դ�����ļ�
					if(dataDownFlag){
						CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//						CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
					}
				}
				
				//ʵ�����˳�����ļ�
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
				if(InitCollectorFile.ifNeedExitFlag 
						//�˳��ļ����ڣ���ִ���˳�����
						||f.exists()){
					if(f.exists())
						logger.info("#####�ɼ��������߳�" + threadName + "�����ļ��󣬼�⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��߳̽����˳�>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####�ɼ��������߳�" + threadName + "�����ļ���ȫ�ֱ����˳���־Ϊ�棬�߳̽����˳�>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
			}			
			
//			while((fileName = fileNameQueue.poll()) != null){
//				//����п����ļ�
//				if(CollectorConfInfo.ifHasCtlSourceFile){
//					ctlFileName = fileName;
//					//�Ƿ����ؿ����ļ�
//					if(CollectorConfInfo.ifDownloadCtlFile){
//						//�����Ҫ�����ؿ����ļ�
//						if(CollectorConfInfo.ifFirstDownloadCtlFile){
//							//���ؿ����ļ�
//							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
//							//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
//							dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
//							//���������ļ�
//							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//							
//							//���سɹ����ƶ�����ɾ��Դ�����ļ��������ļ�
//							if(ctlDownFlag && dataDownFlag){
//								//�Ƿ�ֿ���������ļ���У���ļ�
//								if(CollectorConfInfo.ifSaveDifferentPath){
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}else{
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}
////								//�ƶ�����ɾ��Դ�����ļ�
////								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
////								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//							}
//						}else{//�����������ļ�
//							dataFileName = fileName;
//							//���������ļ�
//							dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//							//�����ļ�ȫ�����������ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
//							ctlFileName = dataFileName.replace(CollectorConfInfo.dataSourceFileSuffixName, CollectorConfInfo.ctlSourceFileSuffixName);
//							//���ؿ����ļ�
//							ctlDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, ctlFileName, threadName,CollectorConfInfo.saveTmpPath);
//							//���سɹ����ƶ�����ɾ��Դ�����ļ��������ļ�
//							if(ctlDownFlag && dataDownFlag ){
//								//�Ƿ�ֿ���������ļ���У���ļ�
//								if(CollectorConfInfo.ifSaveDifferentPath){
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveCtlPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}else{
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, ctlFileName);
//									CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
//								}
////								//�ƶ�����ɾ��Դ�����ļ�
////								CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
////								CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//							}
//						}
//					}else{//�����ؿ����ļ���ֱ��ɾ�������ļ������ƶ������ļ�
////						//�ƶ�����ɾ��Դ�����ļ�
////						CollectorFileCommon.mvOrDeleteFtpSourceCtlFile(ftpClient,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
//						//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
//						dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
//						//���������ļ�
//						dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName, CollectorConfInfo.saveTmpPath);
//						if(dataDownFlag){
//							CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
////							CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//						}
//					}
//				}else{//û�п����ļ�����ֻ�������ļ�
//					dataFileName = fileName;
//					//���������ļ�
//					dataDownFlag = CollectorFileCommon.ftpDownloadOneFile(ftpClient, dataFileName, threadName,CollectorConfInfo.saveTmpPath);
//					//�ƶ�����ɾ��Դ�����ļ�
//					if(dataDownFlag){
//						CollectorFileCommon.mvFile(CollectorConfInfo.saveTmpPath, CollectorConfInfo.saveDataPath, dataFileName);
////						CollectorFileCommon.mvOrDeleteFtpSourceDataFile(ftpClient,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
//					}
//				}
//				
//				//ʵ�����˳�����ļ�
//				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
//				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
//				if(InitCollectorFile.ifNeedExitFlag 
//						//�˳��ļ����ڣ���ִ���˳�����
//						||f.exists()){
//					if(f.exists())
//						logger.info("#####�ɼ��������߳�" + threadName + "�����ļ��󣬼�⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��߳̽����˳�>>>>>>>>>>>>>>>>");
//					if(InitCollectorFile.ifNeedExitFlag)
//						logger.info("#####�ɼ��������߳�" + threadName + "�����ļ���ȫ�ֱ����˳���־Ϊ�棬�߳̽����˳�>>>>>>>>>>>>>>>>");
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
			logger.error("%%%%%�̣߳�" + threadName + "�����ļ���������", e);

		} finally {
			CollectorFileCommon.closeFtpConnect(ftpClient);
		}
	}
}
