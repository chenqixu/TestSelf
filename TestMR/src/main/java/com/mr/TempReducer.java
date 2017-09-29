package com.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * �ĸ��������ͷֱ����
 * KeyIn        Reducer���������ݵ�Key��������ÿ�������еġ���ݡ�
 * ValueIn      Reducer���������ݵ�Value��������ÿ�������еġ����¡�
 * KeyOut       Reducer��������ݵ�Key�������ǲ��ظ��ġ���ݡ�
 * ValueOut     Reducer��������ݵ�Value����������һ���еġ�������¡�
 */
public class TempReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int maxValue = Integer.MIN_VALUE;
		StringBuffer sb = new StringBuffer();
		// ȡvalues�����ֵ
		for (IntWritable value : values) {
			maxValue = Math.max(maxValue, value.get());
			sb.append(value).append(", ");
		}
		// ��ӡ������ Before Reduce: 2000, 15, 23, 99, 12, 22,
		System.out.print("Before Reduce: " + key + ", " + sb.toString());
		context.write(key, new IntWritable(maxValue));
		// ��ӡ������ After Reduce: 2000, 99
		System.out.println("======"+"After Reduce: " + key + ", " + maxValue);
	}
}
