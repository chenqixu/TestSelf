package com.newland.bi.bigdata.spark;

import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * 转化和行动
 * 惰性操作
 * */
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
	
	/**
	 * 使用数组初始化RDD
	 * */
	public JavaRDD<String> listinit(String[] strs){
		JavaRDD<String> lines = sc.parallelize(Arrays.asList(strs));
		return lines;
	}
	
	/**
	 * 使用文件初始化RDD
	 * */
	public JavaRDD<String> fileinit(String path){
		JavaRDD<String> input = sc.textFile(path);
		return input;
	}
	
	/**
	 * 切分并计数，最后输出
	 * */
	public void deal(JavaRDD<String> input, String outputFile){
		// 切分为单词
		JavaRDD<String> words = input.flatMap(
				new FlatMapFunction<String, String>(){
					public Iterable<String> call(String x){
						return Arrays.asList(x.split(" "));
					}
				});
		// 转换为键值对并计数
		JavaPairRDD<String, Integer> counts = words.mapToPair(
				new PairFunction<String, String, Integer>(){
					public Tuple2<String, Integer> call(String x){
						return new Tuple2(x, 1);
					}
				}).reduceByKey(new Function2<Integer, Integer, Integer>(){
					public Integer call(Integer x, Integer y){
						return x + y;
					}
				});
		// 将统计出来的单词总数存入一个文本文件，引发求值
		counts.saveAsTextFile(outputFile);
	}
	
	/**
	 * 过滤内容
	 * */
	public JavaRDD<String> filterRDD(JavaRDD<String> input){
		JavaRDD<String> errorsRDD = input.filter(
				new Function<String, Boolean>(){
					public Boolean call(String x){ return x.contains("error"); }
				});
		// or
		// JavaRDD<String> errorsRDD = input.filter(new Contains("error"));
		return errorsRDD;
	}
	
	/**
	 * 组合RDD
	 * */
	public JavaRDD<String> unionRDD(JavaRDD<String> input1, JavaRDD<String> input2){
		return input1.union(input2);
	}
	
	/**
	 * 打印指定行数内容
	 * */
	public void printContentByNum(JavaRDD<String> input, int n){
		System.out.println("count:"+input.count());
		System.out.println("here are "+n+" examples:");
		for(String line:input.take(n)){
			System.out.println(line);
		}
	}
	
	public static void main(String[] args) {
		SparkTest st = new SparkTest();
	}
}
