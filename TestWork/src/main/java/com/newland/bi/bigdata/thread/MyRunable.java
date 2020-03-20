package com.newland.bi.bigdata.thread;

import java.util.concurrent.TimeUnit;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


public class MyRunable implements Runnable {
	// 日志类
	private static MyLogger logger = MyLoggerFactory.getLogger(MyRunable.class);
	private String threadName = this + "";
	private int index = 0;
	
	public MyRunable() {
		index++;
		logger.info("index {}", index);
	}
	
	@Override
	public void run() {
		int i = 0;
		while(i<10){
			logger.info(threadName);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			i++;
		}
	}
}
