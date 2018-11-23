package com.spring.test.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.newland.bi.util.db.DBUtil;

public class DBUtilFactory {
	private DBUtil db = null;
	private List<Connection> connlist = new ArrayList<Connection>();
	private List<String> getconntimelist = new ArrayList<String>();
	private List<String> releaseconntimelist = new ArrayList<String>();
	private static DBUtilFactory duf = new DBUtilFactory();
	
	private DBUtilFactory() {
		init();
	}
	
	private static DBUtilFactory getInstance() {
		if (duf == null)
			synchronized(duf) {
				if (duf == null) duf = new DBUtilFactory();
			}
		return duf;
	}
	
	/**
	 * 初始化
	 * */
	private void init() {
		db = new DBUtil("ywxx");
	}
	
	/**
	 * 取出一个连接，加入列表，如果已经存在，则不新增
	 * */
	private synchronized Connection addConnListAndReturn() {
		long begin = new Date().getTime();
		Connection conn = db.getConnection();
		boolean flag = false;
		for(Connection c : connlist) {
			if(c.equals(conn)) {
				flag = true;
				break;
			}
		}
		if(!flag) connlist.add(conn);
		long end = new Date().getTime();
		getconntimelist.add("[getConn]"+(end-begin));
		return conn;
	}
	
	public static int getPoolSize() {
		return getInstance().connlist.size();
	}
	
	private synchronized int getConnListActiveSize() {
		int activesize = connlist.size();			
		for(Connection c : connlist) {
			try {
				if(c.isClosed()) activesize--;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return activesize;
	}
	
	public static int getActiveSize() {
		return getInstance().getConnListActiveSize();
	}
	
	/**
	 * 获取连接
	 * */
	public static Connection getConnetion() {		
		return getInstance().addConnListAndReturn();
	}
	
	private void releaseConnction(Connection conn) {
		if(conn != null)
			synchronized(conn) {
				long begin = new Date().getTime();
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				long end = new Date().getTime();
				releaseconntimelist.add("[releaseConn]"+(end-begin));
			}
	}
	
	/**
	 * 释放连接
	 * */
	public static void releaseConn(Connection conn) {
		getInstance().releaseConnction(conn);
	}
	
	public static List<String> getConnTimeList() {
		return getInstance().getconntimelist;
	}
	
	/**
	 * 并发测试
	 * */
	public static void connListDeal() {
		List<Thread> tlist = new ArrayList<Thread>();
		// 启动
		for(int i=0;i<10;i++){
			tlist.add(getInstance().new ConnThread());
			tlist.get(i).start();
		}
		// 等待
		for(int i=0;i<10;i++){
			try {
				tlist.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	class ConnThread extends Thread {
		public void run() {
			// 获取连接
			Connection conn = DBUtilFactory.getConnetion();
			// 业务处理
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 释放连接
			DBUtilFactory.releaseConn(conn);
		}
	}
}
