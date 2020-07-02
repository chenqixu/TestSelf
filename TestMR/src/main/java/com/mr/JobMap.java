package com.mr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * JobMap
 *
 * @author chenqixu
 */
public class JobMap extends Mapper<LongWritable, Text, NullWritable, Text> {

    protected void setup(Context context) throws IOException, InterruptedException {
    }

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        context.write(NullWritable.get(), value);
    }
}
