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

public class TempMain {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		//����·��
//		String dst = "hdfs://streamslab.localdomain:8020/test/input.txt";
		String dst = "hdfs://streamslab.localdomain:9000/test/input.txt";
		//���·���������ǲ����ڵģ����ļ���Ҳ���С�
//		String dstOut = "hdfs://streamslab.localdomain:8020/test/output";
		String dstOut = "hdfs://streamslab.localdomain:9000/test/output";		
		Configuration hadoopConfig = new Configuration();
		//2.0.0����û��
//		hadoopConfig.set("fs.hdfs.impl",
//				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
//		hadoopConfig.set("fs.file.impl",
//				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		//�ȼ�������,FileSystem��Ҫ
		String path = "/opt/hadoop/hadoop-2.4.0/etc/hadoop/";
//		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));		
		FileSystem fs = null;
		try {
			fs = FileSystem.get(hadoopConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//������·�����ڣ���ɾ��
		Path p = new Path(dstOut);
		if (fs.exists(p)) {
			fs.delete(p, true);
			System.out.println("=====delPath ɾ����" + p.toString() + "=====");
		}
		//����
		Job job = new Job(hadoopConfig);
		//�����Ҫ���jar���У���Ҫ�������
		job.setJarByClass(TempMain.class);
		//jobִ����ҵʱ���������ļ���·��
		FileInputFormat.addInputPath(job, new Path(dst));
		FileOutputFormat.setOutputPath(job, new Path(dstOut));
		//ָ���Զ����Mapper��Reducer��Ϊ�����׶ε���������
		job.setMapperClass(TempMapper.class);
		job.setReducerClass(TempReducer.class);
		//���������������Key��Value������
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		//ִ��job��ֱ�����
		job.waitForCompletion(true);
		System.out.println("TrackingURL:"+job.getTrackingURL());
		System.out.println("Finished");
	}
}
