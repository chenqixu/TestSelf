package com.bussiness.bi.bigdata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimestenJDBCTest {
	public static void main(String[] args) {
		long begin = new Date().getTime();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String URL = "";
		if(args.length==1){
			URL = args[0];
		}else{
			System.out.println("args not enougth.");
			System.exit(1);
		}
		try{
			Class.forName("com.timesten.jdbc.TimesTenDriver");
			//Class.forName("com.timesten.jdbc.TimesTenClientDriver");
			con = DriverManager.getConnection(URL);
			st = con.createStatement();
			rs = st.executeQuery("select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in ('22831_216721193','22831_108252457','22831_108200235','22831_108003587','22831_34709057')");
			List<Object> list = new ArrayList<Object>();
			while(rs.next()){
				String value = rs.getString(1)+rs.getString(2)+rs.getString(3);
				list.add(value);
			}
			rs.close();
			st.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(st!=null)
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		long end = new Date().getTime();
		System.out.println("deal time:"+(end-begin)/1000.0);
	}
}
