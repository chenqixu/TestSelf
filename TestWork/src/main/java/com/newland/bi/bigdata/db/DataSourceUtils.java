package com.newland.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.sri.sam.service.ISaInfoQueryer;
import com.nl.sri.sam.service.impl.SaInfoQueryer;

/**
 * 数据源工具类
 * 被static修饰的成员变量和成员方法独立于该类的任何对象。
 * 也就是说，它不依赖类特定的实例，被类的所有实例共享
 * */
public class DataSourceUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(DataSourceUtils.class);	
	private static boolean isLocal = true;
	private static boolean isPool = true;
	private static String dbDriver = "oracle.jdbc.driver.OracleDriver";
	private static String dbUserName= "/bi/sam/lib/ESainfo.txt";
	private static String dbPassword= "ywxx.db.bassweb";
	private static String dbURL= "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";	
	private static DataSource dataSource;
	
	static {
		if(isLocal){
			dbUserName= "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\conf\\ESainfo.txt";
			dbPassword= "ywxx.db.newbiweb";
		}
	}

	/**
	 * 通过配置文件初始化配置,数据源
	 * */
	public static void initConfigure() {
		logger.info("##驱动DataSourceUtils.dbDriver:"+dbDriver);
		logger.info("##用户名DataSourceUtils.dbUserName:"+dbUserName);
		logger.info("##密码DataSourceUtils.dbPassword:"+dbPassword);
		logger.info("##数据库连接串DataSourceUtils.dbURL:"+dbURL);
		// 获得数据源
		if(isPool){ // 是否使用连接池
			dataSource = DataSourceUtils.setupDataSource(dbDriver,
					dbUserName, dbPassword,	dbURL);
		}
	}
	
	public static DataSource getConfSource(){
		if(isPool){ // 是否使用连接池
			if(dataSource==null){
				logger.info("##DataSourceUtils还未初始化配置,数据源!##");
			}
		}
		return dataSource;
	}
	
	// 获取连接
	public static Connection getConnection()
			throws Exception {
		 // 是否使用连接池
		if(isPool){
			return getConfSource().getConnection();
		} else {
			//统一密码管理
		    ISaInfoQueryer cSaInfoQueryer = new SaInfoQueryer();
		    cSaInfoQueryer.loadLibrary("nothing to load"); // 已标注为@Deprecated 保留此接口为兼容旧版，无实际作用
		    //用户名
		    StringBuffer szUserName = new StringBuffer();
		    //密码
		    StringBuffer szPasswd = new StringBuffer();
		    cSaInfoQueryer.openAndQueryByName(dbUserName, dbPassword, szUserName, szPasswd);
	        // 加载数据库驱动类
			Class.forName(dbDriver);
			Connection conn = DriverManager.getConnection(dbURL, szUserName.toString(), szPasswd.toString());
			return conn;
		}
	}

	// 关闭连接
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

	/**
	 * 创建连接池
	 * 
	 * @param connectURI
	 * @return
	 */
	public static DataSource setupDataSource(String driver, String username,
			String password, String url) {
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		
		//统一密码管理
	    ISaInfoQueryer cSaInfoQueryer = new SaInfoQueryer();
	    cSaInfoQueryer.loadLibrary("nothing to load"); // 已标注为@Deprecated 保留此接口为兼容旧版，无实际作用
	    //用户名
	    StringBuffer szUserName = new StringBuffer();
	    //密码
	    StringBuffer szPasswd = new StringBuffer();
	    cSaInfoQueryer.openAndQueryByName(username, password, szUserName, szPasswd);
		ds.setUsername(szUserName.toString());
		ds.setPassword(szPasswd.toString());
		ds.setUrl(url);
		// 最大活动连接
		ds.setMaxActive(5);
		// 最大空闲连接
		ds.setMinIdle(5);
		// 最小空闲连接
		ds.setMaxIdle(2);
		return ds;
	}
	
	public static boolean getDataSourceState(DataSource ds) {
		if(isPool){ // 是否使用连接池
			BasicDataSource bds = (BasicDataSource) ds;
			return bds.isClosed();
		}
		return false;
	}

	public static void shutdownDataSource(DataSource ds) throws SQLException {
		if(isPool){ // 是否使用连接池
			BasicDataSource bds = (BasicDataSource) ds;
			bds.close();
			ds = null;
		}
	}
}
