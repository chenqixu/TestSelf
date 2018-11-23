package com.cqx.jmx.realtime.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.UserStatus;
import com.cqx.jmx.realtime.dao.UserStatusDaoImpl;

public class ThreadUtil {
	//配置文件
	private Common common;
	//参数
	private Map<String, String> paramsMap = null;
	//Dao线程列表
	private List<ThreadDao> daoThreadList = new ArrayList<ThreadDao>();
	//连接池
	private static List<DBUtil> dbutillist = null;
	//处理时间
	private long dealtime = 0;
	
	//连接池初始化	
	public static synchronized void init(){
		if(dbutillist==null){
			dbutillist = new ArrayList<DBUtil>();
			for(int i=0;i<16;i++){
				int j = i+1;
				dbutillist.add(new DBUtil("bassisbdb"+j));
//				System.out.println(dbutillist.get(i));
			}
		}
	}
	
	/**
	 * 获取线程内容
	 * */
	public List<?> getDaoThreadList() {
		List<String> result = new ArrayList<String>();
		for(ThreadDao td : daoThreadList){
			result.add(td.toString());
		}
		return result;
	}

	/**
	 * 获取处理时间
	 * */
	public long getDealtime() {
		return dealtime;
	}

	/**
	 * 构造函数
	 * */
	public ThreadUtil(Map<String, String> _paramsMap
			,Common _common){
		this.paramsMap = _paramsMap;
		this.common = _common;
	}
	
	/**
	 * 获取结果，具体处理流程<br>
	 * 1、清理线程列表<br>
	 * 2、初始化及启动线程<br>
	 * 3、获取并返回结果
	 * */
	public List<UserStatus> getResult(){
        long begin = new Date().getTime();
		//清理线程列表
		cleanList();
		//初始化及启动线程
//		for(NamedParameterJdbcTemplate _npjt : npjtlist){
		for(DBUtil _db : dbutillist){
//			setAndStart(paramsMap, new UserStatusDaoImpl(_npjt, common));
			setAndStart(paramsMap, new UserStatusDaoImpl(_db, common));
		}
		//获取结果
		List<UserStatus> result = joinAndUnion();
        long end = new Date().getTime();
        dealtime = end-begin;
        System.out.println("getThreadResulttime:"+dealtime);
		return result;
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
	private void setAndStart(Map<String, String> paramsMap, UserStatusDaoImpl dao){
		ThreadDao tdao = new ThreadDao(dao);
		tdao.setParams(paramsMap);
		tdao.start();
		daoThreadList.add(tdao);
	}
	
	/**
	 * 等待线程完成以及结果合并
	 * */
	private List<UserStatus> joinAndUnion(){
		//返回结果
		List<UserStatus> userstatus = new ArrayList<UserStatus>();
		try {
//			System.out.println("this:"+this+" daoThreadList:"+daoThreadList+" size:"+daoThreadList.size());
			//等待线程完成
			for(ThreadDao t : daoThreadList){
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//结果合并
		for(ThreadDao t : daoThreadList){
			if(t.getUserstatus()!=null)
				userstatus.addAll(t.getUserstatus());
		}
//		//清理线程
//		cleanList();
		return userstatus;
	}
	
	/**
	 * Dao多线程并发内部类
	 * */
	class ThreadDao extends Thread {
		private UserStatusDaoImpl dao;
		//参数
		private Map<String, String> params = null;
		//结果
		private List<UserStatus> userstatus = null;
		private long begin = 0;
		private long end = 0;
		public ThreadDao(UserStatusDaoImpl _dao){
			this.dao = _dao;
		}
		public List<UserStatus> getUserstatus() {
			return userstatus;
		}
		public void setParams(Map<String, String> params) {
			this.params = params;
		}
		@Override
		public void run() {
			begin = new Date().getTime();
			try {
				this.userstatus = this.dao.queryUserbyStationByDataSource(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			end = new Date().getTime();
		}
		@Override
		public String toString() {
			return super.toString()+" deal time:"+(end-begin);
		}
	}
}
