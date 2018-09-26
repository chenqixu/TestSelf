package com.newland.bi.bigdata.db.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCUtils {

	private static final Logger logger = LoggerFactory.getLogger(CCUtils.class);
	private static DataSource dataSource;
	private static String dbDriver = "oracle.jdbc.driver.OracleDriver";
	private static String dbUserName = "bishow";
	private static String dbPassword = "bishow";
	private static String dbURL = "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";
	private static CCUtils ccutils = new CCUtils();
	private static DBUtils dbutils;
	
	private CCUtils(){
		initConfigure();
	}
	
	private static CCUtils getInstance(){
		if(ccutils==null)
			synchronized (ccutils) {
				if(ccutils==null)
					ccutils=new CCUtils();
			}			
		return ccutils;
	}
	
	private synchronized DBUtils getDBInstance(){
		if(dbutils==null)
			dbutils = new DBUtils();
		return dbutils;
	}
	
	private class DBUtils {		
		/**
		 * 创建连接池
		 */
		private DataSource setupDataSource(String driver, String username,
				String password, String url) {
			CCDatasource ds = new CCDatasource();
			ds.setDriverClassName(driver);
			ds.setUsername(username);
			ds.setPassword(password);
			ds.setUrl(url);
			// 最大活动连接
			ds.setMaxActive(5);
			// 最大空闲连接
			ds.setMinIdle(5);
			// 最小空闲连接
			ds.setMaxIdle(2);
			return ds;
		}
		
		/**
		 * 获取配置的数据源
		 * */
		private DataSource getConfSource(){
			if(dataSource==null){
				logger.info("##CCUtils还未初始化配置,数据源!##");
			}
			return dataSource;
		}
	}
	
	/**
	 * 通过配置文件初始化配置,数据源
	 * */
	private void initConfigure() {
		logger.info("##驱动DataSourceUtils.dbDriver:"+dbDriver);
		logger.info("##用户名DataSourceUtils.dbUserName:"+dbUserName);
		logger.info("##密码DataSourceUtils.dbPassword:"+dbPassword);
		logger.info("##数据库连接串DataSourceUtils.dbURL:"+dbURL);
		// 获得数据源
		dataSource = getDBInstance().setupDataSource(dbDriver, dbUserName, dbPassword,	dbURL);
	}
	
	/**
	 * 获取连接
	 * */
	public static Connection getConnection()
			throws Exception {
		return getInstance().getDBInstance().getConfSource().getConnection();
	}
	
	/**
	 * 关闭连接
	 * */
	public static void release(Connection connection, Statement statement,
			ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Connection conn = CCUtils.getConnection();
		System.out.println(conn);
		CCUtils.release(conn, null, null);
	}
}
