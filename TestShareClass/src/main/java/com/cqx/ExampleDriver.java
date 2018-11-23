package com.cqx;

import org.apache.hadoop.util.ProgramDriver;

/**
 * 命令<br>
 * hadoop jar hadoop-mapreduce-examples-2.7.1.jar wordcount  /input /output<br>
 * hadoop-mapreduce-examples-2.7.1.jar的wordcount不需要写全路径和类型的试验<br>
 * 这个实际上是有主类的Main-Class: org.apache.hadoop.examples.ExampleDriver
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
