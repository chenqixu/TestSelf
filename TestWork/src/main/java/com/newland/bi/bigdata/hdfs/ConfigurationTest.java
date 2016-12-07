package com.newland.bi.bigdata.hdfs;

import org.apache.hadoop.fs.Path;

public class ConfigurationTest {
	public static void main(String[] args) {
		Configuration cfg = new Configuration();
		cfg.addResource(new Path("h:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\conf\\test.xml"));
		System.out.println(cfg.get("fs.defaultFS"));
	}
}
