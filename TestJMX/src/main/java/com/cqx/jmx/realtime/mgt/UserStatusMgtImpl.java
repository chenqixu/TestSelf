package com.cqx.jmx.realtime.mgt;

import java.util.HashMap;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.ThreadUtilStatus;
import com.cqx.jmx.realtime.util.ThreadUtil;
import com.cqx.jmx.util.JMXFactory;

public class UserStatusMgtImpl {
	//�����ļ�
	private Common common;
	private ThreadUtilStatus tus;	

	public void setTus(ThreadUtilStatus tus) {
		this.tus = tus;
	}

	/**
	 * ҵ����������ѯ
	 * */
	public void queryUserbyStation(Map<String, String> paramsMap) {
		ThreadUtil.init();
		ThreadUtil tu = new ThreadUtil(paramsMap, common);
		tus.add(tu);
		//��ȡ���
		tu.getResult();		
	}
	
	public static void main(String[] args) {
		final Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("lac_ci", "1");
		paramsMap.put("is_residentuser", "0");
		final UserStatusMgtImpl usm = new UserStatusMgtImpl();
		//ÿ2�����һ�β�ѯ
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
		//���м��
		ThreadUtilStatus tus = new ThreadUtilStatus();
		usm.setTus(tus);
		JMXFactory.startJMX("ThreadUtilStatus", tus);
	}
}
