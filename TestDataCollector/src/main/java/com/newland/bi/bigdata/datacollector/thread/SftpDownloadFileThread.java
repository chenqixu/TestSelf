package com.newland.bi.bigdata.datacollector.thread;

import java.io.File;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.newland.bi.bigdata.datacollector.common.CollectorConstant;
import com.newland.bi.bigdata.datacollector.common.InitCollectorFile;
import com.newland.bi.bigdata.datacollector.common.SftpCollectFileCommon;
import com.newland.bi.bigdata.datacollector.config.CollectorConfInfo;
import com.newland.bi.bigdata.datacollector.entity.SftpConnection;

/**
 * 
 * @description:FTP�����ļ��߳�
 * @author:xixg
 * @date:2014-02-19
 */
public class SftpDownloadFileThread extends Thread{
	//��־��¼��
	private static Logger logger = Logger.getLogger(SftpDownloadFileThread.class);
	//�̰߳�ȫ���У���ŵ�ǰҪ���ص������ļ���
	private Queue<String> fileNameQueue;
	private String sourceDataPathString;
	//FTP�ͻ������Ӷ���
	private SftpConnection sftpConnection;
	//�����е��ļ���
	private String fileName ;
	//�����ļ���
	private String dataFileName;
	//�����ļ���
	private String ctlFileName;
	//�߳���
	private String threadName;
	private String mvSourceCtlFilePath;
	private String mvSourceDataFilePath;
	public SftpDownloadFileThread(Queue<String> fileNameQueue,SftpConnection sftpConnection,
			String sourceDataPathString,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		this.fileNameQueue = fileNameQueue;
		this.sftpConnection = sftpConnection;
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
		ChannelSftp channelSftp = sftpConnection.getChannelSftp();
		try {
			while((fileName = fileNameQueue.poll()) != null){
				//����п����ļ�
				if(CollectorConfInfo.ifHasCtlSourceFile){
					ctlFileName = fileName;
					//�Ƿ����ؿ����ļ�
					if(CollectorConfInfo.ifDownloadCtlFile){
						//�����Ҫ�����ؿ����ļ�
						if(CollectorConfInfo.ifFirstDownloadCtlFile){
							//���ؿ����ļ�
							if(CollectorConfInfo.ifSaveDifferentPath){
								SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, ctlFileName, threadName,CollectorConfInfo.saveCtlPath);
							}else{
								SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, ctlFileName, threadName,CollectorConfInfo.saveDataPath);
							}
							//��������ƶ�����ɾ��Դ�����ļ�
							SftpCollectFileCommon.mvOrDeleteSftpSourceCtlFile(channelSftp,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
							//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
							dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
							//���������ļ�
							SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, dataFileName, threadName, CollectorConfInfo.saveDataPath);
							SftpCollectFileCommon.mvOrDeleteSftpSourceDataFile(channelSftp,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
						}else{//�����������ļ�
							dataFileName = fileName;
							//���������ļ�
							SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, dataFileName, threadName, CollectorConfInfo.saveDataPath);
							SftpCollectFileCommon.mvOrDeleteSftpSourceDataFile(channelSftp,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
							//�����ļ�ȫ�����������ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
							ctlFileName = dataFileName.replace(CollectorConfInfo.dataSourceFileSuffixName, CollectorConfInfo.ctlSourceFileSuffixName);
							//���ؿ����ļ�
							if(CollectorConfInfo.ifSaveDifferentPath){
								SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, ctlFileName, threadName,CollectorConfInfo.saveCtlPath);
							}else{
								SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, ctlFileName, threadName,CollectorConfInfo.saveDataPath);
							}
							//��������ƶ�����ɾ��Դ�����ļ�
							SftpCollectFileCommon.mvOrDeleteSftpSourceCtlFile(channelSftp,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
						}
					}else{//�����ؿ����ļ���ֱ��ɾ�������ļ������ƶ������ļ�
						//�ƶ�����ɾ��Դ�����ļ�
						SftpCollectFileCommon.mvOrDeleteSftpSourceCtlFile(channelSftp,sourceDataPathString, ctlFileName,mvSourceCtlFilePath, threadName);
						//�����ļ�ȫ�����ѿ����ļ��ĺ�׺���滻Ϊ�����ļ��ĺ�׺��
						dataFileName = ctlFileName.replace(CollectorConfInfo.ctlSourceFileSuffixName, CollectorConfInfo.dataSourceFileSuffixName);
						//���������ļ�
						SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, dataFileName, threadName, CollectorConfInfo.saveDataPath);
						SftpCollectFileCommon.mvOrDeleteSftpSourceDataFile(channelSftp,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
					}
				}else{//û�п����ļ�����ֻ�������ļ�
					dataFileName = fileName;
					//���������ļ�
					SftpCollectFileCommon.sftpDownloadOneFile(channelSftp,sourceDataPathString, dataFileName, threadName,CollectorConfInfo.saveDataPath);
					//�ƶ�����ɾ��Դ�����ļ�
					SftpCollectFileCommon.mvOrDeleteSftpSourceDataFile(channelSftp,sourceDataPathString, dataFileName,mvSourceDataFilePath, threadName);
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
		} catch (Exception e) {
			logger.error("%%%%%�̣߳�"+threadName+"�����ļ���������", e);
			
		}finally{
			SftpCollectFileCommon.closeSftpConnection(sftpConnection);
		}
	}
}
