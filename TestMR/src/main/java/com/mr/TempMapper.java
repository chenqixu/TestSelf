package com.mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 四个泛型类型分别代表：
 * KeyIn        Mapper的输入数据的Key，这里是每行文字的起始位置（0,11,...）
 * ValueIn      Mapper的输入数据的Value，这里是每行文字
 * KeyOut       Mapper的输出数据的Key，这里是每行文字中的“年份”
 * ValueOut     Mapper的输出数据的Value，这里是每行文字中的“气温”
 */
public class TempMapper extends
        Mapper<LongWritable, Text, Text, IntWritable> {

    private static final String encoding = "GBK";
    private Counter map_input_records;
    private Counter errorRecordsCounter ;
//    public enum CounterEnum {
//        errorRecords;
//    }

    protected void setup(Context context) throws IOException,
            InterruptedException {
        //获取系统默认编码
        System.out.println("系统默认编码：" + System.getProperty("file.encoding"));//查询结果GBK
        //系统默认字符编码
        System.out.println("系统默认字符编码:" + Charset.defaultCharset()); //查询结果GBK
        //操作系统用户使用的语言
        System.out.println("系统默认语言:" + System.getProperty("user.language")); //查询结果zh
        System.setProperty("file.encoding", "GBK");
        System.setProperty("user.language", "GBK");
        System.out.println("================================");
        //获取系统默认编码
        System.out.println("系统默认编码：" + System.getProperty("file.encoding"));//查询结果GBK
        //系统默认字符编码
        System.out.println("系统默认字符编码:" + Charset.defaultCharset()); //查询结果GBK
        //操作系统用户使用的语言
        System.out.println("系统默认语言:" + System.getProperty("user.language")); //查询结果zh
        //计数器
        map_input_records = context.getCounter("org.apache.hadoop.mapreduce.TaskCounter", "MAP_INPUT_RECORDS");
        //错误计数器
        errorRecordsCounter = context.getCounter("com.mr.ErrorRecordsCounter", "errorRecords");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void map(LongWritable key, Text value,
                       org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        // 打印样本: Before Mapper: 0, 2000010115
        String newValue = new String(value.getBytes(), 0, value.getLength(), encoding);
        System.out.println("Before Mapper: " + key + ", [value]" + value + "，[newValue]" + newValue + "，map_input_records：" + map_input_records.getValue());
        errorRecordsCounter.increment(1);
//		String line = value.toString();
//		String year = line.substring(0, 4);
//		int temperature = Integer.parseInt(line.substring(8));
//		context.write(new Text(year), new IntWritable(temperature));
//		// 打印样本: After Mapper:2000, 15
//		System.out.println("======" + "After Mapper:" + new Text(year) + ", "
//				+ new IntWritable(temperature));
    }

}
