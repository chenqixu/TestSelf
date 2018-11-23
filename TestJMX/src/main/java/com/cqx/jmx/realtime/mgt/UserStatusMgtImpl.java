package com.cqx.jmx.realtime.mgt;

import java.util.HashMap;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.ThreadUtilStatus;
import com.cqx.jmx.realtime.util.ThreadUtil;
import com.cqx.jmx.util.JMXFactory;

public class UserStatusMgtImpl {
	//配置文件
	private Common common;
	private ThreadUtilStatus tus;	

	public void setTus(ThreadUtilStatus tus) {
		this.tus = tus;
	}

	/**
	 * 业务处理，并发查询
	 * */
	public void queryUserbyStation(Map<String, String> paramsMap) {
		ThreadUtil.init();
		ThreadUtil tu = new ThreadUtil(paramsMap, common);
		tus.add(tu);
		//获取结果
		tu.getResult();		
	}
	
	public static void main(String[] args) {
		final Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("lac_ci", "1");
		paramsMap.put("is_residentuser", "0");
		final UserStatusMgtImpl usm = new UserStatusMgtImpl();
		//每2秒进行一次查询
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(2000);						
						usm.queryUserbyStation(paramsMap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		//进行监控
		ThreadUtilStatus tus = new ThreadUtilStatus();
		usm.setTus(tus);
		JMXFactory.startJMX("ThreadUtilStatus", tus);
	}
}
