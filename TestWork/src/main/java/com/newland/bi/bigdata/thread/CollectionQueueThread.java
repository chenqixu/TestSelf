package com.newland.bi.bigdata.thread;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.bi.bigdata.utils.CollectionUtils;
import com.newland.bi.bigdata.utils.OtherUtils;
import com.newland.storm.component.etl.common.model.impl.ExtractFileInfo;

/**
 * 获取文件队列线程-异步
 * 根据ip和路径获取文件
 * */
public class CollectionQueueThread extends CollectionRunnable {

	private static final Logger LOG = LoggerFactory.getLogger(CollectionQueueThread.class);
	
	public CollectionQueueThread(String uuid) {
		super(uuid);
		LOG.info("启动CollectionQueueThread");
	}
	
	/**
	 * 如果文件队列少于100个，就发起请求，获取新文件加入文件队列
	 * 每次休眠60秒
	 * */
	@Override
	public void run() {
		while(flag) {
			if(CollectionUtils.getInstance().getToDoFilesQueue().size()<10){
				LOG.info("队列小于10");
				Collection<ExtractFileInfo> ec = CollectionUtils.getInstance().queryFtpFileListByParams(this.uuid);
				if(ec!=null) {
					LOG.info("增加到队列");
					CollectionUtils.getInstance().getToDoFilesQueue().addAll(ec);
				}
			}
			LOG.info("休眠2秒");
			OtherUtils.sleep(2000);
		}
	}
}
