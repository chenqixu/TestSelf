package com.spring.test.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.spring.test.bean.UserStatus;

public class TimestenTest {
	private NamedParameterJdbcTemplate npjt;
	public TimestenTest(){}
	public TimestenTest(NamedParameterJdbcTemplate _npjt){
		this.npjt = _npjt;
	}
	
	public Connection getConn(){
        long begin = new Date().getTime();
        Connection result = null;
        try{
                result = ((JdbcTemplate) npjt.getJdbcOperations()).getDataSource().getConnection();
        }catch(Exception e){
                e.printStackTrace();
        }
        long end = new Date().getTime();
		System.out.println("getDataSource:"+((JdbcTemplate) npjt.getJdbcOperations()).getDataSource()
				+" conn:"+result.hashCode()
				+" getConn time:"+(end-begin)/1000.0);
        return result;
	}

	public void queryUserbyStationByConn(Map<String, String> params){
        Statement st = null;
        ResultSet rs = null;
        Connection conn = getConn();
        if(conn!=null){
        	try{
                long begin = new Date().getTime();
                String sql = "select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in ("
                		+"'23028_55826049'"+")";
            	st = conn.createStatement();
            	rs = st.executeQuery(sql);
                List<Object> list = new ArrayList<Object>();
                while(rs.next()){
                	String value = rs.getString(1)+rs.getString(2)+rs.getString(3);
                	list.add(value);
                }
                long end = new Date().getTime();
                System.out.println("querySQL time:"+(end-begin)/1000.0);
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
	}
	
	public List<UserStatus> queryUserbyStationByDataSource(Map<String, String> params){
		List<UserStatus> userlist = null;
		String sql = "";
		Map<String, Object> paramlist = new HashMap<String, Object>();
		paramlist.put("lac_ci", Arrays.asList(params.get("lac_ci").split(",")));
		sql = "select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in (:lac_ci) ";
		userlist = npjt.query(sql, paramlist, ParameterizedBeanPropertyRowMapper.newInstance(UserStatus.class));
		return userlist;
	}
	
	class ThreadUtil {
		private Map<String, String> paramsMap = null;
		private List<NamedParameterJdbcTemplate> npjtlist = null;
		private List<ThreadDao> daoThreadList = new ArrayList<ThreadDao>();
		
		public ThreadUtil(Map<String, String> _paramsMap
				, List<NamedParameterJdbcTemplate> _npjtlist){
			this.paramsMap = _paramsMap;
			this.npjtlist = _npjtlist;
		}
		
		public List<UserStatus> getResult(){
			cleanList();
			for(NamedParameterJdbcTemplate _npjt : npjtlist){
				setAndStart(paramsMap, new TimestenTest(_npjt));
			}
			return joinAndUnion();
		}
		
		private void cleanList(){
			daoThreadList.clear();
		}
		
		private void setAndStart(Map<String, String> paramsMap, TimestenTest dao){
			ThreadDao tdao = new ThreadDao(dao);
			tdao.setParams(paramsMap);
			tdao.start();
			daoThreadList.add(tdao);
		}
		
		private List<UserStatus> joinAndUnion(){
			List<UserStatus> userstatus = new ArrayList<UserStatus>();
			try {
				for(ThreadDao t : daoThreadList){
					t.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(ThreadDao t : daoThreadList){
				if(t.getUserstatus()!=null)
					userstatus.addAll(t.getUserstatus());
			}
			return userstatus;
		}
		
		class ThreadDao extends Thread {
			private TimestenTest dao;
			private Map<String, String> params = null;
			private List<UserStatus> userstatus = null;
			public ThreadDao(TimestenTest _dao){
				this.dao = _dao;
			}
			public List<UserStatus> getUserstatus() {
				return userstatus;
			}
			public void setParams(Map<String, String> params) {
				this.params = params;
			}
			@Override
			public void run() {
				try {
					//this.userstatus = this.dao.queryUserbyStationByDataSource(params);
					this.dao.queryUserbyStationByConn(params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}		
		}
	}
	
	class TestThread extends Thread {
		Map<String, String> params = null;
		List<NamedParameterJdbcTemplate> npjtlist = null;
		public TestThread(Map<String, String> _params, List<NamedParameterJdbcTemplate> _npjtlist){
			params = _params;
			npjtlist = _npjtlist;
		}
		public void run(){
			long begin = new Date().getTime();
			System.out.println(this+" "+test(params, npjtlist));
			long end = new Date().getTime();
			System.out.println(this+" deal time:"+(end-begin)/1000.0);
		}
	}
	
	public static List<UserStatus> test(Map<String, String> params, List<NamedParameterJdbcTemplate> npjtlist){
		ThreadUtil tu = new TimestenTest().new ThreadUtil(params, npjtlist);
		return tu.getResult();
	}
	
	public static void main(String[] args) {
		String lac_ci = "";
		if(args.length==1){
			lac_ci = args[0];
		}else{
			System.out.println("args not enougth.");
			System.exit(1);
		}
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:resources/spring/spring-base.xml");
		Map<String, String> params = new HashMap<String, String>();
		params.put("lac_ci", lac_ci);
		List<NamedParameterJdbcTemplate> npjtlist = new ArrayList<NamedParameterJdbcTemplate>();
		//for(int i=1;i<17;i++){
		//	npjtlist.add((NamedParameterJdbcTemplate)ctx.getBean("namedParameterJdbcTemplateTT"+i));
		//}
		npjtlist.add((NamedParameterJdbcTemplate)ctx.getBean("namedParameterJdbcTemplateTT1"));
		//第一次测试
		test(params, npjtlist);
		//并发测试
		List<TestThread> tlist = new ArrayList<TestThread>();
		for(int j=0;j<10;j++){
			tlist.add(new TimestenTest().new TestThread(params, npjtlist));
			tlist.get(j).start();
		}
		for(TestThread tt: tlist){
			try {
				tt.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		System.out.println("Thread.sleep 2000...");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		tlist.clear();
//		for(int j=0;j<10;j++){
//			tlist.add(new TimestenTest().new TestThread(params, npjtlist));
//			tlist.get(j).start();
//		}
//		for(TestThread tt: tlist){
//			try {
//				tt.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}