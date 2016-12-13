package com.cqx;

import org.apache.hadoop.util.ProgramDriver;

/**
 * ����<br>
 * hadoop jar hadoop-mapreduce-examples-2.7.1.jar wordcount  /input /output<br>
 * hadoop-mapreduce-examples-2.7.1.jar��wordcount����Ҫдȫ·�������͵�����<br>
 * ���ʵ�������������Main-Class: org.apache.hadoop.examples.ExampleDriver
 * */
public class ExampleDriver {
	public static void main(String[] args) {
		int exitCode = -1;
		ProgramDriver pgd = new ProgramDriver();
		try {
			pgd.addClass("helloworld", HelloWorld.class, "");
			exitCode = pgd.run(args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(exitCode);
	}
}
