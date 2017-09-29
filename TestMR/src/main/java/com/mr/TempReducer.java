package com.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 四个泛型类型分别代表：
 * KeyIn        Reducer的输入数据的Key，这里是每行文字中的“年份”
 * ValueIn      Reducer的输入数据的Value，这里是每行文字中的“气温”
 * KeyOut       Reducer的输出数据的Key，这里是不重复的“年份”
 * ValueOut     Reducer的输出数据的Value，这里是这一年中的“最高气温”
 */
public class TempReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int maxValue = Integer.MIN_VALUE;
		StringBuffer sb = new StringBuffer();
		// 取values的最大值
		for (IntWritable value : values) {
			maxValue = Math.max(maxValue, value.get());
			sb.append(value).append(", ");
		}
		// 打印样本： Before Reduce: 2000, 15, 23, 99, 12, 22,
		System.out.print("Before Reduce: " + key + ", " + sb.toString());
		context.write(key, new IntWritable(maxValue));
		// 打印样本： After Reduce: 2000, 99
		System.out.println("======"+"After Reduce: " + key + ", " + maxValue);
	}
}
