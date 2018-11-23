package com.cqx.jmx.realtime.dao;

import java.util.List;
import java.util.Map;

import com.cqx.jmx.realtime.bean.Common;
import com.cqx.jmx.realtime.bean.UserStatus;
import com.cqx.jmx.realtime.util.DBUtil;

public class UserStatusDaoImpl {
	//配置文件
	private Common common;
	//数据库工具类
	private DBUtil dbutil;
	
	/**
	 * 构造函数
	 * */
	public UserStatusDaoImpl(DBUtil _dbutil, Common _common){
		dbutil = _dbutil;
		common = _common;
	}	
	
	/**
	 * 通过参数查询
	 * */
	public List<UserStatus> queryUserbyStationByDataSource(Map<String, String> params) {
		return null;
	}
}
