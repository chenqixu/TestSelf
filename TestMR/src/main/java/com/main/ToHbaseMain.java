package com.main;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.KeyValueSortReducer;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.SimpleTotalOrderPartitioner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.mr.HFileMapper;

public class ToHbaseMain {
	public static void main(String[] args) throws IOException,
	InterruptedException, ClassNotFoundException {
		//输入路径
		String dst = "hdfs://streamslab.localdomain:8020/";
		//输出路径，必须是不存在的，空文件加也不行。
		String dstOut = "hdfs://streamslab.localdomain:8020/";		
		Configuration hadoopConfig = new Configuration();
		//先加载配置,FileSystem需要
		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
		//加载程序自身的配置
		String program_path = "/home/hadoop/jar/conf/tohbase.xml";
		hadoopConfig.addResource(new Path(program_path));
		//获得输入文件和输出路径
		dst = dst + hadoopConfig.get("input", "test/input.txt");
		dstOut = dstOut + hadoopConfig.get("dstout", "test/output");
		//获得表名
		String table_name = hadoopConfig.get("tablename", "gn_cdr_4");
		System.out.println("=====input:"+dst+"=====");
		System.out.println("=====dstOut:"+dstOut+"=====");
		System.out.println("=====table_name:"+table_name+"=====");
		FileSystem fs = null;
		try {
			fs = FileSystem.get(hadoopConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//如果输出路径存在，先删除
		Path p = new Path(dstOut);
		if (fs.exists(p)) {
			fs.delete(p, true);
			System.out.println("=====delPath 删除：" + p.toString() + "=====");
		}
		//任务
		Job job = new Job(hadoopConfig);
		//如果需要打成jar运行，需要下面这句
		job.setJarByClass(ToHbaseMain.class);
        //设置map以及reduce的名字，reduce的使用固定的即可
        job.setMapperClass(HFileMapper.class);
        job.setReducerClass(KeyValueSortReducer.class);
        //设定map的输出的key以及value，这个由reduce决定的，因为使用自带的reduce，所以这个是固定的不变
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);
        //设置partition方法，这个如果是写hfile也直接默认这个就可以
//        job.setPartitionerClass(SimpleTotalOrderPartitioner.class);
        //设置输入路径以及输出路径
		FileInputFormat.addInputPath(job, new Path(dst));
		FileOutputFormat.setOutputPath(job, new Path(dstOut));
		// 不需要设置,系统会根据相关信息调用 HFileOutputFormat
		job.setOutputFormatClass(HFileOutputFormat.class);
		HTable table = new HTable(hadoopConfig, table_name);
		HFileOutputFormat.configureIncrementalLoad(job, table);
		
        StringBuilder compressionConfigValue = new StringBuilder();
        HTableDescriptor tableDescriptor = table.getTableDescriptor();
        if(tableDescriptor == null)
            return;
        Collection families = tableDescriptor.getFamilies();
        int i = 0;
        HColumnDescriptor familyDescriptor;
        for(Iterator i$ = families.iterator(); i$.hasNext(); compressionConfigValue.append(URLEncoder.encode(familyDescriptor.getCompression().getName(), "UTF-8")))
        {
            familyDescriptor = (HColumnDescriptor)i$.next();
            if(i++ > 0)
                compressionConfigValue.append('&');
            compressionConfigValue.append(URLEncoder.encode(familyDescriptor.getNameAsString(), "UTF-8"));
            compressionConfigValue.append('=');
        }
        System.out.println("|||||||||||||||||||||||||hbase.hfileoutputformat.families.compression="+compressionConfigValue.toString());
        hadoopConfig.set("hbase.hfileoutputformat.families.compression", compressionConfigValue.toString());
        		
		System.out.println("=====开始执行job=====");
		//执行job，直到完成
		if(job.waitForCompletion(true)){
			System.out.println("=====执行job成功，开始加载hbase=====");
        	try {
        		//job成功执行，然后把该hfile载入到hbase中
        		LoadIncrementalHFiles loader;
				loader = new LoadIncrementalHFiles(hadoopConfig);
	        	loader.doBulkLoad(new Path(dstOut), table);
			} catch (Exception e) {
				System.out.println("=====加载hbase异常："+e.toString()+"=====");
				e.printStackTrace();
			}
        	//加载完成删除输出文件
        	boolean deleteout = fs.delete(new Path(dstOut),true);
			System.out.println("=====加载完成删除输出文件结果："+deleteout+"=====");
		}else{
			System.out.println("=====执行job失败，退出。=====");
			System.exit(1);
		}
		System.out.println("=====任务完成，退出。=====");
		System.exit(0);
	}
}
