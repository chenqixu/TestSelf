package com.newland.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

/**
 * oracle12c
 * jdbc写法有点差别
 * 冒号变成斜杠
 * 还需要使用ojdbc7-12.1.0.2.jar
 * */
public class JDBCTest {

	public static void main(String[] args) {
		String jdbcURL = "jdbc:oracle:thin:@10.48.236.215:1521/edc_maint_pri";
		String dbUsername = "edc_maint_log";
		String dbPassword = "#D9f_we3";
		try {
//			OracleDataSource ods = new OracleDataSource();
//			ods.setURL(jdbcURL);
//			ods.setUser(dbUsername);
//			ods.setPassword(dbPassword);
//			Properties props = new Properties();
//			props.put("oracle.jdbc.allowedLogonVersion", 12);
//			ods.setConnectionProperties(props);
//			Connection conn = ods.getConnection();
			Connection conn = null;
			String DriverClassName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(DriverClassName);
			conn = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
			System.out.println("conn:" + conn);
			conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
