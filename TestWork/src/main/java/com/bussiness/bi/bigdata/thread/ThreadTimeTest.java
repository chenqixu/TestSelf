package com.bussiness.bi.bigdata.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThreadTimeTest {
	private List<ThreadDao> daoThreadList = new ArrayList<ThreadDao>();
	class ThreadDao extends Thread {
		private long delaytime = 0;
		private String result = null;
		public String getResult(){
			return result;
		}
		public ThreadDao(long _delaytime){
			delaytime = _delaytime;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(delaytime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * 清理线程列表
	 * */
	private void cleanList(){
		daoThreadList.clear();
	}
	
	/**
	 * 设置参数启动线程加入线程列表
	 * */
	private void setAndStart(long delaytime){
		ThreadDao tdao = new ThreadDao(delaytime);
		tdao.start();
		daoThreadList.add(tdao);
	}
	
	/**
	 * 等待线程完成以及结果合并
	 * */
	private List<String> joinAndUnion(){
		List<String> userstatus = new ArrayList<String>();
		try {
			//等待线程完成
			for(ThreadDao t : daoThreadList){
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//结果合并
		for(ThreadDao t : daoThreadList){
			if(t.getResult()!=null)
				userstatus.add(t.getResult());
		}
		return userstatus;
	}
	
	/**
	 * 获取结果，具体处理流程<br>
	 * 1、清理线程列表<br>
	 * 2、初始化及启动线程<br>
	 * 3、获取并返回结果
	 * */
	public List<String> getResult(){
        long begin = new Date().getTime();
		//清理线程列表
		cleanList();
		//启动线程
		for(int i=0;i<16;i++){
			setAndStart(1000L);
		}
		//获取结果
		List<String> result = joinAndUnion();
        long end = new Date().getTime();
        System.out.println("getResult time:"+(end-begin)/1000.0);
		return result;
	}
	
	class ThreadDo extends Thread{
		private ThreadTimeTest ttt = null;
		public ThreadDo(ThreadTimeTest _ttt){
			ttt = _ttt;
		}
		public void run(){
	        long begin = new Date().getTime();
			ttt.getResult();
	        long end = new Date().getTime();
	        System.out.println(this+" ThreadTimeTest time:"+(end-begin)/1000.0);
		}
	}
	
	public static void main(String[] args) {
		for(int i=0;i<10;i++){
			new ThreadTimeTest().new ThreadDo(new ThreadTimeTest()).start();
		}
	}
}
