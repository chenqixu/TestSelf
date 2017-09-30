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
		//����·��
		String dst = "hdfs://streamslab.localdomain:8020/";
		//���·���������ǲ����ڵģ����ļ���Ҳ���С�
		String dstOut = "hdfs://streamslab.localdomain:8020/";		
		Configuration hadoopConfig = new Configuration();
		//�ȼ�������,FileSystem��Ҫ
		String path = "/home/hadoop/app/hadoop-2.0.0-cdh4.3.0/etc/hadoop/";
		hadoopConfig.addResource(new Path(path + "core-site.xml"));
		hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
		hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
		//���س������������
		String program_path = "/home/hadoop/jar/conf/tohbase.xml";
		hadoopConfig.addResource(new Path(program_path));
		//��������ļ������·��
		dst = dst + hadoopConfig.get("input", "test/input.txt");
		dstOut = dstOut + hadoopConfig.get("dstout", "test/output");
		//��ñ���
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
		//������·�����ڣ���ɾ��
		Path p = new Path(dstOut);
		if (fs.exists(p)) {
			fs.delete(p, true);
			System.out.println("=====delPath ɾ����" + p.toString() + "=====");
		}
		//����
		Job job = new Job(hadoopConfig);
		//�����Ҫ���jar���У���Ҫ�������
		job.setJarByClass(ToHbaseMain.class);
        //����map�Լ�reduce�����֣�reduce��ʹ�ù̶��ļ���
        job.setMapperClass(HFileMapper.class);
        job.setReducerClass(KeyValueSortReducer.class);
        //�趨map�������key�Լ�value�������reduce�����ģ���Ϊʹ���Դ���reduce����������ǹ̶��Ĳ���
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);
        //����partition��������������дhfileҲֱ��Ĭ������Ϳ���
//        job.setPartitionerClass(SimpleTotalOrderPartitioner.class);
        //��������·���Լ����·��
		FileInputFormat.addInputPath(job, new Path(dst));
		FileOutputFormat.setOutputPath(job, new Path(dstOut));
		// ����Ҫ����,ϵͳ����������Ϣ���� HFileOutputFormat
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
        		
		System.out.println("=====��ʼִ��job=====");
		//ִ��job��ֱ�����
		if(job.waitForCompletion(true)){
			System.out.println("=====ִ��job�ɹ�����ʼ����hbase=====");
        	try {
        		//job�ɹ�ִ�У�Ȼ��Ѹ�hfile���뵽hbase��
        		LoadIncrementalHFiles loader;
				loader = new LoadIncrementalHFiles(hadoopConfig);
	        	loader.doBulkLoad(new Path(dstOut), table);
			} catch (Exception e) {
				System.out.println("=====����hbase�쳣��"+e.toString()+"=====");
				e.printStackTrace();
			}
        	//�������ɾ������ļ�
        	boolean deleteout = fs.delete(new Path(dstOut),true);
			System.out.println("=====�������ɾ������ļ������"+deleteout+"=====");
		}else{
			System.out.println("=====ִ��jobʧ�ܣ��˳���=====");
			System.exit(1);
		}
		System.out.println("=====������ɣ��˳���=====");
		System.exit(0);
	}
}
