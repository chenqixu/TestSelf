package com.cqx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public class TestShareMain {
	// 日志记录器
	private static Logger logger = Logger.getLogger(TestShareMain.class);
	public static int currCycleNum = 1;
	
	public static void deal(String tag){
		String sourceDataPathString = "TestShareMain";
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
        // 加到队列
        for(int i=0;i<100;i++){
        	fileNameQueue.offer(tag+" "+i);    		
    	}
        // 启动10个线程
    	for(int i=0;i<10;i++){
    		Thread t = new TestShareThread(fileNameQueue);
    		InitCollectorFile.TestThreadList.add(t);
    		t.start();
    	}
	}	

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
			logger.error(e.toString(), e);
		}
    }
	
	public static void main(String[] args) {
		if(args!=null && args.length==3){			
		}else{
			System.out.println("[TestShareMain]no args.");
	    	System.exit(1);
		}
		String tag = args[0];
		String conf = args[1];
		String exit = args[2];
		if(!new File(conf).exists()){
			System.out.println("[conf]"+conf+" is not a file, exception exit.");
			System.exit(1);
		}
		try{
			// 初始化
			InitCollectorFile.initDataCollector(conf, exit);
			while(true){
				// 判断是否需要退出
				if(InitCollectorFile.exit()){
					logger.info("[TestShareMain]检查到退出文件,线程退出.");
					break;
				}
				logger.info("#####TestShareMain开始进行第"+currCycleNum+"次循环");
				// 业务处理
				deal(tag);
				// 循环判断线程是否结束
				while(InitCollectorFile.TestThreadList.size()>0){
					//清理不活动的僵死线程
					clearNoAliveThread(InitCollectorFile.TestThreadList);
					Thread.sleep(1000);
				}
				currCycleNum++;
			}
		}catch(Exception e){
			logger.error(e.toString(), e);
		}
		logger.info("[TestShareMain]end.");		
	}
}
