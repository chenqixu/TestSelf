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

public class HiveTest {
	private NamedParameterJdbcTemplate npjt;
	public HiveTest(){}
	public HiveTest(NamedParameterJdbcTemplate _npjt){
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
	
	public static void main(String[] args) {
//		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:resources/spring/spring-base.xml");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:*spring-base.xml");
		HiveTest ht = new HiveTest((NamedParameterJdbcTemplate)ctx.getBean("namedParameterJdbcTemplateHive"));
		ht.getConn();
	}
}