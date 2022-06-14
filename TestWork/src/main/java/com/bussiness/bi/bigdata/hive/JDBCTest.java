package com.bussiness.bi.bigdata.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest {
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	private static String url = "jdbc:hive2://10.1.8.75:10000/default";
	private Connection conn = null;
	
	public JDBCTest(){
		initConnetion();
	}
	
	public Connection getConn(){
		return conn;
	}
	
	private Connection initConnetion(){
		try {
			Class.forName(driverName);
			// 默认使用端口10000, 使用默认数据库，用户名密码默认 hive 服务器登录用户名 hive 登录密码
			conn = DriverManager.getConnection(url, "hive", "hive");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	private Statement getStatement(){
		Statement stmt = null;
		try {
			if (conn!=null)
				stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}
	
	private void executeSql(String sql){
		Statement stmt = getStatement();
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		release(null, stmt, null);
	}
	
	private void executeQuery(String sql){
		Statement stmt = getStatement();
		ResultSet res = null;
		try {
			System.out.println("Running:" + sql);
			res = stmt.executeQuery(sql);
			System.out.println("执行“select * query”运行结果:");
			if (res!=null)System.out.println(res.getRow());
			while (res.next()) {
				System.out.println(res.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		release(null, stmt, res);
	}
	
	private void release(Connection conn, Statement stmt, ResultSet res){
		if (conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (res!=null){
			try {
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void test(){		
		// EOF无法切换到后台运行
//		org.apache.hadoop.hive.cli.CliDriver a;
		
		// 任务异常
//		org.apache.hadoop.mapreduce.v2.app.job.impl.JobImpl a1;
		
		// 元数据
//		org.apache.hadoop.hive.metastore.tools.HiveMetaTool ht;
//		org.apache.hadoop.hive.ql.Driver qd;
//		org.apache.hadoop.hive.metastore.HiveMetaStoreClient hmsc;
//		org.apache.hadoop.hive.metastore.Warehouse wh;
		
		// 压缩
//		org.apache.hadoop.hive.ql.io.orc.CompressionKind ck;
		
		// ambari-server
//		org.apache.ambari.server.controller.AmbariServer as;
	}

	public static void main(String[] args) throws Exception {
		JDBCTest jt = new JDBCTest();
		String sql = null;
		
		// 将nl-hive-function-instr-1.0.jar加入到hdfs路径下	
//		sql = "add jar hdfs://edc01:8020/lib/hivelib/nl-hive-function-instr-1.0.jar";
//		jt.executeSql(sql);
//		// 加入到classpath下
//		sql = "create temporary function getFirstDomain as 'com.newland.bi.function.udf.getFirstDomain'";
//		jt.executeSql(sql);
//		sql = "create temporary function getSecondDomain as 'com.newland.bi.function.udf.GetSecondDomain'";
//		jt.executeSql(sql);
//		sql = "create temporary function getCatalog as 'com.newland.bi.function.udf.GetCatalog'";
//		jt.executeSql(sql);		

		// 使用自定义UDF cityudf
//		sql = "select concat(getSecondDomain(url,parse_url(url,'HOST')),'/',getCatalog(url,parse_url(url,'HOST'))) from url_test";
//		jt.executeQuery(sql);
		
		sql = "show tables";
//		sql = "select a from logs";
		jt.executeQuery(sql);
		
		// 最后释放连接资源
		jt.release(jt.getConn(), null, null);
	}
}
