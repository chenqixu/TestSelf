package com.newland.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBMysql {
	public static void main(String[] args) {
//		DbConnect dbc = new DbConnect();	
//		// 数据库连接
//		dbc.getMysqlDbConn();
//		if(dbc.getDb_conn()!=null){
//			System.out.println(dbc.queryTest("团购"));			
//		}
		try{
			Connection conn = null;
			String DriverClassName="com.mysql.jdbc.Driver";
			String dbUrl="jdbc:mysql://edc-verf-mn03/hive";
			String dbUsername="hive";
			String dbPassword="bch";
	        // 加载数据库驱动类
			Class.forName(DriverClassName);
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			System.out.println("conn:"+conn);
			conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
