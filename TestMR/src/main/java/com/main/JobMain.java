package com.main;

import com.cqx.common.utils.mapreduce.JobBuilder;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
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
                .setLocalMode()
                .buildFileSystem()
                .buildJob(JobMain.class)
//                .buildJob(JobMain.class, "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\target\\TestMR-1.0.0.jar")
                .addInputPath("hdfs://master75/cqx/data/mrinput/")
                .deleteAndSetOutPutPath("hdfs://master75/cqx/data/mroutput/", TextOutputFormat.class)
                .setMapperClass(JobMap.class)
                .setOutputKeyValueClass(LongWritable.class, Text.class)
                .setNumReduceTasks(0)
                .waitForCompletion();
    }

    public static class JobMap extends Mapper<LongWritable, Text, LongWritable, Text> {

        private FileSplit fileSplit;
        private Counter map_input_records;

        protected void setup(Context context) {
            fileSplit = (FileSplit) context.getInputSplit();
            //计数器
            map_input_records = context.getCounter("org.apache.hadoop.mapreduce.TaskCounter", "MAP_INPUT_RECORDS");
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            context.write(key, new Text(String.format("[FileSplit：%s][map_input_records：%s]%s", fileSplit.toString(), map_input_records.getValue(), value)));
        }
    }
}
