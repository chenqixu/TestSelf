package com.bussiness.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.nl.sri.sam.service.ISaInfoQueryer;
import com.nl.sri.sam.service.impl.SaInfoQueryer;

/**
 * @ClassName DbConnect
 * @Description 数据库连接
 * @author 陈棋旭
 * @version 1.1 2015-09-16 陈棋旭修改,合并程序配置文件统一读数据库优化
 * @version 1.2 2015-10-20 陈棋旭修改,数据库修改成oracle,并进行统一密码改造
 * */
public class DbConnect {
	// 日志记录器
	private static Logger logger = Logger.getLogger(DbConnect.class);
	
	// 数据库查询配置表唯一标识ID
	private String id = "";
	// 数据库查询配置表
	private String conftable = "";
	// 数据库连接
	private Connection db_conn =  null;

	public void setConftable(String conftable) {
		this.conftable = conftable;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Connection getDb_conn() {
		return db_conn;
	}
	
	public void getMysqlDbConn() {
		try{
			Connection conn = null;
			String DriverClassName="com.mysql.jdbc.Driver";
			String dbUrl="jdbc:mysql://127.0.0.1:3306/accacount";
			String dbUsername="acc";
			String dbPassword="123456";
	        // 加载数据库驱动类
			Class.forName(DriverClassName);
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//			logger.info("##conn##:"+conn);
			this.db_conn = conn;
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	/**
	 * @description 公用方法 通过参数获取数据库连接
	 * @author 陈棋旭
	 * @date 2015-09-16
	 * @version 1.2 2015-10-20 陈棋旭修改,数据库修改成oracle,并进行统一密码改造
	 * @return Connection 数据库连接对象
	 */
	public void getDbConn() {
		Connection conn = null;
		String DriverClassName="oracle.jdbc.driver.OracleDriver";
		String dbUrl="jdbc:oracle:thin:@10.1.0.242:1521:ywxx";
		String dbUsername="d:/Work/ETL/统一密码改造/ESainfo.txt";
		String dbPassword="ywxx.db.bassweb";
		// 数据库用户名
        String user = "";
        // 数据库密码
        String password = "";
        int nRes = 0;
		try {
	    	// 统一密码管理
		    ISaInfoQueryer cSaInfoQueryer = new SaInfoQueryer();
		    cSaInfoQueryer.loadLibrary("nothing to load"); // 已标注为@Deprecated 保留此接口为兼容旧版，无实际作用
		    // 用户名
		    StringBuffer szUserName = new StringBuffer();
		    // 密码
		    StringBuffer szPasswd = new StringBuffer();
		    nRes = cSaInfoQueryer.openAndQueryByName(dbUsername, dbPassword, szUserName, szPasswd);		    
	        user = szUserName.toString(); 
	        password = szPasswd.toString();
			logger.info("##统一密码管理 解密user##:"+user);
			logger.info("##统一密码管理 解密password##:"+password);
	        // 加载数据库驱动类
			Class.forName(DriverClassName);
			conn = DriverManager.getConnection(dbUrl, user, password);
			logger.info("##conn##:"+conn);
		} catch (Exception e) {
			logger.error("#####连接数据库出错！统一密码获取结果:"+ nRes + ",数据库URL=" + dbUrl
					+ ",统一密码文件：" + dbUsername + ",统一密码登陆用户标识："
					+ dbPassword + ",数据库用户名:" + user + ",数据库密码:" + password , e);
		}
		if (conn == null)
			logger.info("#####连接数据库出错！统一密码获取结果:"+ nRes + ",数据库URL=" + dbUrl
					+ ",统一密码文件：" + dbUsername + ",统一密码登陆用户标识："
					+ dbPassword + ",数据库用户名:" + user + ",数据库密码:" + password);
		this.db_conn = conn;
//		return conn;
	}
	
	/**
	 * @description 公用方法 通过参数查询数据库中的配置信息
	 * @author 陈棋旭
	 * @date 2015-09-16
	 * @param id 数据库查询配置表唯一标识ID
	 * @param name 参数名称
	 * @param default_value 默认值
	 * @return String 数据库配置信息
	 * */
	public String queryValueByNameFormDb(String name){
		return queryValueByNameFormDb(this.id, name, this.conftable, this.db_conn, "");
	}
	
	/**
	 * @description 公用方法 通过参数查询数据库中的配置信息
	 * @author 陈棋旭
	 * @date 2015-09-16
	 * @param id 数据库查询配置表唯一标识ID
	 * @param name 参数名称
	 * @param default_value 默认值
	 * @return String 数据库配置信息
	 * */
	public String queryValueByNameFormDb(String name, String default_value){
		return queryValueByNameFormDb(this.id, name, this.conftable, this.db_conn, default_value);
	}

	/**
	 * @description 公用方法 通过参数查询数据库中的配置信息
	 * @author 陈棋旭
	 * @date 2015-09-16
	 * @param id 数据库查询配置表唯一标识ID
	 * @param name 参数名称
	 * @param tablename 数据库查询配置表
	 * @param dbconn 数据连接
	 * @param default_value 默认值
	 * @return String 数据库配置信息
	 */
	public String queryValueByNameFormDb(String id, String name,
			String tablename, Connection dbconn, String default_value){
		// 返回值
		String value = "";
		// 查询sql
		String sql = "select value from "+tablename+" where id=? and name=?";
		// 预编译sql语句声明
		PreparedStatement pstmt =  null;
		// 结果集
		ResultSet rs = null;
		try{
			if(dbconn!=null){
				// 预编译sql
				pstmt = dbconn.prepareStatement(sql);
				// 设置第一个查询参数
				pstmt.setString(1, id);
				// 设置第二个查询参数
				pstmt.setString(2, name);
				// 查询
				rs = pstmt.executeQuery();
				if(rs!=null){
					//获得结果
					rs.next();
					value = rs.getString("value");
				}
			}
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(null, pstmt, rs);
		}catch(Exception e){
			logger.error("#####通过参数查询数据库中的配置信息出错! id:"+id
					+",name:"+name+",tablename:"+tablename+"#####", e);
			e.printStackTrace();
		}finally{
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(null, pstmt, rs);
		}
		// 如果值为空，则改成默认值
		if(value==null || value.length()==0){
			value = default_value;
		}
		return value;
	}
	
	public String queryTest(String name){
		// 返回值
		String value = "";
		// 查询sql
		String sql = "select acc_use_desc from acc_use_type where acc_use_name=?";
		// 预编译sql语句声明
		PreparedStatement pstmt =  null;
		// 结果集
		ResultSet rs = null;
		try{
			if(this.db_conn!=null){
				// 预编译sql
				pstmt = this.db_conn.prepareStatement(sql);
				// 设置第一个查询参数
				pstmt.setString(1, name);
				// 查询
				rs = pstmt.executeQuery();
				if(rs!=null){
					//获得结果
					rs.next();
					value = rs.getString("acc_use_desc");
				}
			}
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(null, pstmt, rs);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(this.db_conn, pstmt, rs);
		}
		return value;
	}
	
	/**
	 * 关闭数据库连接
	 * */
	public void closeDb_conn(){
		closeDB(this.db_conn, null, null);
	}

    /**
     * 数据库操作完成后，关闭相关的连接资源
	 */
    private void closeDB(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
        	//关闭结果集
            if (rs != null) {
                rs.close();
                rs = null;
            }
            //关闭Statement
            if (stmt != null) {
            	stmt.close();
            	stmt = null;
            }
            //关闭数据连接
            if (conn != null) {
                if (!conn.isClosed())
                    conn.close();
                conn = null;
            }
        }catch (Exception e) {
			logger.error("#####关闭数据库连接出错!#####", e);
			e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
		DbConnect dbc = new DbConnect();	
		// 数据库连接
		dbc.getDbConn();
		if(dbc.getDb_conn()!=null){
			// 设置数据库查询配置表
			dbc.setConftable("");
			// 设置数据库查询配置表唯一标识ID
			dbc.setId("");
			System.out.println(Boolean.valueOf(dbc.queryValueByNameFormDb("", "false")));
		}
	}
}
