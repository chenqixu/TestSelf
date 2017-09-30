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
		//���·���������ǲ����ڵģ����ļ���Ҳ���С�
		String dstOut = "hdfs://streamslab.localdomain:8020/yznewlandbase/move/";
		
		//���ü���
		Configuration hadoopConfig = new Configuration();
//		hadoopConfig.set("fs.hdfs.impl",
//				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		hadoopConfig.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		//�ȼ�������,FileSystem��Ҫ
		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
		String path1 = "/home/hadoop/jar/conf/";
		hadoopConfig.addResource(new Path(path1 + "getMovementTrackData_config.xml"));
		
		//����ʱ��
		hadoopConfig.set(GetMovementConstants.SOURCE_DATA_DATE, "20150622");
		//��������ļ���
		hadoopConfig.set(GetMovementConstants.OUTPUT_NAME, "moveout");
		//�������·��
		String fullOutputPathStr = hadoopConfig.get(GetMovementConstants.OUTPUT_PATH);
		hadoopConfig.set(GetMovementConstants.FULL_OUTPUT_PATH, fullOutputPathStr);
		
		//hdfs�ļ�ϵͳ
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
		
		//����map�����key����
		job.setMapOutputKeyClass(TempKey.class);
		//����map�����value����
		job.setMapOutputValueClass(Text.class);
		
		//����reduce�����key����
		job.setOutputKeyClass(Text.class);
		//����reduce�����value����
		job.setOutputValueClass(NullWritable.class);

		/**
		 * ��map�׶Σ�ʹ��job.setInputFormatClass(TextInputFormat)��Ϊ�����ʽ��
		 * ע�����Ӧ�÷����Զ���Map�ж�������<IntPair, IntWritable>��
		 * ����������һ��List<IntPair, IntWritable>��
		 * ��map�׶ε���󣬻��ȵ���job.setPartitionerClass�����List���з�����
		 * ÿ������ӳ�䵽һ��reducer��ÿ���������ֵ���job.setSortComparatorClass���õ�key�ȽϺ���������
		 * ���Կ������Ȿ�����һ�������������û��ͨ��job.setSortComparatorClass����key�ȽϺ����࣬
		 * ��ʹ��key��ʵ�ֵ�compareTo������
		 * 
		 * ��reduce�׶Σ�reducer���յ�����ӳ�䵽���reducer��map�����
		 * Ҳ�ǻ����job.setSortComparatorClass���õ�key�ȽϺ�������������ݶ�����
		 * Ȼ��ʼ����һ��key��Ӧ��value��������
		 * ��ʱ��Ҫ�õ����飬ʹ��jobjob.setGroupingComparatorClass���õķ��麯���ࡣ
		 * ֻҪ����Ƚ����Ƚϵ�����key��ͬ�����Ǿ�����ͬһ���飬���ǵ�value����һ��value��������
		 * �������������keyʹ������ͬһ���������key�ĵ�һ��key��
		 * �����ǽ���Reducer��reduce����
		 * */
		//���÷���
		job.setPartitionerClass(TempKeyPartitioner.class);
		//��������
		job.setSortComparatorClass(SortCompare.class);
		//���÷���
		job.setGroupingComparatorClass(GroupingCompare.class);
		
		//����Reducer������
		job.setReducerClass(GetMovementTrackDataReducer.class);
		
		//��������·���Ͷ�Ӧ��Mapper������
		String inputPathMC_cdr = hadoopConfig.get(GetMovementConstants.INPUT_PAHT_MC_CDR);
		GetMovementCommonUtils.addInputPath(job, fs, inputPathMC_cdr, MCcdrDataMapper.class);

		String inputPathLTE_S1MME = hadoopConfig.get(GetMovementConstants.INPUT_PAHT_LTE_S1MME);
		GetMovementCommonUtils.addInputPath(job, fs, inputPathLTE_S1MME, LTES1MMEMapper.class);
		
		System.out.println("OUTPUT_NAME:"+hadoopConfig.get(GetMovementConstants.OUTPUT_NAME));
		//�������
		MultipleOutputs.addNamedOutput(job, hadoopConfig.get(GetMovementConstants.OUTPUT_NAME),
				TextOutputFormat.class, Text.class, NullWritable.class);
		//�������·��
		FileOutputFormat.setOutputPath(job, p);

		//����reduce���Ϊ���ļ�
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		
		//ִ��job��ֱ�����
		job.waitForCompletion(true);
		System.out.println("Finished");
	}
}
