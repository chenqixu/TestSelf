<%@ page import="java.net.URI"%>
<%@ page import="org.apache.hadoop.conf.Configuration"%>
<%@ page import="org.apache.hadoop.fs.FileSystem"%>
<%@ page import="org.apache.hadoop.fs.Path"%>
<%@ page import="org.apache.oozie.service.HadoopAccessorService"%>
<%@ page import="org.apache.oozie.service.Services"%>
<%@page contentType="text/html; charset=UTF-8"%>
<HTML>
<HEAD>
<TITLE>Oozie Test Info</TITLE>
</HEAD>
<BODY>
<%
try {
	String appPath = "/user/edc_base/udap/xml/101169491131@2018010707000002";
	String user = "edc_base";
	HadoopAccessorService has = Services.get().get(HadoopAccessorService.class);
    URI uri = new Path(appPath).toUri();
    System.out.println("##uri.getAuthority##"+uri.getAuthority());
    Configuration fsConf = has.createJobConf(uri.getAuthority());
    System.out.println("##fsConf##"+fsConf);
    FileSystem fs = has.createFileSystem(user, uri, fsConf);
    System.out.println("##fs##"+fs);
    System.out.println("##fs.defaultFS##"+fsConf.get("fs.defaultFS"));
} catch (Exception e) {
	e.printStackTrace();
}
%>
</BODY>
</HTML>