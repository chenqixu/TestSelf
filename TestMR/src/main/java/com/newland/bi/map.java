package com.newland.bi;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class map extends Mapper<LongWritable, Text, Text, Text> {
	// ������Ϣʵ����
	private static Configuration conf = null;
	// �ֲ�ʽ�ļ�ϵͳ
	private FileSystem fs = null;
	// ���ж��������
	private static String lineValue;

	/**
	 * ������ʼʱ����һ��
	 * */
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// ���������Ϣʵ����Ϊ�գ����ȡ����
		if (conf == null) conf = context.getConfiguration();
		// ��ȡ�ļ�ϵͳʵ��
		fs = FileSystem.newInstance(conf);
	}
	
	/**
	 * ���������ʱ����һ��
	 * */
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// �رշֲ�ʽ�ļ�ϵͳʵ��	
		fs.close();
	}
	
	/**
	 * �������ļ����д���
	 * @throws InterruptedException 
	 * @throws IOException 
	 * */
	@Override
	public void map(LongWritable key, Text value, Context context) {
		// ���ж�������
		lineValue = value.toString();
		// �ָ�����
		String arr[] = lineValue.split(Contants.SPLIT_DICT, -1);
		// id,name,code
		String keystr = arr[0]+Contants.SPLIT_DICT+arr[1]+Contants.SPLIT_DICT+arr[2];// key
		// cnt,user_time
		String valuestr = arr[3]+Contants.SPLIT_DICT+arr[4];// value
		if (keystr != null && keystr.trim().length()>0  && valuestr != null && valuestr.trim().length()>0) {
			// ���ֶν��з��飬��reduce�н��л���
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
