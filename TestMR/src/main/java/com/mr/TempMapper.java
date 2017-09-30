package com.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 四个泛型类型分别代表：
 * KeyIn        Mapper的输入数据的Key，这里是每行文字的起始位置（0,11,...）
 * ValueIn      Mapper的输入数据的Value，这里是每行文字
 * KeyOut       Mapper的输出数据的Key，这里是每行文字中的“年份”
 * ValueOut     Mapper的输出数据的Value，这里是每行文字中的“气温”
 */
public class TempMapper extends
		Mapper<LongWritable, Text, Text, IntWritable> {

	@SuppressWarnings("unchecked")
	@Override
	protected void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper.Context context)
			throws IOException, InterruptedException {
		// 打印样本: Before Mapper: 0, 2000010115
		System.out.print("Before Mapper: " + key + ", " + value);
		String line = value.toString();
		String year = line.substring(0, 4);
		int temperature = Integer.parseInt(line.substring(8));
		context.write(new Text(year), new IntWritable(temperature));
		// 打印样本: After Mapper:2000, 15
		System.out.println("======" + "After Mapper:" + new Text(year) + ", "
				+ new IntWritable(temperature));
	}

}
