<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.spring.test.bean.GreetingClient"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

GreetingClient gc = new GreetingClient();
gc.client();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    This is my JSP page. <br>
    <form method="post" action="<%=path%>/mvc/doCBAuth.do">
    	<input id="reqBean" name="reqBean" value="test..."/>
    	<button onclick="submit()">submit</button>
    </form>
    <button id="btn" name="btn">ajax_click</button>
    <script src="<%=path%>/js/jquery.min.js"></script>
    <script type="text/javascript" >
    	/* init */
    	$(function(){
			$("#btn").on('click',function(){
				$.post("mvc/doCBAuth.do",{reqBean:$("#reqBean").val()},function(data){
					alert(data);
				});
			});
		});
    </script>
  </body>
</html>
