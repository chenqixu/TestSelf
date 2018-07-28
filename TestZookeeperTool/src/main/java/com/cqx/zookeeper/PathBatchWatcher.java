package com.cqx.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class PathBatchWatcher implements IChildWatchCallback {
	private static final Logger logger = LoggerFactory.getLogger(PathBatchWatcher.class);
	private LinkedBlockingQueue<ChangeNodeInfo> queueStatusInfo;

	public PathBatchWatcher(LinkedBlockingQueue<ChangeNodeInfo> queueStatusInfo) {
		this.queueStatusInfo = queueStatusInfo;
	}

	@Override
	public void childNodeAddAction(ChildData childData) {
		String strZkpath = childData.getPath();
		logger.info("###strZkpath###:"+strZkpath);
		byte[] bytesData = childData.getData();
		logger.info("###bytesData###:"+bytesData);
		if (bytesData == null || bytesData.length == 0) {
			logger.info("###bytesData == null || bytesData.length == 0###");
			return;
		}
		Map map = (Map<String, String>) SerializationUtils.deserialize(bytesData);
		if (map == null) {
			logger.info("###map == null###");
			return;
		}
		logger.info("###childNodeAddAction###:"+map.toString());
	}

	@Override
	public void childNodeRemoveAction(ChildData childData) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void childNodeUpdateAction(ChildData childData) {
		// TODO Auto-generated method stub		
	}
}
