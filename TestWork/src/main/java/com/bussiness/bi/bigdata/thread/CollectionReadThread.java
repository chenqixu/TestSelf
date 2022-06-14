package com.bussiness.bi.bigdata.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bussiness.bi.bigdata.utils.CollectionUtils;

public class CollectionReadThread extends CollectionRunnable {

	private static final Logger LOG = LoggerFactory.getLogger(CollectionReadThread.class);
	
	public CollectionReadThread(String sourceIp) {
		super(sourceIp);
	}

	@Override
	public void run() {
		while(flag) {
			FileContentBean fcb = null;
			while((fcb=CollectionUtils.getInstance().getMesssageQueue().poll())!=null) {
				LOG.info("当前已读取到多少行{}，内容{}，进行ack", fcb.getLinePosition(), fcb.getRecords());
				((CollectionThread)CollectionMain.getThreadlist().get(CollectionThread.class.getSimpleName())).ack(fcb.getFileMessageId());
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
