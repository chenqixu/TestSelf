package com.frame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GreenPlumForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	public GreenPlumForm(){
	}

	public void actionPerformed(String usr, String pwd, String url, String sql) {
		String result1 = "";
		try{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}catch(Exception e){
			System.out.println("Fail loading Driver!");
			result1 = " " + result1 + "Fail loading Driver!";
			result1 = " " + result1 + e.toString();
			e.printStackTrace();
		}
		try{
			Connection db = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			//查询
			Statement st = db.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.print(rs.getString(1));
				System.out.print("  ");
				System.out.println(rs.getString(2));
			}
			afterQueryProcess(st, db, rs);//查询结束后执行操作
		}catch(Exception e){
			System.out.println("Connection URL or username or password errors!");
			result1 = " " + result1 + "Connection URL or username or password errors!";
			result1 = " " + result1 + e.toString();
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询结束后执行操作
	 * 
	 * @param st
	 * @param connobj
	 * @param conn
	 * @param rset
	 */
	private void afterQueryProcess(Statement st, Connection conn, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (st != null) {
				st.close();
				st = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (conn != null) {
				if (!conn.isClosed())
					conn.close();
				conn = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GreenPlumForm gpf = new GreenPlumForm();

		String usr = "gpbase";
		String pwd = "gpbaseFZ2#";
//		String pwd = "000000";
		String url = "jdbc:postgresql://10.46.219.48:5432/bigdatagp";
		String sql = "SELECT * FROM gpbase.cfg_area_sector_info limit 10";
//		String url = "jdbc:postgresql://10.1.4.88:5432/bigdatagp";
//		String sql = "SELECT * FROM cfg_area_sector_info limit 10";
		if(args.length==2){
			usr = args[0];
			pwd = args[1];
		}
		
		gpf.actionPerformed(usr, pwd, url, sql);
	}
}
