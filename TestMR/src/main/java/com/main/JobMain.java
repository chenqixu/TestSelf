package com.main;

import com.cqx.common.utils.mapreduce.JobBuilder;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

/**
 * 本地MR提交远程测试，Mapper使用静态公共内部类
 *
 * @author chenqixu
 */
public class JobMain {

    public static void main(String[] args) throws Exception {
        JobBuilder.newbuilder("edc_base")
                .buildConf("d:\\tmp\\etc\\hadoop\\conf75\\")
                .buildFileSystem()
                .buildJob(JobMain.class, "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\target\\TestMR-1.0.0.jar")
                .addInputPath("hdfs://master75/cqx/data/mrinput/")
                .deleteAndSetOutPutPath("hdfs://master75/cqx/data/mroutput/", TextOutputFormat.class)
                .setMapperClass(JobMap.class)
                .setOutputKeyValueClass(NullWritable.class, Text.class)
                .setNumReduceTasks(0)
                .waitForCompletion();
    }

    public static class JobMap extends Mapper<LongWritable, Text, NullWritable, Text> {

        protected void setup(Context context) throws IOException, InterruptedException {
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(NullWritable.get(), value);
        }
    }
}
