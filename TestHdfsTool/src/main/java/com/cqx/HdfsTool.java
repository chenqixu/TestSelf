package com.cqx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsTool {
	// �����ļ�
	private Configuration conf = null;
	// �ֲ�ʽ�ļ�ϵͳ
	private FileSystem fs = null;

	public HdfsTool(){
		conf = new Configuration();
	}
	
	/**
	 * ���������ļ�
	 * */
	public void initConf(InputBean bean){
		String confpath = bean.getConfpath();
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
			try {
				fs = FileSystem.get(conf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * hdfs�ϻ���ļ���С
	 * */
	public static long getFileSize(FileSystem fs, Path path){
		try {
			if (fs.exists(path)) {
				return fs.listStatus(path)[0].getLen();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0L;
		} catch (IOException e) {
			e.printStackTrace();
			return 0L;
		}
		return 0L;
	}	
	
}
