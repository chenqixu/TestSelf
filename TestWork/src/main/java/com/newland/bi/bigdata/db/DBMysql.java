package com.newland.bi.bigdata.db;

public class DBMysql {
	public static void main(String[] args) {
		DbConnect dbc = new DbConnect();	
		// 数据库连接
		dbc.getMysqlDbConn();
		if(dbc.getDb_conn()!=null){
			System.out.println(dbc.queryTest("团购"));			
		}		
	}
}
