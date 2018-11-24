package com.cqx.netty.bean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ClientQueryBean {
	private BlockingQueue<String> queryResult;
	
	public ClientQueryBean() {
		queryResult = new LinkedBlockingQueue<String>();
	}
	
	public void put(String msg) throws Exception {
		queryResult.put(msg);
	}
	
	public String poll() {
		return queryResult.poll();
	}
	
	public String query() {
		String resultStr = null;
		int querycnt = 0;
		while(true) {
			resultStr = poll();
			if(resultStr!=null && !resultStr.equals("null")) {
				System.out.println("resultStr：" + resultStr);
				break;
			}
	        try {
	        	querycnt++;
	            TimeUnit.MILLISECONDS.sleep(100);
	            // 重试超过5次就退出
	            if(querycnt>5) {
	            	System.out.println("querycnt>5，break.");
	            	break;
	            }
	        } catch (InterruptedException e) {
	        	e.printStackTrace();
	        }
		}
		return resultStr;
	}
}
