package com.newland.bi;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class reduce extends Reducer<Text,Text,Text,Text>{
	// 配置实例
	private Configuration conf = null;
	// 分布式文件系统
	private FileSystem fs = null;
	// 声明 MultipleOutputs 的变量
	private MultipleOutputs<Text,NullWritable> multipleOutputs;
	// 输出文件名
	private String outputName;
	private String outputPath;
    
	/**
	 * 在任务开始时调用一次
	 * */
	protected void setup(Context context) throws IOException,InterruptedException{
		super.setup(context);
		// 获取Configuration对象
    	if(null == conf) conf = context.getConfiguration();
		// 获取文件系统实例
		fs = FileSystem.newInstance(conf);
		// 多文件输出
		multipleOutputs = new MultipleOutputs(context);
		// reduce输出路径和文件名
    	outputName = conf.get(Contants.OUTPUTNAME);
    	outputPath =  conf.get(Contants.OUTPUTPATH)+outputName;		
	}
	
	/**
	 * 在任务结束时调用一次
	 * */
	protected void cleanup(Context context) throws IOException,InterruptedException {
		// 关闭多文件输出
		multipleOutputs.close();
		// 关闭分布式文件系统实例
		fs.close();
		// 调用父类的cleanup方法
		super.cleanup(context);
	}
	
	/**
	 * 根据map输出的每个分组id,name,code进行汇总处理（sum(cnt),sum(user_time)）
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) {
		// map中输出的分组字段（id,name,code）
		String keyStr = key.toString();
		// 进行汇总操作
		int all_cnt = 0;// 汇总值
		int all_user_time = 0;// 汇总值
		// 循环map中输出的value，使用多文件输出实例输出到文件
		for(Text value : values){
			// 切割map中输出的值字段，进行汇总
			String arr[] = value.toString().split(Contants.SPLIT_DICT, -1);
			String cnt = arr[0];
			String user_time = arr[1];
			all_cnt += Integer.valueOf(cnt);// 同一分组进行汇总
			all_user_time += Integer.valueOf(user_time);// 同一分组进行汇总
		}
		// 输出，key和汇总后的value
		String outputStr = keyStr+Contants.SPLIT_DICT+all_cnt+Contants.SPLIT_DICT+all_user_time;
		try {
			multipleOutputs.write(outputName,new Text(outputStr),NullWritable.get(),outputPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
