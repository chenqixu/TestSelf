package com.cqx.mr;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.cqx.HbaseInputBean;

public class OldTableDeal {
	// �����ļ�
	private Configuration conf = null;
	// ����
	private static final int REGION_PARTITION_NUM = 200;
	// ����ȡģ
	private Random random = new Random();
	// �ָ���
	public final String split_str = ""+((char)((int)01));

	// ��ʼ�������������ļ�
	public OldTableDeal(){
		conf = HBaseConfiguration.create();		
	}
	
	/**
	 * ���������ļ�
	 * */
	public void initConf(HbaseInputBean hib){
		String confpath = hib.getConfpath();
		// ɨ��conf path�µ�����xml�����ļ�
		File cp = new File(confpath);
		if(cp.isDirectory()){
			for(File resource : cp.listFiles()){
				String _path = resource.getPath();
				if(_path.endsWith(".xml")){
					conf.addResource(new Path(_path));
				}
			}
			System.out.println("[conf]"+conf);
		}
	}
	
	public void getHBaseData(int numOfDays) throws IOException,
		InterruptedException, ClassNotFoundException {
		
		
	}
	
	public static void main(String[] args) {
		
	}
}
