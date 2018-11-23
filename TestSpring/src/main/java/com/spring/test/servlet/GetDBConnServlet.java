package com.spring.test.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring.test.util.DBUtilFactory;

public class GetDBConnServlet extends SpringSupportServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GetDBConnServlet.class);
	DBUtilFactory dbf;

	public GetDBConnServlet(){
		super();		
	}
	
	/**
	 * @param response x 
	 * @param request x 
	 * @throws IOException x 
	 *  */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
	
	/**
	 * 业务
	 * @param response x 
	 * @param request x 
	 * @throws IOException x 
	 * */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("GetDBConnServlet doPost begin...");
		// 并发测试
		DBUtilFactory.connListDeal();
//		// 获取连接
//		Connection conn = DBUtilFactory.getConnetion();
//		// 释放连接
//		DBUtilFactory.releaseConn(conn);
		LOG.info("GetDBConnServlet doPost end...");
	}
}
