package com.cqx;

import java.util.Queue;
import java.util.Random;

import org.apache.log4j.Logger;

public class TestShareThread extends Thread {
	// 日志记录器
	private static Logger logger = Logger.getLogger(TestShareThread.class);
	
	//线程安全队列，存放当前要下载的所有文件名
	private Queue<String> fileNameQueue;
	private String threadName;
	private String fileName ;
	
	public TestShareThread(Queue<String> fileNameQueue){
		this.fileNameQueue = fileNameQueue;
	}
	
	public void run(){
		//获取当前线程名称
		threadName = Thread.currentThread().getName();
		while((fileName = fileNameQueue.poll()) != null){
			// 判断是否需要退出
			if(InitCollectorFile.exit()){
				logger.info("[TestShareThread]检查到退出文件,线程退出.");
				break;
			}
			logger.info(threadName+" [poll]"+fileName);
			try {
				// 随机休眠1到9秒
				int sleep = randomSleep()*1000;
				logger.info(threadName+" 随机休眠"+sleep+"豪秒");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				logger.error(e.toString(), e);
			}
		}
	}
	
	/**
	 * 随机1个数字
	 * */
	public int randomSleep() {
		String s = "123456789";
		char[] c = s.toCharArray();
		Random random = new Random();
		return Integer.valueOf(String.valueOf(c[random.nextInt(c.length)]));
	}
}
