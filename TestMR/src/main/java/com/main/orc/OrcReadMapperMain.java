package com.main.orc;

import com.mr.orc.OrcReadMapper;
import com.mr.util.HadoopConfUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.orc.OrcConf;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapreduce.OrcInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orc读取测试，单mapper模式
 *
 * @author chenqixu
 */
public class OrcReadMapperMain {

    private static final Logger logger = LoggerFactory.getLogger(OrcReadMapperMain.class);

    public static void main(String[] args) throws Exception {
        //设置用户名
        HadoopConfUtil.setHadoopUser("edc_base");
        //输入路径
        String dst = HadoopConfUtil.getPath("D:\\tmp\\data\\orcouput", "/test/mroutput");
        //输出路径，必须是不存在的，空文件加也不行。
        String dstOut = HadoopConfUtil.getPath("D:\\tmp\\data\\orcouputnull", "/test/mroutputnull");
        //获取配置
        Configuration hadoopConfig = HadoopConfUtil.getLocalConf();
        //设置orc输入schema
        OrcConf.MAPRED_INPUT_SCHEMA.setString(hadoopConfig, "struct<name:string,age:string>");
        //获取分布式文件对象
        FileSystem fs = HadoopConfUtil.getFileSystem(hadoopConfig);
        //如果输出路径存在，先删除
        Path p = new Path(dstOut);
        if (fs.exists(p)) {
            fs.delete(p, true);
            logger.info("=====delPath 删除：{}=====", p.toString());
        }
        //任务
        Job job = new Job(hadoopConfig);
        //添加第三方jar包
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/orc-core-1.1.0.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/orc-mapreduce-1.1.0.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/hive-exec-1.2.0.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/hive-storage-api-2.2.1.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/aircompressor-0.8.jar");

        logger.info("schema：{}", TypeDescription.fromString(OrcConf.MAPRED_INPUT_SCHEMA.getString(hadoopConfig)));
        //任务名称
        job.setJobName("OrcReadMapperMain");
        //如果需要打成jar运行，需要下面这句
        job.setJarByClass(OrcReadMapperMain.class);
        //指定自定义的Mapper处理类和输入输出
        job.setMapperClass(OrcReadMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(NullWritable.class);
        //reduce个数
        job.setNumReduceTasks(0);
        //任务读取文件的类型
        job.setInputFormatClass(OrcInputFormat.class);
        //任务输出文件的类型
        job.setOutputFormatClass(TextOutputFormat.class);
        //job执行作业时输入和输出文件的路径
        FileInputFormat.addInputPath(job, new Path(dst));
        FileOutputFormat.setOutputPath(job, new Path(dstOut));
        //执行job，直到完成
        job.waitForCompletion(true);
        logger.info("TrackingURL：{}", job.getTrackingURL());
        logger.info("Finished");
    }
}
