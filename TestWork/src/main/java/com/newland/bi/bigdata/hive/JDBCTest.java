package com.newland.bi.bigdata.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBCTest {
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	private static String url = "jdbc:hive2://10.1.8.2:10000/default";
	private static String sql = "";

	public static void main(String[] args) throws Exception {
		ResultSet res = null;
		Connection conn = null;
		Class.forName(driverName);
		// 默认使用端口10000, 使用默认数据库，用户名密码默认 hive 服务器登录用户名 hive 登录密码
		conn = DriverManager.getConnection(url, "hive", "hive");
		String tableName = "url_test";
		Statement stmt = conn.createStatement();
		// 将nl-hive-function-instr-1.0.jar加入到hdfs路径下
		sql = "add jar hdfs://edc01:8020/lib/hivelib/nl-hive-function-instr-1.0.jar";
		stmt.execute(sql);
//		// 加入到classpath下
//		sql = "create temporary function getFirstDomain as 'com.newland.bi.function.udf.getFirstDomain'";
//		stmt.execute(sql);
//		sql = "create temporary function getSecondDomain as 'com.newland.bi.function.udf.GetSecondDomain'";
//		stmt.execute(sql);
//		sql = "create temporary function getCatalog as 'com.newland.bi.function.udf.GetCatalog'";
//		stmt.execute(sql);
		// 使用自定义UDF cityudf
		sql = "select concat(getSecondDomain(url,parse_url(url,'HOST')),'/',getCatalog(url,parse_url(url,'HOST'))) from " + tableName;
		System.out.println("Running:" + sql);
		res = stmt.executeQuery(sql);
		System.out.println("执行“select * query”运行结果:");
		if (res!=null)System.out.println(res.getRow());
		while (res.next()) {
			System.out.println(res.getString(1));
		}
		res.close();
		res = null;
		stmt.close();
		stmt = null;
		conn.close();
		conn = null;
		
		// EOF无法切换到后台运行
		org.apache.hadoop.hive.cli.CliDriver a;
		
		// 任务异常
		org.apache.hadoop.mapreduce.v2.app.job.impl.JobImpl a1;
		
		// 元数据
		org.apache.hadoop.hive.metastore.tools.HiveMetaTool ht;
		org.apache.hadoop.hive.ql.Driver qd;
		org.apache.hadoop.hive.metastore.HiveMetaStoreClient hmsc;
		
		// ambari-server
//		org.apache.ambari.server.controller.AmbariServer as;
	}
}
