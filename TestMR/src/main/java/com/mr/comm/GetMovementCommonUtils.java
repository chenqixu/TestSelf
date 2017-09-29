package com.mr.comm;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class GetMovementCommonUtils {
	/**
	 * @description: 在输入路径下根据汇总时间条件过滤文件名，配置对应的mapper类进行处理
	 * @param job 任务对象
	 * @param fs 文件系统对象
	 * @param path 输入路径
	 * @param mapper 对应的mapper处理类
	 */
	@SuppressWarnings("unchecked")
	public static void addInputPath(Job job, FileSystem fs, String path, Class mapper) {
		//输入路径
		Path inputPath=new Path(path);
		try {
			if(fs.exists(inputPath)){
				//列出目录下文件的元数据信息
				FileStatus[] fileStatusArr=fs.listStatus(inputPath);
				for(FileStatus fileStatus:fileStatusArr){
					System.out.println("输入文件：" + fileStatus.getPath().toString());
					//取文件作为输入
					MultipleInputs.addInputPath(job, fileStatus.getPath(), TextInputFormat.class,mapper);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
