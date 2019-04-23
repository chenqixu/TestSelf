package com.cqx.test;

import org.apache.spark.SparkConf;  
import org.apache.spark.api.java.JavaRDD;  
import org.apache.spark.api.java.JavaSparkContext; 
import org.apache.spark.sql.SparkSession;

public class Test1 {
	
	public String appname = "cqxTest";
	public String scmaster = "spark://master75:7077";
	public String hdfstextfile = "hdfs://10.1.8.75:8020/home/1.txt";
	
	public void spark1(){
		SparkSession spark = SparkSession.builder()
		      .master(scmaster)
		      .appName(appname)
		      .config("spark.some.config.option", "some-value")
		      .getOrCreate();
		JavaRDD<String> lines = spark.read().textFile(hdfstextfile).javaRDD();
		System.out.println("[lines.count]"+lines.count());
		spark.stop();
	}
	
	public void spark2(){
//		SparkConf conf = new SparkConf().setMaster("local").setAppName("HelloSpark");
//		org.apache.spark.deploy.SparkSubmit a;
		JavaSparkContext sc = null;
		SparkConf conf = new SparkConf().setMaster(scmaster).setAppName(appname);
		try {  
			sc = new JavaSparkContext(conf);
			JavaRDD<String> lines =sc.textFile(hdfstextfile);
			System.out.println("[lines.count]"+lines.count());
			sc.close();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(sc!=null)sc.close();
        }
	}

	public void local() {
		JavaSparkContext sc = null;
		SparkConf conf = new SparkConf().setMaster("local").setAppName(appname);
		try {
			sc = new JavaSparkContext(conf);
			JavaRDD<String> lines =sc.textFile("d:\\tmp\\data\\dpi\\a.txt");
			System.out.println("[lines.count]"+lines.count());
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(sc!=null)sc.close();
		}
	}
	
	public static void main(String[] args) {
		Test1 t = new Test1();
		t.local();
//		t.spark2();
		// bulid
		// ftp
		// ssh exec
	}
}
