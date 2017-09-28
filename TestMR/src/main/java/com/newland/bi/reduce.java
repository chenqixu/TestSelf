package com.newland.bi;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class reduce extends Reducer<Text,Text,Text,Text>{
	// ����ʵ��
	private Configuration conf = null;
	// �ֲ�ʽ�ļ�ϵͳ
	private FileSystem fs = null;
	// ���� MultipleOutputs �ı���
	private MultipleOutputs<Text,NullWritable> multipleOutputs;
	// ����ļ���
	private String outputName;
	private String outputPath;
    
	/**
	 * ������ʼʱ����һ��
	 * */
	protected void setup(Context context) throws IOException,InterruptedException{
		super.setup(context);
		// ��ȡConfiguration����
    	if(null == conf) conf = context.getConfiguration();
		// ��ȡ�ļ�ϵͳʵ��
		fs = FileSystem.newInstance(conf);
		// ���ļ����
		multipleOutputs = new MultipleOutputs(context);
		// reduce���·�����ļ���
    	outputName = conf.get(Contants.OUTPUTNAME);
    	outputPath =  conf.get(Contants.OUTPUTPATH)+outputName;		
	}
	
	/**
	 * ���������ʱ����һ��
	 * */
	protected void cleanup(Context context) throws IOException,InterruptedException {
		// �رն��ļ����
		multipleOutputs.close();
		// �رշֲ�ʽ�ļ�ϵͳʵ��
		fs.close();
		// ���ø����cleanup����
		super.cleanup(context);
	}
	
	/**
	 * ����map�����ÿ������id,name,code���л��ܴ���sum(cnt),sum(user_time)��
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) {
		// map������ķ����ֶΣ�id,name,code��
		String keyStr = key.toString();
		// ���л��ܲ���
		int all_cnt = 0;// ����ֵ
		int all_user_time = 0;// ����ֵ
		// ѭ��map�������value��ʹ�ö��ļ����ʵ��������ļ�
		for(Text value : values){
			// �и�map�������ֵ�ֶΣ����л���
			String arr[] = value.toString().split(Contants.SPLIT_DICT, -1);
			String cnt = arr[0];
			String user_time = arr[1];
			all_cnt += Integer.valueOf(cnt);// ͬһ������л���
			all_user_time += Integer.valueOf(user_time);// ͬһ������л���
		}
		// �����key�ͻ��ܺ��value
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
