package com.newland.bi.bigdata.impala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 用户测试impala-jdbc连接
 * */
public class ImpalaJDBCTest {
	static String JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver";
	static String CONNECTION_URL = "jdbc:impala://10.1.8.75:21050/default;user=foo;password=bar;MEM_LIMIT=1000000000;REQUEST_POOL=myPool;queuename=root";
	com.cloudera.impala.jdbc41.Driver a;
	org.apache.hive.service.cli.thrift.TExecuteStatementReq t;
	public static void main(String[] args) throws Exception {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(CONNECTION_URL);
			ps = con.prepareStatement("select * from hb_userlog_query limit 1");
			rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1) + '\t');
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
			con.close();
		}
	}
}
