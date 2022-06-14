package com.bussiness.bi.bigdata.spark;

import org.apache.spark.api.java.function.Function;

public class Contains implements Function<String, Boolean> {
	private String query;
	public Contains(String query) {
		this.query=query;
	}
	public Boolean call(String x) {
		return x.contains(query);
	}
}
