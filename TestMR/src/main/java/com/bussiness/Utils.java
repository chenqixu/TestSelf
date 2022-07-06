package com.bussiness;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class Utils {
	
	/**
	 * 增加输入文件到job
	 * */
	public static void addInputPath(Job job, FileSystem fs, String path,
			String filter, Class mapper) {
		// 输入路径
		Path inputPath = new Path(path);
		try {
			if (fs.exists(inputPath)) {
				// 列出目录下文件的元数据信息
				FileStatus[] fileStatusArr = fs.listStatus(inputPath);
				for (FileStatus fileStatus : fileStatusArr) {
					if (fileStatus.getPath().getName().toString().contains(filter)) {
						System.out.println(">>>>>>输入文件："
								+ fileStatus.getPath().toString());
						// 取文件作为输入
						MultipleInputs.addInputPath(job, fileStatus.getPath(),
								TextInputFormat.class, mapper);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
