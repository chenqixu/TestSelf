package com.newland.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import oracle.jdbc.pool.OracleDataSource;

/**
 * oracle12c
 * jdbc写法有点差别
 * 冒号变成斜杠
 * 还需要使用ojdbc7-12.1.0.2.jar
 * */
public class JDBCTest {
	private static Timer timer = new Timer();
	private static String dbUsername = "edc_addressquery";//"edc_etl_col";
	private static String dbPassword = "edc_addressquery";//"Yx_9z52t";//"edc_etl_col";
	
	public static class MyTask extends TimerTask {

        @Override
        public void run() {
        	//SERVICE
//        	String jdbcURLSERVICE = "jdbc:oracle:thin:@10.1.8.83:1521/edcetlXDB";
//        	String jdbcURLSERVICE = "jdbc:oracle:thin:@10.46.103.169:1521/edc_etl";
        	String jdbcURLSERVICE = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = 10.1.8.204)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = orapri)))";
        	//SID
//        	String jdbcURLSID = "jdbc:oracle:thin:@10.1.8.83:1521:edcetlXDB";
        	JDBCTest.test(jdbcURLSERVICE);
//        	JDBCTest.test(jdbcURLSID);
			timer.cancel();
        }
    }
	
	public static void test(String jdbcURL){
		try{
			Connection conn = null;
//			String DriverClassName = "oracle.jdbc.driver.OracleDriver";
			String DriverClassName = "oracle.jdbc.OracleDriver";
			Class.forName(DriverClassName);
			DriverManager.setLoginTimeout(15); // 超时
			conn = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
			System.out.println(new java.util.Date()+" jdbc:"+jdbcURL+" conn:" + conn);
			conn.close();
			conn = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		String jdbcURL = "jdbc:oracle:thin:@10.48.236.215:1521/edc_maint_pri";
//		String dbUsername = "edc_maint_log";
//		String dbPassword = "#D9f_we3";
//		try {
////			OracleDataSource ods = new OracleDataSource();
////			ods.setURL(jdbcURL);
////			ods.setUser(dbUsername);
////			ods.setPassword(dbPassword);
////			Properties props = new Properties();
////			props.put("oracle.jdbc.allowedLogonVersion", 12);
////			ods.setConnectionProperties(props);
////			Connection conn = ods.getConnection();
//			Connection conn = null;
//			String DriverClassName = "oracle.jdbc.driver.OracleDriver";
//			Class.forName(DriverClassName);
//			conn = DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
//			System.out.println("conn:" + conn);
//			conn.close();
//			conn = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


		MyTask task = new MyTask();
		timer.schedule(task, 5);
	}
}
