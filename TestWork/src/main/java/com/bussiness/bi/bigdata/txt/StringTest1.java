package com.bussiness.bi.bigdata.txt;

import java.net.URLDecoder;

public class StringTest1 {
	public static void main(String[] args) {
		String dependence_time = "'$'";
		System.out.println(dependence_time.length());
		dependence_time = "'$-1'";
		System.out.println(dependence_time.length());

		System.out.println(URLDecoder.decode("%E8%8E%86%E7%94%B0%E7%88%B1%E5%AE%B6%E5%85%89%E7%BD%91"));
		System.out.println(URLDecoder.decode("莆田爱家光网"));
	}
}
