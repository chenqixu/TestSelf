package com.cqx.jmx.realtime.dao;

import java.util.List;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.UserStatus;
import com.cqx.jmx.realtime.util.DBUtil;

public class UserStatusDaoImpl {
	//�����ļ�
	private Common common;
	//���ݿ⹤����
	private DBUtil dbutil;
	
	/**
	 * ���캯��
	 * */
	public UserStatusDaoImpl(DBUtil _dbutil, Common _common){
		dbutil = _dbutil;
		common = _common;
	}	
	
	/**
	 * ͨ��������ѯ
	 * */
	public List<UserStatus> queryUserbyStationByDataSource(Map<String, String> params) {
		return null;
	}
}
