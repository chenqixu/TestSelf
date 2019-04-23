package com.mr.db;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * DBMapper
 *
 * @author chenqixu
 */
public class DBMapper extends Mapper<LongWritable, Student, Text, NullWritable> {
    //创建map输出时的key类型
    private Text mapOutKey = new Text();
//    //创建map输出时的value类型
//    private Text mapOutValue = new Text();

    @Override
    protected void map(LongWritable key, Student value, Context context)
            throws IOException, InterruptedException {
//        //创建输出的key:把id当做key
//        mapOutKey.set(String.valueOf(value.getId()));
//        //创建输出的value：把name当做value
//        mapOutValue.set(value.getName());
        mapOutKey.set(value.toString());
        //通过context写出去
        context.write(mapOutKey, NullWritable.get());
    }
}
