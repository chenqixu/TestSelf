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
	//��־��¼��
	private static Logger logger = Logger.getLogger(SftpCollectFileCommon.class);
	/** 
	 * 
	 * @description:���÷���������SFTP����
	 * @author:xixg
	 * @date:2014-01-18
	 * @return SftpConnection sftp����ʵ����
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
			logger.error("%%%%%ʧ������SFTP��������"+CollectorConfInfo.ftpServerIp+"���˿ںţ�"+CollectorConfInfo.ftpServerPort+"��SFTP�û�����"
					+CollectorConfInfo.ftpServerUser+"��SFTP���룺"+CollectorConfInfo.ftpServerPassword,e);
		}
		return sftpConnection;
    }
    
    /** 
	 * 
	 * @description:���÷������ر�SFTP����
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
			logger.error("%%%%%�ر�SFTP���ӳ�������", e);
		}
    }
    
    /**
     * 
     * @description:  �ɼ��ļ��ܷ���
     * @author:xixg
     * @date:2014-02-13
     */
	public static void sftpCollectDirsFileService(){
		try {
			//����SFTP���ӣ�����SFTPɨ��Ŀ¼
			SftpConnection sftpConnection = getSftpConnection();
			//��Ҫ�ɼ���Դ����Ŀ¼����
			int sourceDataPathAraayLength = InitCollectorFile.sourceDataPathAraay.length;
			//ѭ������SFTP�������ϵĶ��Ŀ¼
			for(int i=0;i<sourceDataPathAraayLength;i++){
				//�ļ��ɼ���ʱ�����Ƿ�Ҫ�˳�
				//ʵ�����˳�����ļ�
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
				if(InitCollectorFile.ifNeedExitFlag 
						//�˳��ļ����ڣ���ִ���˳�����
						||f.exists()){
					//�˳��ļ�����,ִ���˳��¼�
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ����⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��ɼ������˳�>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ��ȫ�ֱ����˳���־Ϊ�棬�ɼ������˳�>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//���òɼ�һ��Ŀ¼�ļ���ҵ�񷽷�
				sftpCollectOneDirFileService(sftpConnection,InitCollectorFile.sourceDataPathAraay[i],InitCollectorFile.ftpThreadNumEveryDirArray[i],
						InitCollectorFile.mvSourceCtlFilePathArray[i],InitCollectorFile.mvSourceDataFilePathArray[i]);
			}
			 //ɨ��Ŀ¼���ļ�ʱ�Ĵ���
	        InitCollectorFile.currScanDirHasFile++;
	        //�ر�ɨ��Ŀ¼��SFTP����
	        closeSftpConnection(sftpConnection);
		} catch(Exception e){
			logger.error("%%%%%�ɼ����Ŀ¼�ļ���ҵ�񷽷������쳣!!!!",e);
		}
	}
	
	 /**
     * 
     * @description:  �г�FTP��������ָ��Ŀ¼�µ�ָ���ļ����б�
     * @author:xixg
     * @date:2014-02-13
     * @param remoteFilePath Զ���ļ�·��
     * @return List<String>
     */
    public static List<String> sftpListFiles(SftpConnection sftpConnection,String remoteFilePath) {
    	List<String> fileList = new ArrayList<String>();
        try {
        	ChannelSftp channelSftp = sftpConnection.getChannelSftp();
        	//�ı�SFTP�Ĺ���Ŀ¼
        	channelSftp.cd(remoteFilePath);
        	//�г���ǰĿ¼�������ļ��������Vector��
        	Vector fileVector = channelSftp.ls(remoteFilePath);
        	//����Vector
        	Iterator it = fileVector.iterator(); 
        	//ѭ��ȡ��Vector�е��ļ���
        	while(it.hasNext()) 
        	{ 
        		//ȡ���ļ���
	            String fileName = ((LsEntry)it.next()).getFilename(); 
	            //���˳�����Ҫ����ļ���
	            if(filterFileName(fileName,InitCollectorFile.currFilterSpecificFileName)){
	            	fileList.add(fileName);
	            }
        	}
        }catch (Exception e) {
        	logger.error("%%%%%�г�SFTP��������Ŀ¼��"+remoteFilePath+"���ļ�ʱ������", e);
        }
        return fileList;
    }
    
    /**
     * 
     * @description:  ���˳�SFTP��������ָ��Ŀ¼�µ�ָ���ļ����б�
     * @author:xixg
     * @date:2014-02-13
     * @param remoteFilePath Զ���ļ�·��
     * @return boolean ���˺��Ƿ����Ҫ��
     */
    public static boolean filterFileName(String fileName,String fileNameInclude){

		boolean returnFlag = false;
		try {
			//�Ƿ��ų�ָ���ļ������ļ�
			if(CollectorConfInfo.ifExcludeSpecificFileName){
				//�ų�ָ���ļ������ļ�
				if(fileName.indexOf(CollectorConfInfo.excludeSpecificFileName) > -1) return false;
			}
			//�Ƿ���˳��ļ��������ض��ַ���
			if(CollectorConfInfo.ifFilterSpecificFileName){
				//���˳��ļ��������ض��ַ���
				if(fileName.indexOf(CollectorConfInfo.filterSpecificFileName) < 0) return false;
			}
			//�����Ҫ�����ض��ļ���
			if(CollectorConfInfo.ifFilterSpecificTimeFileName
					//�����ض��ļ�����Ϊ��
					&& fileNameInclude != null && !"".equals(fileNameInclude)
					//�������ض��ļ�����ȥ��
					&& fileName.indexOf(fileNameInclude) < 0) return false;
			//Դ�ļ��Ƿ��п����ļ�
			if(CollectorConfInfo.ifHasCtlSourceFile ){
				if(CollectorConfInfo.ifDownloadCtlFile){
					//�����Ҫ�����ؿ����ļ�
					if(CollectorConfInfo.ifFirstDownloadCtlFile){
						//�˳������ļ���׺��ΪCTL���ļ�
						if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
					}else{
						//�˳������ļ���׺��Ϊ�����ļ����õ�ֵ���ļ�
						if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
					}
				}else{
					//�˳������ļ���׺��ΪCTL���ļ�
					if(fileName.endsWith(CollectorConfInfo.ctlSourceFileSuffixName)) returnFlag = true;
				}
			}else{
				//�˳������ļ���׺��Ϊ�����ļ����õ�ֵ���ļ�
				if(fileName.endsWith(CollectorConfInfo.dataSourceFileSuffixName)) returnFlag = true;
			}
		} catch (Exception e) {
			logger.info("%%%%%SFTP�����ļ�����������",e);
		}
		return returnFlag;
	
    }
    
	
	 /**
     * 
     * @description:  �ɼ��ļ��ܷ���
     * @author:xixg
     * @date:2014-02-13
     */
	public static void sftpCollectOneDirFileService(SftpConnection sftpConnection,String sourceDataPathString,
			String ftpThreadNumStr,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		try {
			//���SFTP����Ϊ�գ������´���FTP����
			if(sftpConnection == null ){
				//����SFTP���ӣ�����FTPɨ��Ŀ¼
				sftpConnection = getSftpConnection();
			}
			//����ʱ�����ļ�List
			List<String> allHourFileList = null;
			int allHourFileListSize = 0;
			//��ǰСʱҪ���ص������ļ�
			List<String> currHourFileList = null;
			int currHourFileTotal = 0;
			//��ǰĿ¼����������߳���
			int ftpThreadNum = Integer.parseInt(ftpThreadNumStr);
			//����ʱ���
			String firstFileDate = "";
			//�ɼ��ļ��Ƿ����ļ�����ʱ��˳��
			if(CollectorConfInfo.ifSortByFileName){
				//�г�ָ��Ŀ¼�µ������ļ�
				allHourFileList = sftpListFiles(sftpConnection,sourceDataPathString);
				allHourFileListSize = allHourFileList.size();
				//���Ŀ¼�е��ļ�Ϊ�㣬��������һ��ʱ����������һ��ѭ������Ŀ¼
		        if(allHourFileListSize == 0) {
		        	logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ɨ��Դ����Ŀ¼��"+sourceDataPathString+"ʱû���ļ������������һ�ε�Ŀ¼ɨ��>>>>>>>>>>");
		        	Thread.sleep(2000);
		        	return ;
		        }
		        //��ctlFileList�е��ļ���ʱ���������GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv
		        Collections.sort(allHourFileList, new TimeOfFileComparator());
		        String firstFileName = "";
		        //�Ƿ�ɼ�����ʱ�����ļ�
				if(CollectorConfInfo.ifDownloadNewestFile){
					//ȡ���������һ���ļ�����Ϊ����ʱ�����ļ�
					firstFileName = allHourFileList.get(allHourFileListSize-1);
				}else{
					//�ļ��������ȡ������ʱ����ļ�
			        firstFileName = allHourFileList.get(0);
				}
				//��ȡ�ļ����е�ʱ���
		        //����GN�����ݣ�GnC64_http_dnssession_60_20131218_105700_20131218_105759.csv  ʱ���Ϊ20131218_10
		        //����MC�����ݣ�FuJianYiDong-A-IuCS-4-201408061520.txt  ʱ���Ϊ20140806-15
				firstFileDate = CollectorFileCommon.getTimeByFileName(firstFileName);
		        logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ɨ��Ŀ¼��"+sourceDataPathString+"ʱ���ļ�������ʱ�����ļ�����Ϊ��"+allHourFileListSize+",�ļ���������磨�����£�ʱ���Ϊ��"+firstFileDate);
		        //��ȡĿ¼��������ʱ����ͬʱ��������CSV�ļ�
		        currHourFileList = CollectorFileCommon.getSameTimeCsvFilesByList(allHourFileList, firstFileDate,CollectorConfInfo.ifDateAndHourTheSameLocation);
			}else{//�ɼ��ļ��������ļ�����ʱ��˳�򣬲ɼ�ָ��ʱ����ļ�
				if(0 == InitCollectorFile.currScanDirHasFile){//��������ǵ�һ������Ŀ¼������˳�ָ��ʱ�����ļ�Ϊ�����ļ��ĳ�ʼֵ
					InitCollectorFile.currFilterSpecificFileName = CollectorConfInfo.filterSpecificTimeFileName;
				}else{//��������ǲ��ǵ�һ������Ŀ¼������˳�ָ��ʱ���Ҫ����
					String currSystemDate = CollectorFileCommon.getCurrentDate(CollectorConstant.FORMAT_0F_HOUR);
					//���ȫ�ֱ����еĵ�ǰʱ��С��ϵͳʱ��㣬��ʱ���Сʱ����
					if(InitCollectorFile.currFilterSpecificFileName.compareTo(currSystemDate) < 0){
						//Сʱ����
						InitCollectorFile.currFilterSpecificFileName = CollectorFileCommon.incrementHourStr(InitCollectorFile.currFilterSpecificFileName);
					}
				}
				firstFileDate = InitCollectorFile.currFilterSpecificFileName;
				//�ļ�������
//	            DataColectorFTPFileFilter fileFilter = new DataColectorFTPFileFilter(InitCollectorFile.currFilterSpecificFileName);
				//�г�ָ��Ŀ¼�µ������ļ�
				currHourFileList = sftpListFiles(sftpConnection,sourceDataPathString);
				currHourFileTotal = currHourFileList.size();
				//���Ŀ¼�е��ļ�Ϊ�㣬��������һ��ʱ����������һ��ѭ������Ŀ¼
		        if(currHourFileTotal == 0) {
		        	logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ɨ��Դ����Ŀ¼��"+sourceDataPathString+"ʱû���ļ������������һ�ε�Ŀ¼ɨ��>>>>>>>>>>");
		        	Thread.sleep(2000);
		        	return ;
		        }
			}
			currHourFileTotal = currHourFileList.size();
	        logger.info("#####���ڲɼ�ʱ��㣺"+firstFileDate+"���ļ�����ʱ�����ļ�����Ϊ��"+currHourFileTotal);
	        
	        //ĳ��Ŀ¼����ļ����Ķ���
	        Queue<String> fileNameQueue = null;
	        //���ȫ�ֱ���Map�д��ڴ�Ŀ¼��Ӧ���ļ������У���ȡ��������
	        if(InitCollectorFile.fileNameQueueMap.containsKey(sourceDataPathString)){
	        	//ȡ����Ŀ¼���ļ�������
	        	fileNameQueue = InitCollectorFile.fileNameQueueMap.get(sourceDataPathString);
	        }else{//��������ڱ�Ŀ¼��Ӧ���ļ������У��򴴽��˶���
	        	fileNameQueue = new ConcurrentLinkedQueue<String>();
	        	//���´������ļ������з���ȫ�ֱ���map��
	        	InitCollectorFile.fileNameQueueMap.put(sourceDataPathString, fileNameQueue);
	        }
			//ѭ��ȡ��currCtlFileTotal���ļ������߳̽��вɼ�
			for (int i = 0; i < currHourFileTotal; i++) {
				//�ļ��ɼ���ʱ�����Ƿ�Ҫ�˳�
				//ʵ�����˳�����ļ�
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
				if(InitCollectorFile.ifNeedExitFlag 
						//�˳��ļ����ڣ���ִ���˳�����
						||f.exists()){
					//�˳��ļ�����,ִ���˳��¼�
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ����⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��ɼ������˳�>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ��ȫ�ֱ����˳���־Ϊ�棬�ɼ������˳�>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//ѭ��ȡ���ļ�����ȫ�ֵİ�ȫ������
				String fileName = currHourFileList.get(i);
				fileNameQueue.offer(fileName);
			}
			//���������ļ����õ�FTP�߳�����������Ӧ��FTP�߳̽��������ļ�
			for(int i=0;i<ftpThreadNum;i++){
				//�ļ��ɼ���ʱ�����Ƿ�Ҫ�˳�
				//ʵ�����˳�����ļ�
				File f = new File(CollectorConstant.EXIT_FILE_FULL_NAME);
				//���ȫ�ֱ��������˳���ʶ�Ƿ�Ϊ�棬��Ϊ�棬������˳�
				if(InitCollectorFile.ifNeedExitFlag 
						//�˳��ļ����ڣ���ִ���˳�����
						||f.exists()){
					//�˳��ļ�����,ִ���˳��¼�
					CollectorFileCommon.exitHandle();
					if(f.exists())
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ����⵽�˳��ļ���"+CollectorConstant.EXIT_FILE_FULL_NAME+" ���ڣ��ɼ������˳�>>>>>>>>>>>>>>>>");
					if(InitCollectorFile.ifNeedExitFlag)
						logger.info("#####�ɼ������"+InitCollectorFile.currCycleNum+"��ѭ��ɨ��Ŀ¼ʱ��ȫ�ֱ����˳���־Ϊ�棬�ɼ������˳�>>>>>>>>>>>>>>>>");
					InitCollectorFile.isExit = true;
					return;
				}
				//��ǰ�̵߳�������߳�ʱ���������˯����Ϣ���������߳�
				while(InitCollectorFile.dataCollectorThreadList.size() >= ftpThreadNum){
					//���������߳�
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
				//����SFTP���ӣ�����FTPɨ��Ŀ¼
				SftpConnection sftpConnectionThread = getSftpConnection();
				SftpDownloadFileThread downloadFileThread = new SftpDownloadFileThread(fileNameQueue,sftpConnectionThread,sourceDataPathString,mvSourceCtlFilePath,mvSourceDataFilePath);
		        InitCollectorFile.dataCollectorThreadList.add(downloadFileThread);
		        downloadFileThread.start();
			}
		} catch (Exception e) {
			logger.error("%%%%%�ɼ�SFTP������Ŀ¼��"+sourceDataPathString+"ҵ�������",e);
		}
	}
	
	 /**
	 * 
	 * @description: ���÷�������SFTP��ʽ�����ļ�
	 * @author:xixg
	 * @date:2013-11-23
	 * @param directory ����Ŀ¼
	 * @param downloadFile ���ص��ļ�
	 * @param saveFile ���ڱ��ص�·��
	 * @param sftp
	 * @return String  ������Ҫ���ļ�����ʽ
	 */
	 public static void sftpDownloadOneFile(ChannelSftp channelSftp,String sourceDirectory, String downloadFile, String threadName,String savePath){
		 OutputStream outputStream = null;
    	try {
    		channelSftp.cd(sourceDirectory);
    		//�����ļ��Ŀ�ʼʱ��
			long startTime = System.currentTimeMillis();
			//�м��ļ�ȫ��
			String tmpFileName = savePath  + downloadFile + CollectorConfInfo.ftpTmpFileNameSuffix;
			//ʵ�����ļ�ʵ��
			File tmpFile = new File(tmpFileName);
			//ʵ�����ļ������
			outputStream = new FileOutputStream(tmpFile);
    		channelSftp.get(downloadFile, outputStream);
    		//�ļ�д��󣬹ر����������
            if(outputStream != null){
            	outputStream.flush();
				outputStream.close();
			}
            //�����ļ��Ľ���ʱ��
    		long endTime = System.currentTimeMillis();
			//�����ļ��ܹ���ʱ
			long totalTime = endTime - startTime;
			logger.info("#####�̣߳�"+threadName+"�����ļ�:"+downloadFile+"���,�ܺ�ʱ:"+totalTime);
			//������ɺ���ļ�ȫ��
			String finishFileName = savePath  + downloadFile;
			//ʵ�����ļ�ʵ��
			File finishFile = new File(finishFileName);
			//�����ļ�������ɺ�ȥ��TMP��׺
			tmpFile.renameTo(finishFile);
//			System.out.println("#####�̣߳�"+threadName+"rename over=");
		} catch (Exception e) {
			logger.error("#####SFTP�����ļ���"+sourceDirectory+downloadFile+"��������",e);
		} finally{
			//�ļ�д��󣬹ر����������
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
		 * @description:���÷���  �ƶ�����ɾ��FTP�����ļ�����
		 * @author:xixg
		 * @date:2013-11-29
		 * @param ftpClient FTP����
		 * @param dataFileName �ļ���
		 * @param threadName �߳���
		 * @return String
		 */
		public static boolean mvOrDeleteSftpSourceCtlFile(ChannelSftp channelSftp,String sourceDataPathString,String ctlFileName,
				String mvSourceCtlFilePath,String threadName){
			boolean returnFlag = false;
			try {
				//��ȡ�����ļ������õ��Ƿ�ɾ��Դ�����ļ���ʶ
				if(CollectorConfInfo.ifNeedDeleteSourceCtlFile){
					//ɾ��Զ��FTP�������Ŀ����ļ�
					channelSftp.rm(ctlFileName);
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ���ɾ��SFTPԴ�����ļ�:"+ctlFileName);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵ�ɾ��SFTPԴ�����ļ�:"+ctlFileName);
			}
			try {
				//��ȡ�����ļ������õ��Ƿ��ƶ�Դ���ݱ�ʶ
				if(CollectorConfInfo.ifNeedMvSourceCtlFile){
					//�ƶ�Զ��FTP�������Ŀ����ļ���Ŀ��Ŀ¼
					channelSftp.rename(sourceDataPathString+ctlFileName, mvSourceCtlFilePath+ctlFileName);
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ��ؽ������ļ�:"+ctlFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceCtlFilePath);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵؽ������ļ�:"+ctlFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceCtlFilePath);
			}
			return returnFlag ;
		}
		
		/**
		 * 
		 * @description:���÷���  �ƶ�����ɾ��Դ�����ļ�����
		 * @author:xixg
		 * @date:2013-11-29
		 * @param ftpClient FTP����
		 * @param dataFileName �ļ���
		 * @param threadName �߳���
		 * @return String
		 */
		public static boolean mvOrDeleteSftpSourceDataFile(ChannelSftp channelSftp,String sourceDataPathString,String dataFileName,
				String mvSourceDataFilePath,String threadName){
			boolean returnFlag = false;
			try {
				//��ȡ�����ļ������õ��Ƿ�ɾ��Դ�����ļ���ʶ
				if(CollectorConfInfo.ifNeedDeleteSourceDataFile){
					//ɾ��Զ��FTP�������Ŀ����ļ�
					channelSftp.rm(dataFileName);
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ���ɾ��SFTPԴ�����ļ�:"+dataFileName);
				}
			} catch (Exception e) {
				returnFlag = false; 
				logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵ�ɾ��SFTPԴ�����ļ�:"+dataFileName);
			}
			try {			
				//��ȡ�����ļ������õ��Ƿ��ƶ�Դ���ݱ�ʶ
				if(CollectorConfInfo.ifNeedMvSourceDataFile){
					//�ƶ�Զ��FTP�������Ŀ����ļ���Ŀ��Ŀ¼
					channelSftp.rename(sourceDataPathString+dataFileName, mvSourceDataFilePath+dataFileName);
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ��ؽ������ļ�:"+dataFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceDataFilePath);
				}
			} catch (Exception e) {
				returnFlag = false;
				logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵؽ������ļ�:"+dataFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceDataFilePath);
			
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
