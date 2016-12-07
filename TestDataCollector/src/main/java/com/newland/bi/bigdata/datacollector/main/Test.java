package com.newland.bi.bigdata.datacollector.main;

import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.net.ftp.FTPClient;

import com.newland.bi.bigdata.datacollector.common.CollectorFileCommon;
import com.newland.bi.bigdata.datacollector.common.InitCollectorFile;
import com.newland.bi.bigdata.datacollector.filter.DataColectorFTPFileFilter;
import com.newland.bi.bigdata.datacollector.thread.TestThread;


public class Test {
	
	public static void main(String[] args) {
//		String fileNameNum = "1234567";
//		int num = Integer.parseInt(fileNameNum.substring(fileNameNum.length()-1));
//		System.out.println(num);
//		if(num % 2 == 2-1){
//			System.out.println("T");
//		}else{
//			System.out.println("F");
//		}
//		//初始化操作
//		InitCollectorFile.initDataCollector();
//		System.out.println(InitCollectorFile.currFilterSpecificFileName);
//		//文件过滤器
//        DataColectorFTPFileFilter fileFilter = new DataColectorFTPFileFilter(InitCollectorFile.currFilterSpecificFileName);
//		//创建FTP连接，进行FTP扫描目录
//		FTPClient ftpClient = CollectorFileCommon.getFtpConnect();
//		//所有时间点的文件List
//		List<String> allHourFileList = null;
//		int sourceDataPathAraayLength = InitCollectorFile.sourceDataPathAraay.length;
//		//循环下载FTP服务器上的多个目录
//		for(int i=0;i<sourceDataPathAraayLength;i++){
//			//列出指定目录下的所有文件
//			allHourFileList = CollectorFileCommon.listFtpFiles(ftpClient,
//					InitCollectorFile.sourceDataPathAraay[i],fileFilter);
//			System.out.println(InitCollectorFile.sourceDataPathAraay[i]+" "+allHourFileList.size());
//		}

        Queue<String> fileNameQueue = null;
    	fileNameQueue = new ConcurrentLinkedQueue<String>();
    	for(int i=0;i<100;i++){
        	fileNameQueue.offer("aa"+i);    		
    	}
    	List<Thread> TestThreadList = new Vector<Thread>();
    	for(int i=0;i<10;i++){
    		Thread t = new TestThread(fileNameQueue);
    		TestThreadList.add(t);
    		t.start();
    	}
    	while(TestThreadList.size()>0){
    		for(int i=0;i<TestThreadList.size();i++){
    			if(!TestThreadList.get(i).isAlive()){
    				TestThreadList.remove(TestThreadList.get(i));
    			}
    		}
    	}
    	System.out.println("end.");
	}	
}
