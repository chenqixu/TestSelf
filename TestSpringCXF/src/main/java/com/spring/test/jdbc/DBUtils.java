package com.spring.test.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.spring.test.bean.Common;
import com.spring.test.bean.UserStatus;

@Component
public class DBUtils {
	@Resource
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private Common configInfo;
	@Resource(name="namedParameterJdbcTemplateTT1")
	private NamedParameterJdbcTemplate namedParameterJdbcTemplateTT1;
//	@Resource(name="jdbcTemplateTT1")
//	private JdbcTemplate jdbcTemplateTT1;
	private List<NamedParameterJdbcTemplate> npjtlist = null;
	
	public DBUtils(){
		npjtlist = new ArrayList<NamedParameterJdbcTemplate>();
//		npjtlist.add(namedParameterJdbcTemplateTT1);
	}
	
	public void init(){
		npjtlist.add(namedParameterJdbcTemplateTT1);
	}
	
	public Common getConfigInfo() {
		return configInfo;
	}

	public List<?> query(String sql, Map<String, ?> paramMap, Class<?> elementType) throws Exception{
		System.out.println("[sql]"+sql);
		System.out.println("[paramMap]"+paramMap);
		System.out.println("[namedParameterJdbcTemplateTT1]"+namedParameterJdbcTemplateTT1.getJdbcOperations());
//		System.out.println("[jdbcTemplateTT1]"+jdbcTemplateTT1.getDataSource().getConnection());
//		return jdbcTemplateTT1.queryForList(sql, elementType);
		return namedParameterJdbcTemplateTT1.queryForList(sql, paramMap, elementType);
	}
	
	public List<?> query(String sql, Map<String, ?> paramMap) throws Exception{
		System.out.println("[sql]"+sql);
		System.out.println("[paramMap]"+paramMap);
		System.out.println("[namedParameterJdbcTemplateTT1]"+namedParameterJdbcTemplateTT1.getJdbcOperations());
//		System.out.println("[jdbcTemplateTT1]"+jdbcTemplateTT1.getDataSource().getConnection());
		System.out.println(namedParameterJdbcTemplateTT1.getJdbcOperations().getClass());
		System.out.println(((JdbcTemplate) namedParameterJdbcTemplateTT1.getJdbcOperations()).getDataSource().getConnection());
//		return jdbcTemplateTT1.query(sql, new UserStatus());
		return namedParameterJdbcTemplateTT1.query(sql, paramMap, new UserStatus());
	}
	
	/**
	 * 测试Spring-JDBC
	 * @throws Exception 
	 * */
	public static void testJDBC(DBUtils db) throws Exception{
		//设置参数
//		String sql = "select file_flag as msisdn,flag_dependence as lacci,last_checked_time as uptime "
//				+"from utap.auto_trigger_pr_conf where task_template_id=:id";
		String sql = "select msisdn,fn_11 as lacci,fs_11 as uptime from realtime_status_snapshot T1 where T1.fn_11 in (:lac_ci)";
		Map<String, String> paramMap = new HashMap<String, String>();
//		paramMap.put("id", "1");
		paramMap.put("lac_ci", "23028_55826050");
		Map<String, Object> paramlist = new HashMap<String, Object>();
		paramlist.put("lac_ci", Arrays.asList(paramMap.get("lac_ci").split(",")));
		System.out.println("[Arrays]"+Arrays.asList(paramMap.get("lac_ci").split(",")));
		//查询
		List<?> resultlist = db.query(sql, paramlist);
		//打印结果
		System.out.println(resultlist);
	}
	
	/**
	 * 测试Spring-Properties注入bean
	 * */
	public static void testProperties(DBUtils db){
		System.out.println(db.getConfigInfo().getTtuid());
	}
	
	public static void main(String[] args) throws Exception {
		//加载配置文件
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:resources/spring/spring-base.xml");
		//使用名称初始化类
		DBUtils db = (DBUtils) ctx.getBean("DBUtils");
//		System.out.println(db.namedParameterJdbcTemplateTT1);
		//Spring注入初始化迟于构造函数
		db.init();
		System.out.println(db.npjtlist);
//		testJDBC(db);
//		testProperties(db);
	}
}
