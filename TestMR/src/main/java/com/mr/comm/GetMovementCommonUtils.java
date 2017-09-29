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
	 * @description: ������·���¸��ݻ���ʱ�����������ļ��������ö�Ӧ��mapper����д���
	 * @param job �������
	 * @param fs �ļ�ϵͳ����
	 * @param path ����·��
	 * @param mapper ��Ӧ��mapper������
	 */
	@SuppressWarnings("unchecked")
	public static void addInputPath(Job job, FileSystem fs, String path, Class mapper) {
		//����·��
		Path inputPath=new Path(path);
		try {
			if(fs.exists(inputPath)){
				//�г�Ŀ¼���ļ���Ԫ������Ϣ
				FileStatus[] fileStatusArr=fs.listStatus(inputPath);
				for(FileStatus fileStatus:fileStatusArr){
					System.out.println("�����ļ���" + fileStatus.getPath().toString());
					//ȡ�ļ���Ϊ����
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
