package com.newland.bi.bigdata.db;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceTest {
	public static DataSourceTest dst = new DataSourceTest();
	private String dbDriver = "oracle.jdbc.driver.OracleDriver";
	private String dbUserName= "edc_cfg";
	private String dbPassword= "edc_cfg";
	private String dbURL= "jdbc:oracle:thin:@10.1.8.79:1521/edc_cfg_pri";	
	private DataSource dataSource;
	public DataSource getConfSource(){
		return dataSource;
	}
	private DataSourceTest(){
		dataSource = setupDataSource(dbDriver,
				dbUserName, dbPassword,	dbURL);
	}
	public static DataSourceTest getInstance(){
		if(dst==null)dst = new DataSourceTest();
		return dst;
	}
	/**
	 * 创建连接池
	 * 
	 * @param connectURI
	 * @return
	 */
	private DataSource setupDataSource(String driver, String username,
			String password, String url) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setUrl(url);
		// 最大活动连接
		ds.setMaxActive(5);
		// 最小空闲连接
		ds.setMinIdle(2);
		// 最大空闲连接
		ds.setMaxIdle(3);
//		ds.setValidationQuery("select 1 from dual");
//		ds.setValidationQueryTimeout(1000);
//		ds.setTestOnBorrow(false);
//		ds.setTestWhileIdle(true);
//		ds.setTimeBetweenEvictionRunsMillis(15000);
		return ds;
	}
	
	public static class MyTask extends TimerTask {
        @Override
        public void run() {
        	try {
				System.out.println(DataSourceTest.getInstance()
						.getConfSource().getConnection());
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
	}
	
	public static void main(String[] args) throws Exception {
//		DataSourceTest dst = new DataSourceTest();
//		System.out.println(dst.getConfSource().getConnection());
		MyTask task = new MyTask();
		Timer timer = new Timer();
		timer.schedule(task, 500, 3000);
//		org.apache.commons.compress.compressors.z.ZCompressorInputStream a;
	}
}
