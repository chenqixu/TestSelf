package com.newland.bi.bigdata.spark;

import org.apache.spark.api.java.JavaSparkContext;

public class SparkTest {
	private JavaSparkContext sc = null;
	public SparkTest(){
		init();
	}
	/**
	 * 初始化
	 * */
	public void init(){
		sc = new JavaSparkContext("spark://master75:7077",
				"SparkTest_cqx",
				"/usr/lib/spark",
				JavaSparkContext.jarOfClass(SparkTest.class));
		System.out.println(sc);
//		System.out.println("[MASTER]"+System.getenv("MASTER"));
//		System.out.println("[SPARK_HOME]"+System.getenv("SPARK_HOME"));
//		System.out.println("[JARS]"+System.getenv("JARS"));
	}
	
	public static void main(String[] args) {
		SparkTest st = new SparkTest();
	}
}
