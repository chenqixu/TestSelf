package com.cqx.jmx.realtime.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.UserStatus;
import com.cqx.jmx.realtime.dao.UserStatusDaoImpl;

public class ThreadUtil {
	//�����ļ�
	private Common common;
	//����
	private Map<String, String> paramsMap = null;
	//Dao�߳��б�
	private List<ThreadDao> daoThreadList = new ArrayList<ThreadDao>();
	//���ӳ�
	private static List<DBUtil> dbutillist = null;
	//����ʱ��
	private long dealtime = 0;
	
	//���ӳس�ʼ��	
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
	 * ��ȡ�߳�����
	 * */
	public List<?> getDaoThreadList() {
		List<String> result = new ArrayList<String>();
		for(ThreadDao td : daoThreadList){
			result.add(td.toString());
		}
		return result;
	}

	/**
	 * ��ȡ����ʱ��
	 * */
	public long getDealtime() {
		return dealtime;
	}

	/**
	 * ���캯��
	 * */
	public ThreadUtil(Map<String, String> _paramsMap
			,Common _common){
		this.paramsMap = _paramsMap;
		this.common = _common;
	}
	
	/**
	 * ��ȡ��������崦������<br>
	 * 1�������߳��б�<br>
	 * 2����ʼ���������߳�<br>
	 * 3����ȡ�����ؽ��
	 * */
	public List<UserStatus> getResult(){
        long begin = new Date().getTime();
		//�����߳��б�
		cleanList();
		//��ʼ���������߳�
//		for(NamedParameterJdbcTemplate _npjt : npjtlist){
		for(DBUtil _db : dbutillist){
//			setAndStart(paramsMap, new UserStatusDaoImpl(_npjt, common));
			setAndStart(paramsMap, new UserStatusDaoImpl(_db, common));
		}
		//��ȡ���
		List<UserStatus> result = joinAndUnion();
        long end = new Date().getTime();
        dealtime = end-begin;
        System.out.println("getThreadResulttime:"+dealtime);
		return result;
	}
	
	/**
	 * �����߳��б�
	 * */
	private void cleanList(){
		daoThreadList.clear();
	}
	
	/**
	 * ���ò��������̼߳����߳��б�
	 * */
	private void setAndStart(Map<String, String> paramsMap, UserStatusDaoImpl dao){
		ThreadDao tdao = new ThreadDao(dao);
		tdao.setParams(paramsMap);
		tdao.start();
		daoThreadList.add(tdao);
	}
	
	/**
	 * �ȴ��߳�����Լ�����ϲ�
	 * */
	private List<UserStatus> joinAndUnion(){
		//���ؽ��
		List<UserStatus> userstatus = new ArrayList<UserStatus>();
		try {
//			System.out.println("this:"+this+" daoThreadList:"+daoThreadList+" size:"+daoThreadList.size());
			//�ȴ��߳����
			for(ThreadDao t : daoThreadList){
				t.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//����ϲ�
		for(ThreadDao t : daoThreadList){
			if(t.getUserstatus()!=null)
				userstatus.addAll(t.getUserstatus());
		}
//		//�����߳�
//		cleanList();
		return userstatus;
	}
	
	/**
	 * Dao���̲߳����ڲ���
	 * */
	class ThreadDao extends Thread {
		private UserStatusDaoImpl dao;
		//����
		private Map<String, String> params = null;
		//���
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
