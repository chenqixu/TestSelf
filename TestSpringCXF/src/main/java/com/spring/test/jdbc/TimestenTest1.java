package com.spring.test.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.spring.test.bean.UserStatus;

@Transactional
public class TimestenTest1 {
	@Resource (name="namedParameterJdbcTemplateTT1")
	private NamedParameterJdbcTemplate npjt;
	public TimestenTest1(){}
	public TimestenTest1(NamedParameterJdbcTemplate _npjt){
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
	public List<Connection> getConnList(){
		List<Connection> cl = new ArrayList<Connection>();
		for(int i=0;i<10;i++){
			cl.add(getConn());
		}
		return cl;
	}
	public String dealParams(String[] params){
		StringBuffer sb = new StringBuffer();
		if(params!=null){
			for(String s : params){
				sb.append("'")
					.append(s)
					.append("',");
			}
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	public void queryUserbyStationByConn(Connection conn, String params){
        Statement st = null;
        ResultSet rs = null;        
        if(conn!=null){
        	try{
                long begin = new Date().getTime();
                String sql = "select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in ("
                		+dealParams(params.split(","))+")";
            	st = conn.createStatement();
            	rs = st.executeQuery(sql);
                List<Object> list = new ArrayList<Object>();
                while(rs.next()){
                	String value = rs.getString(1)+rs.getString(2)+rs.getString(3);
                	list.add(value);
                }
                long end = new Date().getTime();
                System.out.println("cnn.hash:"+conn.hashCode()+" querySQL time:"+(end-begin)/1000.0);
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
	/**
	 * 通过SPRING.JDBC模板来查询
	 * */
	public List<?> queryByJDBCTemplate(String sql, Map<String, ?> paramMap, Class<?> T){
		if(npjt!=null){
			return npjt.query(sql, paramMap, ParameterizedBeanPropertyRowMapper.newInstance(T));
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public List<UserStatus> queryUserbyStation(Map<String, String> paramMap){
		String sql = "select msisdn,fn_11 as lac_ci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in (:call_id)";
		return (List<UserStatus>) queryByJDBCTemplate(sql, paramMap, UserStatus.class);
	}
	/**
	 * 返回一个内部类对象1
	 * */
	public Test1Thread getTest1Thread(Connection _c, String _params){
		return new Test1Thread(_c, _params);
	}
	/**
	 * 返回一个内部类对象2
	 * */
	public Test2Thread getTest2Thread(Map<String, String> _params){
		return new Test2Thread(_params);
	}
	/**
	 * 线程内部类2，通过jdbctemplate
	 * */
	class Test2Thread extends Thread {
		private Map<String, String> params;
		public Test2Thread(Map<String, String> _params){
			params = _params;
		}
		public void run(){
			//直接调用父类方法
			queryUserbyStation(params);
		}
	}
	/**
	 * 线程内部类1，通过conn
	 * */
	class Test1Thread extends Thread {
		private Connection c;
		private String params;
		public Test1Thread(Connection _c, String _params){
			c = _c;
			params = _params;
		}
		public void run(){
			//直接调用父类方法
			queryUserbyStationByConn(c, params);
		}
	}
	public static void print(Object o){
		System.out.println(o);
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
//		NamedParameterJdbcTemplate npjt1 = (NamedParameterJdbcTemplate)ctx.getBean("namedParameterJdbcTemplateTT1");
//		TimestenTest1 tt1 = new TimestenTest1(npjt1);
		TimestenTest1 tt1 = (TimestenTest1)ctx.getBean("TimestenTest1");
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("call_id", lac_ci);
		//串行
//		tt1.queryUserbyStation(paramMap);
//		tt1.queryUserbyStation(paramMap);
//		tt1.queryUserbyStation(paramMap);
		//并行
//		TimestenTest1 tt2 = (TimestenTest1)ctx.getBean("TimestenTest2");
//		TimestenTest1 tt3 = (TimestenTest1)ctx.getBean("TimestenTest3");
		for(int i=0;i<3;i++){
			Test2Thread t1t = tt1.getTest2Thread(paramMap);
			t1t.start();
		}
		
//		List<Connection> cl = tt1.getConnList();
//		for(Connection c : cl){
//			System.out.println("now c is:"+c.hashCode()+" c.url:"+c);
////			//串行
////			tt1.queryUserbyStationByConn(c, lac_ci);
//			//并行
//			Test1Thread t1t = new TimestenTest1().getTest1Thread(c, lac_ci);
//			t1t.start();
//		}
		
//		//并发测试
//		ThreadConfig tc = ctx.getBean(ThreadConfig.class);
//		DemoThreadService dts = ctx.getBean(DemoThreadService.class);
//		dts.setTt1(tt1);
//		for (int i = 0; i < 3; i++) {
////			dts.executeAsyncTaskPlus(i);
////			dts.executeAsyncTask(i);
//			dts.executeAsyncQueryTask(paramMap);
//		}
	}
}
