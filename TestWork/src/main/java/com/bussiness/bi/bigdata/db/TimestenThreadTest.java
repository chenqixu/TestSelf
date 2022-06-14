package com.bussiness.bi.bigdata.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.newland.bi.util.db.DBUtil;

public class TimestenThreadTest {
	public static DBUtil dbutil = new DBUtil("tt1");
	public TimestenThreadTest(){}
	
	 public void query(){
         Connection conn = null;
         Statement st = null;
         ResultSet rs = null;
         try {
                 long begin1 = new Date().getTime();
                 conn = dbutil.getConnection();
                 long end1 = new Date().getTime();
                 System.out.println("getCon time:"+(end1-begin1)/1000.0+" conn:"+conn);
                 if (conn != null) {
                         long begin = new Date().getTime();
                         st = conn.createStatement();
                         rs = st.executeQuery("select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in ('22831_216721193','22831_108252457')");//,'22831_108200235','22831_108003587','22831_34709057')");
                         List<Object> list = new ArrayList<Object>();
                         while(rs.next()){
                                 String value = rs.getString(1)+rs.getString(2)+rs.getString(3);
                                 list.add(value);
                         }
                         long end = new Date().getTime();
                         System.out.println("deal time:"+(end-begin)/1000.0+" deal result:"+list.size());
                 }
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
			if(conn!=null)
				try {
					if(!conn.isClosed())
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	class TestThread extends Thread {
		public void run(){
			new TimestenThreadTest().query();
		}
	}
	
	public static void main(String[] args) {
		List<TestThread> tlist = new ArrayList<TestThread>();
		for(int j=0;j<30;j++){
			tlist.add(new TimestenThreadTest().new TestThread());
			tlist.get(j).start();
		}
		for(TestThread tt: tlist){
			try {
				tt.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
