package com.newland.bi;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 简单的MR汇总程序
 * <br>文件内容规则：id,name,code,cnt,user_time
 * <br>SQL:select id,name,code,sum(cnt),sum(user_time) from table group by id,name,code;
 * */
public class main extends Configured implements Tool{
	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new main(), args));
	}

	@Override
	public int run(String[] args) throws Exception {
		// 文件输入路径
		String InputPath = "";
		// 文件名过滤规则
		String Inputfilter = "";
		// 输出文件名
		String Output_name = "";
		// 输出文件路径
		String Output_path = "";
		if(args!=null && args.length==4){
			InputPath = args[0];
			Inputfilter = args[1];
			Output_name = args[2];
			Output_path = args[3];
			System.out.println("文件输入路径："+InputPath);
			System.out.println("文件名过滤："+Inputfilter);
			System.out.println("输出文件名："+Output_name);
			System.out.println("输出文件路径："+Output_path);
		}else{
			System.out.println("参数不对，退出。");
			System.exit(-1);
		}
		
		FileSystem fileSystem = null;
		Configuration conf = new Configuration();
		// 加载core_site文件
		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		// 加载hdfs_site文件
		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		// 加载mr_site文件
		conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));
		// 加载yarn文件
		conf.addResource(new Path("/etc/hadoop/conf/yarn-site.xml"));
		// 通过配置实例获取HDFS文件系统
		fileSystem = FileSystem.newInstance(conf);

		// 设置参数
		conf.set(Contants.OUTPUTNAME, Output_name);
		conf.set(Contants.OUTPUTPATH, Output_path);
		// 优先级
		conf.set("mapreduce.job.priority",  "HIGH");

		// 设置job名称
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "nl-test-mr");
		// 设置job运行类
		job.setJarByClass(main.class);
		// 设置mapper输出格式
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		// 设置reduce类
		job.setReducerClass(reduce.class);
		// 设置reduce的输出key类型
		job.setOutputKeyClass(Text.class);
		// 设置reduce的输出value类型
		job.setOutputValueClass(Text.class);
		// 设置reducer个数
		job.setNumReduceTasks(1);	

		// 设置输入路径以及对应mapper
		Utils.addInputPath(job, fileSystem, InputPath, Inputfilter,
				map.class);

		// 设置输出文件名及输出格式
		MultipleOutputs.addNamedOutput(job, Output_name,
				TextOutputFormat.class, Text.class, NullWritable.class);

		// 设置输出路径
		FileOutputFormat.setOutputPath(job, new Path(Output_path));
		// 设置输出路径的全路径
		Path outputPath = new Path(Output_path);
		// 判断输出路径是否存在，如果存在则删除
		if (fileSystem.exists(outputPath)) {
			fileSystem.delete(outputPath, true);
			System.out.println("输出路径已经存在，删除....." + outputPath);
		}

		// 避免reduce输出为空文件
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

		// 等待job执行直到成功
		boolean returnStatus = job.waitForCompletion(true);
		System.out.println("exec job result:"+returnStatus);
		return 0;
	}

}
