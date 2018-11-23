package com.newland.bi.bigdata.datacollector.thread;

import java.util.Queue;

public class TestThread extends Thread {
	//线程安全队列，存放当前要下载的所有文件名
	private Queue<String> fileNameQueue;
	private String threadName;
	private String fileName ;
	public TestThread(Queue<String> fileNameQueue){
		this.fileNameQueue = fileNameQueue;
	}
	public void run(){
		//获取当前线程名称
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
