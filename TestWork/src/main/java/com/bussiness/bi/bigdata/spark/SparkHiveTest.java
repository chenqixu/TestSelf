package com.bussiness.bi.bigdata.spark;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

/**
 * spark-hive测试
 * */
public class SparkHiveTest {
	private SparkContext sc = null;
	
	/**
	 * hive-select
	 * */
	public void hiveSelect(String sql){
		HiveContext hiveCtx = new HiveContext(sc);
//		DataFrame rows = hiveCtx.sql(sql);
//		Row firstRow = rows.first();
//		System.out.println(firstRow.getString(0));
	}
}
