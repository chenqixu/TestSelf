package com.main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
//import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.mr.LTES1MMEMapper;
import com.mr.MCcdrDataMapper;
import com.mr.GetMovementTrackDataReducer;
import com.mr.comm.GetMovementCommonUtils;
import com.mr.comm.GetMovementConstants;
import com.mr.comm.TextOutputFormat;
import com.mr.util.GroupingCompare;
import com.mr.util.SortCompare;
import com.mr.util.TempKey;
import com.mr.util.TempKeyPartitioner;

public class TempMain2 {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		//输出路径，必须是不存在的，空文件加也不行。
		String dstOut = "hdfs://streamslab.localdomain:8020/yznewlandbase/move/";
		
		//配置加载
		Configuration hadoopConfig = new Configuration();
//		hadoopConfig.set("fs.hdfs.impl",
//				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		hadoopConfig.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		//先加载配置,FileSystem需要
		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
		String path1 = "/home/hadoop/jar/conf/";
		hadoopConfig.addResource(new Path(path1 + "getMovementTrackData_config.xml"));
		
		//设置时间
		hadoopConfig.set(GetMovementConstants.SOURCE_DATA_DATE, "20150622");
		//设置输出文件名
		hadoopConfig.set(GetMovementConstants.OUTPUT_NAME, "moveout");
		//配置输出路径
		String fullOutputPathStr = hadoopConfig.get(GetMovementConstants.OUTPUT_PATH);
		hadoopConfig.set(GetMovementConstants.FULL_OUTPUT_PATH, fullOutputPathStr);
		
		//hdfs文件系统
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
		job.setJarByClass(TempMain.class);
		
		//设置map的输出key类型
		job.setMapOutputKeyClass(TempKey.class);
		//设置map的输出value类型
		job.setMapOutputValueClass(Text.class);
		
		//设置reduce的输出key类型
		job.setOutputKeyClass(Text.class);
		//设置reduce的输出value类型
		job.setOutputValueClass(NullWritable.class);

		/**
		 * 在map阶段，使用job.setInputFormatClass(TextInputFormat)做为输入格式。
		 * 注意输出应该符合自定义Map中定义的输出<IntPair, IntWritable>。
		 * 最终是生成一个List<IntPair, IntWritable>。
		 * 在map阶段的最后，会先调用job.setPartitionerClass对这个List进行分区，
		 * 每个分区映射到一个reducer。每个分区内又调用job.setSortComparatorClass设置的key比较函数类排序。
		 * 可以看到，这本身就是一个二次排序。如果没有通过job.setSortComparatorClass设置key比较函数类，
		 * 则使用key的实现的compareTo方法。
		 * 
		 * 在reduce阶段，reducer接收到所有映射到这个reducer的map输出后，
		 * 也是会调用job.setSortComparatorClass设置的key比较函数类对所有数据对排序。
		 * 然后开始构造一个key对应的value迭代器。
		 * 这时就要用到分组，使用jobjob.setGroupingComparatorClass设置的分组函数类。
		 * 只要这个比较器比较的两个key相同，他们就属于同一个组，它们的value放在一个value迭代器，
		 * 而这个迭代器的key使用属于同一个组的所有key的第一个key。
		 * 最后就是进入Reducer的reduce方法
		 * */
		//设置分区
		job.setPartitionerClass(TempKeyPartitioner.class);
		//设置排序
		job.setSortComparatorClass(SortCompare.class);
		//设置分组
		job.setGroupingComparatorClass(GroupingCompare.class);
		
		//设置Reducer处理类
		job.setReducerClass(GetMovementTrackDataReducer.class);
		
		//设置输入路径和对应的Mapper处理类
		String inputPathMC_cdr = hadoopConfig.get(GetMovementConstants.INPUT_PAHT_MC_CDR);
		GetMovementCommonUtils.addInputPath(job, fs, inputPathMC_cdr, MCcdrDataMapper.class);

		String inputPathLTE_S1MME = hadoopConfig.get(GetMovementConstants.INPUT_PAHT_LTE_S1MME);
		GetMovementCommonUtils.addInputPath(job, fs, inputPathLTE_S1MME, LTES1MMEMapper.class);
		
		System.out.println("OUTPUT_NAME:"+hadoopConfig.get(GetMovementConstants.OUTPUT_NAME));
		//设置输出
		MultipleOutputs.addNamedOutput(job, hadoopConfig.get(GetMovementConstants.OUTPUT_NAME),
				TextOutputFormat.class, Text.class, NullWritable.class);
		//设置输出路径
		FileOutputFormat.setOutputPath(job, p);

		//避免reduce输出为空文件
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		
		//执行job，直到完成
		job.waitForCompletion(true);
		System.out.println("Finished");
	}
}
