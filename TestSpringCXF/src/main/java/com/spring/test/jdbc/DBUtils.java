package com.spring.test.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.spring.test.bean.UserStatus;

@Component
public class DBUtils {
	@Resource
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public List<?> query(String sql, Map<String, ?> paramMap, Class<?> elementType){
		System.out.println("[sql]"+sql);
		System.out.println("[paramMap]"+paramMap);
		return namedParameterJdbcTemplate.queryForList(sql, paramMap, elementType);
	}
	
	public List<?> query(String sql, Map<String, ?> paramMap){
		System.out.println("[sql]"+sql);
		System.out.println("[paramMap]"+paramMap);
		return namedParameterJdbcTemplate.query(sql, paramMap, new UserStatus());
	}
	
	public static void main(String[] args) {
		//加载配置文件
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:resources/spring/spring-base.xml");
		//使用名称初始化类
		DBUtils db = (DBUtils) ctx.getBean("DBUtils");
		//设置参数
		String sql = "select file_flag as msisdn,flag_dependence as lacci,last_checked_time as uptime "
				+"from utap.auto_trigger_pr_conf where task_template_id=:id";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("id", "1");
		//查询
		List<?> resultlist = db.query(sql, paramMap);
		//打印结果
		System.out.println(resultlist);
	}
}
