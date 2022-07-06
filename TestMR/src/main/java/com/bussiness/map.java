package com.bussiness;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class map extends Mapper<LongWritable, Text, Text, Text> {
	// 配置信息实体类
	private static Configuration conf = null;
	// 分布式文件系统
	private FileSystem fs = null;
	// 逐行读入的数据
	private static String lineValue;

	/**
	 * 在任务开始时调用一次
	 * */
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// 如果配置信息实体类为空，则获取对象
		if (conf == null) conf = context.getConfiguration();
		// 获取文件系统实例
		fs = FileSystem.newInstance(conf);
	}
	
	/**
	 * 在任务结束时调用一次
	 * */
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// 关闭分布式文件系统实例	
		fs.close();
	}
	
	/**
	 * 对输入文件逐行处理
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	@Override
	public void map(LongWritable key, Text value, Context context) {
		// 逐行读入数据
		lineValue = value.toString();
		// 分割数据
		String arr[] = lineValue.split(Contants.SPLIT_DICT, -1);
		// id,name,code
		String keystr = arr[0]+Contants.SPLIT_DICT+arr[1]+Contants.SPLIT_DICT+arr[2];// key
		// cnt,user_time
		String valuestr = arr[3]+Contants.SPLIT_DICT+arr[4];// value
		if (keystr != null && keystr.trim().length()>0  && valuestr != null && valuestr.trim().length()>0) {
			// 以字段进行分组，在reduce中进行汇总
			try {
				context.write(new Text(keystr), new Text(valuestr));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			return;
		}
	}
}
