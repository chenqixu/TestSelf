package com.bussiness.bi.bigdata.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

public class TempMR1 {
	public static void main(String[] args) {

//        httpTaskLogLocation = ttStatus.getUrlScheme() + "://" + host + ":" +
//                              ttStatus.getHttpPort();
//           //+ "/tasklog?plaintext=true&attemptid=" + status.getTaskID();
		
		Configuration hadoopConfig = new Configuration();
		try {
			Job job = new Job(hadoopConfig);
			System.out.println("TrackingURL:"+job.getTrackingURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
		org.apache.hadoop.fs.BlockLocation test;
//		org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit t1;
	}
}
