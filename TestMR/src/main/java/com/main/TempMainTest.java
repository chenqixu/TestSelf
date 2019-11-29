package com.main;

import com.mr.TempMapper;
import com.mr.TempReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * TempMainTest
 *
 * @author chenqixu
 */
public class TempMainTest {

    private static String endWith(String path) {
        String result = path;
        String end = "/";
        if (result.endsWith(end)) {
            return result;
        } else {
            return result + end;
        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException, ClassNotFoundException {
        //获取配置文件
        if (args.length != 1) {
            System.exit(-1);
            System.out.println("not enough params");
        }
        String conf_path = endWith(args[0]);
        Configuration hadoopConfig = new Configuration();
        //先加载配置,FileSystem需要
        hadoopConfig.addResource(new Path(conf_path + "core-site.xml"));
        hadoopConfig.addResource(new Path(conf_path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(conf_path + "mapred-site.xml"));
        hadoopConfig.addResource(new Path(conf_path + "params.xml"));
        System.out.println("[fs.defaultFS]" + hadoopConfig.get("fs.defaultFS"));

        //输入路径
        String dst = hadoopConfig.get("input_dst");
        //输出路径，必须是不存在的，空文件加也不行。
        String dstOut = hadoopConfig.get("output_dst");
        //是否有reduce
        boolean hasReduce = hadoopConfig.getBoolean("hasReduce", false);

        FileSystem fs = null;
        try {
            fs = FileSystem.get(hadoopConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[fs]" + fs);

        //如果输出路径存在，先删除
        Path p = new Path(dstOut);
        System.out.println("[dstOut]" + dstOut + "[path]" + p);
        if (fs.exists(p)) {
            fs.delete(p, true);
            System.out.println("=====delPath 删除：" + p.toString() + "=====");
        }

        //任务
        Job job = new Job(hadoopConfig);
        //如果需要打成jar运行，需要下面这句
        job.setJarByClass(TempMainTest.class);
        //job执行作业时输入文件的路径
        FileInputFormat.addInputPath(job, new Path(dst));
        //job执行作业时输出文件的路径
        FileOutputFormat.setOutputPath(job, new Path(dstOut));
        //指定自定义的Mapper
        job.setMapperClass(TempMapper.class);
        //设置最后输出结果的Key和Value的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //是否有Reduce
        if (hasReduce) {
            //指定自定义的Reducer
            job.setReducerClass(TempReducer.class);
        } else {
            job.setNumReduceTasks(0);
        }

        //执行job，直到完成
        job.waitForCompletion(true);
        System.out.println("TrackingURL:" + job.getTrackingURL());
        System.out.println("Finished");
    }
}
