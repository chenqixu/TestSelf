package com.newland.bi.bigdata.datacollector.thread;

import java.util.Queue;

public class TestThread extends Thread {
	//�̰߳�ȫ���У���ŵ�ǰҪ���ص������ļ���
	private Queue<String> fileNameQueue;
	private String threadName;
	private String fileName ;
	public TestThread(Queue<String> fileNameQueue){
		this.fileNameQueue = fileNameQueue;
	}
	public void run(){
		//��ȡ��ǰ�߳�����
		threadName = Thread.currentThread().getName();
		while((fileName = fileNameQueue.poll()) != null){
			System.out.println(threadName+" "+fileName);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
