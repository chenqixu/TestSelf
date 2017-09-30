package com.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * �ĸ��������ͷֱ����
 * KeyIn        Mapper���������ݵ�Key��������ÿ�����ֵ���ʼλ�ã�0,11,...��
 * ValueIn      Mapper���������ݵ�Value��������ÿ������
 * KeyOut       Mapper��������ݵ�Key��������ÿ�������еġ���ݡ�
 * ValueOut     Mapper��������ݵ�Value��������ÿ�������еġ����¡�
 */
public class TempMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {

	@SuppressWarnings("unchecked")
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// ��ӡ����: Before Mapper: 0, 2000010115
		System.out.print("Before Mapper: " + key + ", " + value);
		String line = value.toString();
		String year = line.substring(0, 4);
		int temperature = Integer.parseInt(line.substring(8));
		context.write(new Text(year), new IntWritable(temperature));
		// ��ӡ����: After Mapper:2000, 15
		System.out.println("======" + "After Mapper:" + new Text(year) + ", "
				+ new IntWritable(temperature));
	}

}
