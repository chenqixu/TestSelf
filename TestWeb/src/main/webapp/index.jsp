<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>
<%@page import="com.cqx.netty.bean.DiscardBean"%>
<%@page import="com.cqx.netty.client.DiscardClient"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.util.concurrent.BlockingQueue"%>
<%@page import="java.util.concurrent.LinkedBlockingQueue"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h2>该页面每隔3秒刷新一次</h2>
	<p>
		现在的秒钟时间是：
		<%
		Date d = new Date();
		out.print("" + d.getSeconds());
		DiscardBean discardBean = new DiscardBean(3, 1234567l, DiscardBean.buildNullDataBean());
		BlockingQueue<String> resultQueue = new LinkedBlockingQueue<String>();
		new DiscardClient("192.168.230.128", 18888).query(discardBean, resultQueue);
		while(true) {
			String resultStr = null;
			if((resultStr=resultQueue.poll())!=null) {
				out.print("queryResult："+resultStr);
				break;
			}
	        try {
	            TimeUnit.MILLISECONDS.sleep(100);
	        } catch (InterruptedException e) {
	        }
		}
		response.setHeader("refresh", "3");
		%>
	</p>
</body>
</html>