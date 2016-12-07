package com.cqx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public class TestShareMain {
	// ��־��¼��
	private static Logger logger = Logger.getLogger(TestShareMain.class);
	public static int currCycleNum = 1;
	
	public static void deal(String tag){
		String sourceDataPathString = "TestShareMain";
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
        // �ӵ�����
        for(int i=0;i<100;i++){
        	fileNameQueue.offer(tag+" "+i);    		
    	}
        // ����10���߳�
    	for(int i=0;i<10;i++){
    		Thread t = new TestShareThread(fileNameQueue);
    		InitCollectorFile.TestThreadList.add(t);
    		t.start();
    	}
	}	

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
			// ��ʼ��
			InitCollectorFile.initDataCollector(conf, exit);
			while(true){
				// �ж��Ƿ���Ҫ�˳�
				if(InitCollectorFile.exit()){
					logger.info("[TestShareMain]��鵽�˳��ļ�,�߳��˳�.");
					break;
				}
				logger.info("#####TestShareMain��ʼ���е�"+currCycleNum+"��ѭ��");
				// ҵ����
				deal(tag);
				// ѭ���ж��߳��Ƿ����
				while(InitCollectorFile.TestThreadList.size()>0){
					//������Ľ����߳�
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
