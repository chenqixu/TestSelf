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
	//��־��¼��
	private static Logger logger = Logger.getLogger(CollectorFileCommon.class);
	/**
	 * 
	 * @description: ɾ��totalThreadList�����е��߳�
	 * ��ѭ��ɾ��List�й�����ɵ��̣߳�ֱ�����е��̹߳����꣬��ôList�����е��߳�Ҳ������꣩
	 * @author:xixg
	 * @date:2013-11-23
	 * @param inFileNameMap map�д�ŵ��ļ���
	 * @return boolean  ������true�ǣ���List�����е��̶߳�ɾ����
	 */
	 public boolean clearTotalThreadList(List<Thread> totalThreadList){
		 	boolean  exitFlag = false;
	    	//�˳�ǰList����Ҫ������̣߳��̹߳�������Ҷ�����û��Ҫ�ϴ����ļ�����List�е��߳�Ҫ�����
	    	List<Thread>  deleteList = new ArrayList<Thread>();
	    	try {
	    		//��totalThreadList�����߳�ʱ���ظ�ִ��ѭ����ֱ����totalThreadList���е��߳����
				while(totalThreadList.size()>0){
					//ѭ��ȡ��totalThreadList�е��߳�
					for(Thread itemT : totalThreadList){
						//�ж��߳��Ƿ��ڹ���
						if(!itemT.isAlive()){
							//�ѹ�����ϵ��̷߳���ɾ��List��
							deleteList.add(itemT);
						} 
					}
					//ɾ��������ɵ��߳�
					totalThreadList.removeAll(deleteList);
					//ɾ���߳�����2000����
					Thread.sleep(2000);
				}
				exitFlag = true;
			} catch (InterruptedException e) {
				logger.error("%%%%%�����߳��б��Ѳ�����߳�ɾ��������",e);
			}
			return exitFlag;
	    }
	 
	/** 
	 * 
	 * @description:�����߳��б��Ѳ�����߳��ƶ�
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void clearThreadList(List<Thread> sourceThreadList){
    	try {
			//��Ҫɾ�����߳��б�
			List<Thread> needDelThreadList = new ArrayList<Thread>();
			//ѭ��ȡ���߳�
			for(Thread t:sourceThreadList){
				//����߳��Ƿǻ�ģ�������ɾ����List��
				if(!t.isAlive()) needDelThreadList.add(t);
			}
			sourceThreadList.removeAll(needDelThreadList);
		} catch (Exception e) {
			logger.error("%%%%%���ȫ�ֱ���threadList�е��̳߳�����", e);
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
    public static List<String> listFtpFiles(FTPClient ftpClient,String remoteFilePath,FTPFileFilter fileFilter) {
    	List<String> fileList = new ArrayList<String>();
        try {
        	//�ı�FTP�Ĺ���Ŀ¼
        	ftpClient.changeWorkingDirectory(remoteFilePath);
            
            //����ָ��Ŀ¼��ȡ��ָ����׺�����ļ�������������
            FTPFile[] fileArrays = ftpClient.listFiles(remoteFilePath,fileFilter);
            //������תΪList
            for (int i = 0; i < fileArrays.length; i++) {
            	//ת����List
                if (fileArrays[i].isFile()) {
                	//ȡ���ļ���
                    String fileName = fileArrays[i].getName().toString();
                    fileName = new String(fileName.getBytes(), ftpClient.getControlEncoding());
                    //���ļ�������List��
                    fileList.add(fileName);
                }
            }
            
        }catch (Exception e) {
        	logger.error("%%%%%�г�FTP��������Ŀ¼��"+remoteFilePath+"���ļ�ʱ������", e);
        }
        return fileList;
    }
    
    /**
	 * 
	 * @description:���÷���  �ɼ������˳�ǰ���Ĺ���
	 * @author:xixg
	 * @date:2014-03-26
	 */ 
	public static void exitHandle() {
		try{
			synchronized (InitCollectorFile.dataCollectorThreadList) {
				//�ɼ������˳�ǰ���ȴ����ڹ������߳����
				while(InitCollectorFile.dataCollectorThreadList.size()>0){
					logger.info("#####�ɼ������˳�ǰ�ȴ����ڹ������߳���ɣ�ʣ��ɼ��߳���Ϊ:"+InitCollectorFile.dataCollectorThreadList.size());
					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
					Thread.sleep(1000);
				}
			}
		}catch(Exception e){
			logger.error("%%%%%�ɼ������˳��쳣������",e);
		}
	}
	
	
	/**
     * 
     * @description:  �ɼ��ļ��ܷ���
     * @author:xixg
     * @date:2014-02-13
     */
	public static void CollectorDirsFtpFileService(){
		try {
			//��Ųɼ������ļ���Mapÿ��2��Сʱ���һ��
			long nowTime = System.currentTimeMillis();
			if(nowTime - InitCollectorFile.clearTime > 7200000){
				InitCollectorFile.errFileMap.clear();
				InitCollectorFile.clearTime = nowTime;
			}
			
			//����FTP���ӣ�����FTPɨ��Ŀ¼
			FTPClient ftpClient = getFtpConnect();
			int sourceDataPathAraayLength = InitCollectorFile.sourceDataPathAraay.length;
			//�ļ�������
            DataColectorFTPFileFilter fileFilter = new DataColectorFTPFileFilter(InitCollectorFile.currFilterSpecificFileName);
			//ѭ������FTP�������ϵĶ��Ŀ¼
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
				CollectorFtpOneDirFileService(ftpClient,InitCollectorFile.sourceDataPathAraay[i],fileFilter,
						InitCollectorFile.ftpThreadNumEveryDirArray[i],InitCollectorFile.mvSourceCtlFilePathArray[i],InitCollectorFile.mvSourceDataFilePathArray[i]);
			}
			 //ɨ��Ŀ¼���ļ�ʱ�Ĵ���
	        InitCollectorFile.currScanDirHasFile++;
	        //�ر�ɨ��Ŀ¼��FTP����
	        CollectorFileCommon.closeFtpConnect(ftpClient);
		} catch(Exception e){
			logger.error("%%%%%�ɼ����Ŀ¼�ļ���ҵ�񷽷������쳣!!!!",e);
		}
	}
	
	
	 /**
     * 
     * @description:  �ɼ��ļ��ܷ���
     * @author:xixg
     * @date:2014-02-13
     */
	public static void CollectorFtpOneDirFileService(FTPClient ftpClient,String sourceDataPathString,DataColectorFTPFileFilter fileFilter,
			String ftpThreadNumStr,String mvSourceCtlFilePath,String mvSourceDataFilePath){
		try {
			//���FTP����Ϊ�գ������´���FTP����
			if(ftpClient == null ){
				//����FTP���ӣ�����FTPɨ��Ŀ¼
				ftpClient = getFtpConnect();
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
				allHourFileList = CollectorFileCommon.listFtpFiles(ftpClient,sourceDataPathString,fileFilter);
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
				currHourFileList = CollectorFileCommon.listFtpFiles(ftpClient,sourceDataPathString,fileFilter);
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
			int fq = fileNameQueue.size();
			boolean fqf = false;
			if(fq!=currHourFileTotal) {
				fqf = true;
				logger.error("#####���ڲɼ�ʱ��㣺"+firstFileDate+"���ļ�����ʱ�������ӵ������е��ļ�����Ϊ��"+fq+"����ԭ��ɨ���"+currHourFileTotal+"��һ�£�"+fqf);
			}else{
				logger.info("#####���ڲɼ�ʱ��㣺"+firstFileDate+"���ļ�����ʱ�������ӵ������е��ļ�����Ϊ��"+fq+"����ԭ��ɨ���"+currHourFileTotal+"һ�£�"+fqf);
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
//				//��ǰ�̵߳�������߳�ʱ���������˯����Ϣ���������߳�
//				while(InitCollectorFile.dataCollectorThreadList.size() >= ftpThreadNum){
//					//���������߳�
//					CollectorFileCommon.clearNoAliveThread(InitCollectorFile.dataCollectorThreadList);
//					Thread.sleep(1000);
//				}
				//����FTP���ӣ�һ���߳�ʹ��һ��FTP����
				FTPClient ftpClientForThread = getFtpConnect();
				//����FTP���Ӻ󣬸ı乤��Ŀ¼��Ҫ���ص�Դ����Ŀ¼
				ftpClientForThread.changeWorkingDirectory(sourceDataPathString);
		        FtpDownloadFileThread downloadFileThread = new FtpDownloadFileThread(fileNameQueue,ftpClientForThread,sourceDataPathString,mvSourceCtlFilePath,mvSourceDataFilePath);
		        InitCollectorFile.dataCollectorThreadList.add(downloadFileThread);
		        downloadFileThread.start();
			}
		} catch (Exception e) {
			logger.error("%%%%%�ɼ�FTP������Ŀ¼��"+sourceDataPathString+"ҵ�������",e);
		}
	}
	
	 
	
	/** 
	 * 
	 * @description:�����߳��б��Ѳ�����߳��Ƴ�
	 * @author:xixg
	 * @date:2014-01-18
	 * @return void
	 */
    public static void clearNoAliveThread(List<Thread> sourceThreadList){
    	try {
			//��Ҫɾ�����߳��б�
			List<Thread> needDelThreadList = new ArrayList<Thread>();
			//ѭ��ȡ���߳�
			for(Thread t:sourceThreadList){
				//����߳��Ƿǻ�ģ�������ɾ����List��
				if(!t.isAlive()) needDelThreadList.add(t);
			}
			sourceThreadList.removeAll(needDelThreadList);
		} catch (Exception e) {
			logger.error("%%%%%�����߳��б��Ѳ�����߳�ɾ��������",e);
		}
    }
    
    /** 
	 * 
	 * @description:���÷���������FTP����
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
//	        logger.info("#####�ɹ�������FTP��������"+CollectorConfInfo.ftpServerIp+"���˿ںţ�"+CollectorConfInfo.ftpServerPort+"��FTP�û�����"
//					+CollectorConfInfo.ftpServerUser+"��FTP���룺"+CollectorConfInfo.ftpServerPassword);
		} catch (Exception e) {
			logger.error("%%%%%ʧ������FTP��������"+CollectorConfInfo.ftpServerIp+"���˿ںţ�"+CollectorConfInfo.ftpServerPort+"��FTP�û�����"
					+CollectorConfInfo.ftpServerUser+"��FTP���룺"+CollectorConfInfo.ftpServerPassword,e);
		}
		return ftpClient;
    }
    
    /** 
	 * 
	 * @description:���÷������ر�FTP����
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
			logger.error("%%%%%�ر�FTP���ӳ�������", e);
		}
    }
    
    
    
    /**
	 * 
	 * @description:���÷���  �����ļ�������ȡ�ļ�����ʱ��㣬�����ļ���������ʱ��㣺20131218_10
	 * �ļ���ʵ����
	 * 		GN�����ݣ�GnC64_http_dnssession_60_20131218_105600_20131218_105659.csv	ʱ��㣺20131218_10
	 * 		MC�����ݣ�FuJianYiDong-A-IuCS-4-201408061520.txt  ʱ���Ϊ20140806-15
	 * @author:xixg
	 * @date:2014-03-26
	 * @param fileName �ļ���
	 * @return String ʱ���
	 */
	public static String getTimeByFileName(String fileName){
		String retStr = "";
		StringBuffer sb = new StringBuffer();
		try {
			//��Դ�ļ�����"_"�ָ���ȡ��ʱ�䴮
			String[] fileNameArray = fileName.split(CollectorConfInfo.splitFileNameForDateTime);
			//ȡ�ļ������ڣ��磺20131218
			String fileNameDate = fileNameArray[CollectorConfInfo.dateLocationAtFileName];
			//��ȡ��Ҫ��ʱ���ַ���
			if(fileNameDate != null && fileNameDate.length()>= CollectorConfInfo.dateSubStringEnd)
				fileNameDate = fileNameDate.substring(CollectorConfInfo.dateSubStringBegin, CollectorConfInfo.dateSubStringEnd);
			//ȡ�ļ���Сʱ����ӣ��磺105600
			String fileNameHourTime = fileNameArray[CollectorConfInfo.hourLocationAtFileName];
			//��ȡ��Ҫ��Сʱ�ַ���
			if(fileNameHourTime != null && fileNameHourTime.length()>= CollectorConfInfo.hourSubStringEnd)
				fileNameHourTime = fileNameHourTime.substring(CollectorConfInfo.hourSubStringBegin, CollectorConfInfo.hourSubStringEnd);
			sb.append(fileNameDate);
			sb.append(CollectorConfInfo.splitFileNameForDateTime);
			sb.append(fileNameHourTime);
			retStr = sb.toString();
		} catch (Exception e) {
			logger.error("%%%%%�����ļ�����"+fileName+" ��ȡʱ����������",e);
		}
		return retStr;
	}
	
	/**
	 * 
	 * @description:���÷���  ��List�й��˳�ͬһʱ����CSV�ļ�
	 * @author:xixg
	 * @date:2014-03-26
	 * @param hisPathCtlFileList ��ʷĿ¼�е�CTL�ļ�
	 * @param firstFileName ����ʱ�����ļ�
	 * @return List 
	 */    
	public static List<String> getSameTimeCsvFilesByList(List<String> allHourFileList,String inFirstFileNameDate,boolean ifDateAndHourTheSameLocation) {
		String firstFileNameDate = "";
		//������ʱ����ͬʱ�����ļ�list
		List<String> sameTimeList = new ArrayList<String>();
		if(allHourFileList==null || allHourFileList.size()==0 || inFirstFileNameDate == null || "".equals(inFirstFileNameDate)) return allHourFileList;
		firstFileNameDate = inFirstFileNameDate;
		try {
			//�����ļ������õ�Դ�����ļ����е�������Сʱ�Ƿ���ͬһ��λ�ã������ͬһ��λ�ã����ʱ����ʽҪ����ת��
			//���� MC�ӿ��ļ�true��FuJianYiDong-A-IuCS-4-201408061520.txt   ����ͬһʱ�����ļ�ʱ��ʱ����ʽҪ���ļ���һ��
			if(ifDateAndHourTheSameLocation){
				//ת���ļ���ʱ����ʽ�������20140324_11תΪ2014032411��Ϊ�����ļ�������ʱ���׼��
				firstFileNameDate = formatFileNameDate(firstFileNameDate);
			}
			//ѭ��ȡ��List�е��ļ���
			for(int i=0;i<allHourFileList.size();i++){
				String fileName = allHourFileList.get(i);
				//����ļ����в�����ʱ��㣬���List��ɾ��
				if(fileName.contains(firstFileNameDate)) {
					//�ѹ��˳���ͬһ��ʱ����CSV�ļ�����List
					sameTimeList.add(fileName);
				}
			}
		} catch (Exception e) {
			logger.error("%%%%%��List�й��˳�ͬһʱ��㣺"+firstFileNameDate+" �������ļ���������",e);
		}   
	    return sameTimeList;
	}
	
	
	 /**
	 * 
	 * @description: ת���ļ���ʱ����ʽ�������20140324_11תΪ2014032411
	 * @author:xixg
	 * @date:2013-11-23
	 * @param fileNameDate ������ļ���ʱ���ʽ
	 * @return String  ������Ҫ���ļ�����ʽ
	 */
	 public static String formatFileNameDate(String fileNameDate){
		//�ļ���ʱ���ʽ2014032411
		String timeOfFileName = "";
    	try {
		    if(fileNameDate!=null && !"".equals(fileNameDate)){
		    	String[] timeOfFileNameArray = fileNameDate.split(CollectorConfInfo.splitFileNameForDateTime);
		    	//�ļ���ʱ���20140324_11תΪ2014032411
		    	if(timeOfFileNameArray.length>1){
		    		timeOfFileName = timeOfFileNameArray[0]+timeOfFileNameArray[1];
		    	}
		    }
		} catch (Exception e) {
			logger.error("#####ת���ļ���ʱ����ʽ���������20140324_11תΪ2014032411����Ϊ���ļ���ʱ����ַ���Ϊ��"+fileNameDate,e);
		}
		return timeOfFileName;
	 }
	 
	
	 
	 /**
	 * 
	 * @description: ����һ���ļ�����
	 * @author:xixg
	 * @date:2013-11-23
	 * @param fileNameDate ������ļ���ʱ���ʽ
	 * @return String  ������Ҫ���ļ�����ʽ
	 * @throws SocketException 
	 * @throws FTPConnectionClosedException 
	 */
	 public static boolean ftpDownloadOneFile (FTPClient ftpClient,String fileName,String threadName,String savePath) throws  SocketException, FTPConnectionClosedException{
		 boolean downloadFlag = false;
		 OutputStream outputStream = null;
		 InputStream inputStream = null;
		 try {
			//�����ļ��Ŀ�ʼʱ��
			long startTime = System.currentTimeMillis();
			//�м��ļ�ȫ��
			String tmpFileName = savePath  + fileName + CollectorConfInfo.ftpTmpFileNameSuffix;
			//ʵ�����ļ�ʵ��
			File tmpFile = new File(tmpFileName);
			//ʵ�����ļ������
			outputStream = new FileOutputStream(tmpFile);
			//FTP����
			inputStream = ftpClient.retrieveFileStream(fileName);
    		if(inputStream == null){
    			logger.error("%%%%%�̣߳�"+threadName+"��ȡFTP�ļ�"+fileName+"����Ϊ�գ�����ʧ�ܣ�");
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
	    						logger.info("%%%%%ɨ�����η��������ļ�" + fileName + "�����ڣ�ɾ����Ӧ�ļ����ļ�");
	    	    				String ctlFileName = fileName.replace(CollectorConfInfo.dataSourceFileSuffixName,CollectorConfInfo.ctlSourceFileSuffixName);
	    	    				ftpClient.deleteFile(ctlFileName);
	    					}
	    				}
	    			}
    			}
    			return false;
    		}
    		//����FTP���ػ�����
    		byte[] buffer = new byte[CollectorConfInfo.downloadBuffersize*1024*1024];
            int c;
            while ((c = inputStream.read(buffer)) != -1) {
            	//������д�ļ�
            	outputStream.write(buffer, 0, c);
            }
            ftpClient.getReply();
            //�ļ�д��󣬹ر����������
            if(outputStream != null){
				outputStream.close();
			}
            //�ļ�д��󣬹ر�����������
			if(inputStream != null){
				inputStream.close();
			}
			//�����ļ��Ľ���ʱ��
    		long endTime = System.currentTimeMillis();
			//�����ļ��ܹ���ʱ
			long totalTime = endTime - startTime;
			logger.info("#####�̣߳�"+threadName+"�����ļ�:"+fileName+"���,�ܺ�ʱ:"+totalTime);
			downloadFlag = true;
			//������ɺ���ļ�ȫ��
			String finishFileName = savePath  + fileName;
			//ʵ���������ļ�ʵ��
			File finishFile = new File(finishFileName);
			//�����ļ�������ɺ�ȥ��TMP��׺
			boolean renameFlag = tmpFile.renameTo(finishFile);
			if(renameFlag){
//				logger.info("#####�̣߳�"+threadName+"�ɹ���ȥ���ļ�:"+tmpFileName+"�ĺ�׺����"+CollectorConfInfo.ftpTmpFileNameSuffix);
			}else{
//				logger.info("#####�̣߳�"+threadName+"ʧ�ܵ�ȥ���ļ�:"+tmpFileName+"�ĺ�׺����"+CollectorConfInfo.ftpTmpFileNameSuffix);
			}
		 }catch(FTPConnectionClosedException e1){
			 throw e1;
		 }catch(SocketException e2){
			 throw e2;
		 }catch (Exception e) {
			 downloadFlag = false;
			logger.error("%%%%%�̣߳�"+threadName+"�����ļ�"+fileName+"����",e);
		 }
		 return downloadFlag;
	 }
	 
	 
	 /**
	 * 
	 * @description: ʱ���ַ�������Сʱ
	 * @author:xixg
	 * @date:2013-11-23
	 * @param dateStr ʱ�䴮
	 * @return String �������ʱ�䴮
	 */
	 public static String incrementHourStr(String dateStr){
		//�ļ���ʱ���ʽ2014032411
		String returnHourStr = "";
		try {
			//����ʱ���ʽ��ʵ��
	       DateFormat format = new SimpleDateFormat(CollectorConstant.FORMAT_0F_HOUR);   
           Date date = format.parse(dateStr);   
           Calendar calendar = Calendar.getInstance();
           //���ô����ʱ��
           calendar.setTime(date);
           //����Сʱ
           calendar.add(calendar.HOUR, CollectorConfInfo.incrementHourValue);
           returnHourStr = format.format(calendar.getTime());
		} catch (Exception e) {
			logger.error("%%%%%ʱ�䣺"+dateStr+"����"+CollectorConfInfo.incrementHourValue+"Сʱ����",e);
		}
		return returnHourStr;
	 }
	 
	 /**
	 * 
	 * @description:���÷���  ����ָ����ʱ���ʽ ��õ�ǰϵͳʱ��
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
			logger.error("%%%%%��ȡ��ǰϵͳʱ�������",e);
		}
		return dataStr ;
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
	 * @throws FTPConnectionClosedException 
	 * @throws SocketException 
	 */
	public static boolean mvOrDeleteFtpSourceDataFile(FTPClient ftpClient,String sourceDataPathString,String dataFileName,
			String mvSourceDataFilePath,String threadName) throws FTPConnectionClosedException, SocketException{
		boolean returnFlag = false;
		try {
			//��ȡ�����ļ������õ��Ƿ�ɾ��Դ�����ļ���ʶ
			if(CollectorConfInfo.ifNeedDeleteSourceDataFile){
				//ɾ��Զ��FTP�������Ŀ����ļ�
				boolean deleteDataFlag = ftpClient.deleteFile(dataFileName);
				if(deleteDataFlag ){
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ���ɾ��FTPԴ�����ļ�:"+dataFileName);
				}else{
					returnFlag = false; 
					logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵ�ɾ��FTPԴ�����ļ�:"+dataFileName);
				}
			}
			//��ȡ�����ļ������õ��Ƿ��ƶ�Դ���ݱ�ʶ
			if(CollectorConfInfo.ifNeedMvSourceDataFile){
				//�ƶ�Զ��FTP�������Ŀ����ļ���Ŀ��Ŀ¼
				boolean renameDataFlag = ftpClient.rename(sourceDataPathString+dataFileName, mvSourceDataFilePath+dataFileName);
				if(renameDataFlag){
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ��ؽ������ļ�:"+dataFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceDataFilePath);
				}else{
					returnFlag = false;
					logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵؽ������ļ�:"+dataFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceDataFilePath);
				}
			}
		}catch(FTPConnectionClosedException e1){
			 throw e1;
		 }catch(SocketException e2){
			 throw e2;
		 } catch (Exception e) {
			returnFlag = false;
			logger.error("%%%%%�ƶ�����ɾ��Դ�����ļ�����������",e);
		}
		return returnFlag ;
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
	public static boolean mvOrDeleteFtpSourceCtlFile(FTPClient ftpClient,String sourceDataPathString,String ctlFileName,
			String mvSourceCtlFilePath,String threadName){
		boolean returnFlag = false;
		try {
			//��ȡ�����ļ������õ��Ƿ�ɾ��Դ�����ļ���ʶ
			if(CollectorConfInfo.ifNeedDeleteSourceCtlFile){
				//ɾ��Զ��FTP�������Ŀ����ļ�
				boolean deleteCtlFlag = ftpClient.deleteFile(ctlFileName);
				if(deleteCtlFlag ){
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ���ɾ��FTPԴ�����ļ�:"+ctlFileName);
				}else{
					returnFlag = false;
					logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵ�ɾ��FTPԴ�����ļ�:"+ctlFileName);
				}
			}
			//��ȡ�����ļ������õ��Ƿ��ƶ�Դ���ݱ�ʶ
			if(CollectorConfInfo.ifNeedMvSourceCtlFile){
				//�ƶ�Զ��FTP�������Ŀ����ļ���Ŀ��Ŀ¼
				boolean renameCtlFlag = ftpClient.rename(sourceDataPathString+ctlFileName, mvSourceCtlFilePath+ctlFileName);
				if(renameCtlFlag){
					returnFlag = true;
					logger.info("#####�̣߳�"+threadName+"�ɹ��ؽ������ļ�:"+ctlFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceCtlFilePath);
				}else{
					returnFlag = false;
					logger.error("%%%%%�̣߳�"+threadName+"ʧ�ܵؽ������ļ�:"+ctlFileName+"��Ŀ¼��"+sourceDataPathString+"�ƶ���Ŀ¼��"+mvSourceCtlFilePath);
				}
			}
		} catch (Exception e) {
			returnFlag = false;
			logger.error("%%%%%�ƶ�����ɾ��FTP�����ļ�����������",e);
		}
		return returnFlag ;
	}
	/**
	 * @description:�������ļ���һ��·���ƶ�����һ��·��
	 * @param fromPath���ļ�ԭ����·��
	 * @param ToPath���ļ���Ҫ�ƶ�����Ŀ��·��
	 * @param fileName���ļ���
	 */
	public static void mvFile(String fromPath,String toPath, String fileName) {
		
		File fromFile = new File(fromPath + fileName);
		File toFile = new File(toPath + fileName);
		
		boolean mvFlag = fromFile.renameTo(toFile);
		if(mvFlag){
			logger.info("�ɹ��ؽ��ļ���" + fileName + "��" + fromPath + "�ƶ���·��"  + toPath);
		}else{
			logger.info("ʧ�ܵؽ��ļ���" + fileName + "��" + fromPath + "�ƶ���·��"  + toPath);
		}
		
	}
	
}





