package com.main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.mr.TempMapper;
import com.mr.TempReducer;

public class TempMain3 {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		//输入路径
		String dst = "hdfs://bch:8020/test/input.txt";
		//输出路径，必须是不存在的，空文件加也不行。
		String dstOut = "hdfs://bch:8020/test/output";		
		Configuration hadoopConfig = new Configuration();
		//2.0.0这里没有
//		hadoopConfig.set("fs.hdfs.impl",
//				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
//		hadoopConfig.set("fs.file.impl",
//				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		//先加载配置,FileSystem需要
//		String path = "/opt/hadoop/hadoop-2.4.0/etc/hadoop/";
		String path = "/bi/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
		System.out.println("[fs.defaultFS]"+hadoopConfig.get("fs.defaultFS"));
		FileSystem fs = null;
		try {
			fs = FileSystem.get(hadoopConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[fs]"+fs);
		//如果输出路径存在，先删除
		Path p = new Path(dstOut);
		System.out.println("[dstOut]"+dstOut+"[path]"+p);
		if (fs.exists(p)) {
			fs.delete(p, true);
			System.out.println("=====delPath 删除：" + p.toString() + "=====");
		}
		//任务
		Job job = new Job(hadoopConfig);
		//如果需要打成jar运行，需要下面这句
		job.setJarByClass(TempMain.class);
		//job执行作业时输入和输出文件的路径
		FileInputFormat.addInputPath(job, new Path(dst));
		FileOutputFormat.setOutputPath(job, new Path(dstOut));
		//指定自定义的Mapper和Reducer作为两个阶段的任务处理类
		job.setMapperClass(TempMapper.class);
		job.setReducerClass(TempReducer.class);
		//设置最后输出结果的Key和Value的类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		//执行job，直到完成
		job.waitForCompletion(true);
		System.out.println("TrackingURL:"+job.getTrackingURL());
		System.out.println("Finished");
	}
}
