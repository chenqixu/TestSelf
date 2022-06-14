package com.bussiness.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
//			String dbUrl="jdbc:mysql://edc-verf-mn03/hive";
//			String dbUsername="hive";
//			String dbPassword="bch";
			String dbUrl="jdbc:mysql://10.1.8.78:3306/fj_udap_oozie?useUnicode=true";
			String dbUsername="oozie";
			String dbPassword="oozie";
	        // 加载数据库驱动类
			Class.forName(DriverClassName);
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			System.out.println("conn:"+conn);
			queryTest(conn);
			conn = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String queryTest(Connection db_conn){
		// 返回值
		String value = "";
		// 查询sql
		String sql = "select * from WF_JOBS";
		// 预编译sql语句声明
		PreparedStatement pstmt =  null;
		// 结果集
		ResultSet rs = null;
		try{
			if(db_conn!=null){
				// 预编译sql
				pstmt = db_conn.prepareStatement(sql);
				// 查询
				rs = pstmt.executeQuery();
				while(rs!=null && rs.next()){
					//获得结果
//					rs.next();
					value = rs.getString(1);
					System.out.println("[1]"+value);
					break;
				}
			}
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(null, pstmt, rs);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
			closeDB(db_conn, pstmt, rs);
		}
		return value;
	}
	

	/**
	 * 关闭数据库连接
	 * */
	public static void closeDb_conn(Connection db_conn){
		closeDB(db_conn, null, null);
	}

    /**
     * 数据库操作完成后，关闭相关的连接资源
	 */
    private static void closeDB(Connection conn, PreparedStatement stmt, ResultSet rs) {
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
			e.printStackTrace();
        }
    }
}
