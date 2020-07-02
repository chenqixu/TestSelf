package com.main;

import com.cqx.common.utils.mapreduce.JobBuilder;
import com.mr.JobMap;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * JobMain
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
}
