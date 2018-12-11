package com.main;

import com.mr.TempMapper;
import com.mr.TempReducer;
import com.mr.comm.GetMovementCommonUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 多文件输入测试
 *
 * @author chenqixu
 */
public class MultipleInputMain {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "edc_base");
        //输入路径
        String dst = "/test/multipleinput/data1";
        //输出路径，必须是不存在的，空文件加也不行。
        String dstOut = "/test/mroutput";
        Configuration hadoopConfig = new Configuration();
        //先加载配置,FileSystem需要
//        String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\src\\main\\resources\\conf75\\";
        String path = "/etc/hadoop/conf/";
        hadoopConfig.addResource(new Path(path + "core-site.xml"));
        hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
        FileSystem fs = null;
        try {
            fs = FileSystem.get(hadoopConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果输出路径存在，先删除
        Path p = new Path(dstOut);
        if (fs.exists(p)) {
            fs.delete(p, true);
            System.out.println("=====delPath 删除：" + p.toString() + "=====");
        }
        //任务
        Job job = new Job(hadoopConfig);
        job.setJobName("MultipleInputMain");
        //如果需要打成jar运行，需要下面这句
        job.setJarByClass(MultipleInputMain.class);
        //job执行作业时输入和输出文件的路径
        GetMovementCommonUtils.addInputPath(job, fs, dst, TempMapper.class);
        FileOutputFormat.setOutputPath(job, new Path(dstOut));
        //指定自定义的Mapper和Reducer作为两个阶段的任务处理类
        job.setMapperClass(TempMapper.class);
        job.setReducerClass(TempReducer.class);
        //设置最后输出结果的Key和Value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //执行job，直到完成
        job.waitForCompletion(true);
        System.out.println("TrackingURL:"+job.getTrackingURL());
        System.out.println("Finished");
    }
}
