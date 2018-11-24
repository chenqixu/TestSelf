package com.cqx.netty.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskPool {
	private final Map<String, Map<String, DataBean>> mapHeartProgress = new ConcurrentHashMap<String, Map<String, DataBean>>();

	public void heartBean(String tasktemplateid, DataBean dataBean) {
		Map<String, DataBean> taskHeart = mapHeartProgress.get(tasktemplateid);
		if(taskHeart==null) {
			taskHeart = new ConcurrentHashMap<String, DataBean>();
		}
		taskHeart.put(dataBean.getThreadName(), dataBean);
		mapHeartProgress.put(tasktemplateid, taskHeart);
		System.out.println("heartBean：" + mapHeartProgress);
	}

	public synchronized String getMapHeartProgress(String tasktemplateid) {
		System.out.println("getMapHeartProgress tasktemplateid：" + tasktemplateid);
		System.out.println("mapHeartProgress：" + mapHeartProgress);
		return mapHeartProgress.get(tasktemplateid) + "";
	}
	
}
