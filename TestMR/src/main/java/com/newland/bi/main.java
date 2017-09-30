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
 * �򵥵�MR���ܳ���
 * <br>�ļ����ݹ���id,name,code,cnt,user_time
 * <br>SQL:select id,name,code,sum(cnt),sum(user_time) from table group by id,name,code;
 * */
public class main extends Configured implements Tool{
	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new main(), args));
	}

	@Override
	public int run(String[] args) throws Exception {
		// �ļ�����·��
		String InputPath = "";
		// �ļ������˹���
		String Inputfilter = "";
		// ����ļ���
		String Output_name = "";
		// ����ļ�·��
		String Output_path = "";
		if(args!=null && args.length==4){
			InputPath = args[0];
			Inputfilter = args[1];
			Output_name = args[2];
			Output_path = args[3];
			System.out.println("�ļ�����·����"+InputPath);
			System.out.println("�ļ������ˣ�"+Inputfilter);
			System.out.println("����ļ�����"+Output_name);
			System.out.println("����ļ�·����"+Output_path);
		}else{
			System.out.println("�������ԣ��˳���");
			System.exit(-1);
		}
		
		FileSystem fileSystem = null;
		Configuration conf = new Configuration();
		// ����core_site�ļ�
		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		// ����hdfs_site�ļ�
		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		// ����mr_site�ļ�
		conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));
		// ����yarn�ļ�
		conf.addResource(new Path("/etc/hadoop/conf/yarn-site.xml"));
		// ͨ������ʵ����ȡHDFS�ļ�ϵͳ
		fileSystem = FileSystem.newInstance(conf);

		// ���ò���
		conf.set(Contants.OUTPUTNAME, Output_name);
		conf.set(Contants.OUTPUTPATH, Output_path);
		// ���ȼ�
		conf.set("mapreduce.job.priority",  "HIGH");

		// ����job����
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "nl-test-mr");
		// ����job������
		job.setJarByClass(main.class);
		// ����mapper�����ʽ
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		// ����reduce��
		job.setReducerClass(reduce.class);
		// ����reduce�����key����
		job.setOutputKeyClass(Text.class);
		// ����reduce�����value����
		job.setOutputValueClass(Text.class);
		// ����reducer����
		job.setNumReduceTasks(1);	

		// ��������·���Լ���Ӧmapper
		Utils.addInputPath(job, fileSystem, InputPath, Inputfilter,
				map.class);

		// ��������ļ����������ʽ
		MultipleOutputs.addNamedOutput(job, Output_name,
				TextOutputFormat.class, Text.class, NullWritable.class);

		// �������·��
		FileOutputFormat.setOutputPath(job, new Path(Output_path));
		// �������·����ȫ·��
		Path outputPath = new Path(Output_path);
		// �ж����·���Ƿ���ڣ����������ɾ��
		if (fileSystem.exists(outputPath)) {
			fileSystem.delete(outputPath, true);
			System.out.println("���·���Ѿ����ڣ�ɾ��....." + outputPath);
		}

		// ����reduce���Ϊ���ļ�
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

		// �ȴ�jobִ��ֱ���ɹ�
		boolean returnStatus = job.waitForCompletion(true);
		System.out.println("exec job result:"+returnStatus);
		return 0;
	}

}
