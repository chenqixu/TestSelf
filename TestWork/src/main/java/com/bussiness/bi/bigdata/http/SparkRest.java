package com.bussiness.bi.bigdata.http;

import com.bussiness.bi.bigdata.http.frame.CallWebService;

/**
 * jar包必须在远程主机
 * 必须得在执行主机创建/tmp/spark-events目录
 * */
public class SparkRest {
	public static void main(String[] args) {
		String sMethod =  "post";
		String sUrl = "http://10.1.8.75:6066/v1/submissions/create";
		String aRequestContent = "{"
+"  \"action\" : \"CreateSubmissionRequest\","
+"  \"appArgs\" : [ \"\" ], "
+"  \"appResource\" : \"file:/home/edc_base/cqx/java/TestSpark-1.0.0.jar\", "
+"  \"clientSparkVersion\" : \"2.3.0\","
+"  \"environmentVariables\" : {"
+"    \"SPARK_ENV_LOADED\" : \"1\""
+"  },"
+"  \"mainClass\" : \"com.cqx.test.Test1\","
+"  \"sparkProperties\" : {"
+"    \"spark.jars\" : \"file:/home/edc_base/cqx/java/TestSpark-1.0.0.jar\","
+"    \"spark.driver.supervise\" : \"false\","
+"    \"spark.app.name\" : \"MyJob\","
+"    \"spark.eventLog.enabled\": \"true\","
+"    \"spark.submit.deployMode\" : \"cluster\","
+"    \"spark.master\" : \"spark://10.1.8.75:6066\""
+"  }"
+"}";
		String result = CallWebService.getInstance().doAction(sMethod, sUrl, aRequestContent.getBytes());
		System.out.println(result);
	}
}
