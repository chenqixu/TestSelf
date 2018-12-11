package com.main.orc;

import com.mr.orc.OrcMapper;
import com.mr.orc.OrcReducer;
import com.mr.util.HadoopConfUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.orc.OrcConf;
import org.apache.orc.TypeDescription;
import org.apache.orc.mapred.OrcStruct;
import org.apache.orc.mapreduce.OrcOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orc测试MR，既有mapper，也有reducer
 *
 * @author chenqixu
 */
public class OrcMapAndReducerMain {

    private static final Logger logger = LoggerFactory.getLogger(OrcMapAndReducerMain.class);

    public static void main(String[] args) throws Exception {
        //设置用户名
        HadoopConfUtil.setHadoopUser("edc_base");
        //输入路径
        String dst = HadoopConfUtil.getPath("D:\\tmp\\data\\input\\orcinput.data", "/cqx/data/orc/orcinput.data");
        //输出路径，必须是不存在的，空文件加也不行。
        String dstOut = HadoopConfUtil.getPath("D:\\tmp\\data\\orcouput", "/test/mroutput");
        //获取配置
        Configuration hadoopConfig = HadoopConfUtil.getLocalConf();
        //设置orc输出schema
        OrcConf.MAPRED_OUTPUT_SCHEMA.setString(hadoopConfig, "struct<name:string,age:string>");
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
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/hive-exec-1.1.0-cdh5.14.2.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/hive-storage-api-2.2.1.jar");
        HadoopConfUtil.addArchiveToClassPath(job, "/cqx/lib/aircompressor-0.8.jar");

        logger.info("schema：{}", TypeDescription.fromString(OrcConf.MAPRED_OUTPUT_SCHEMA.getString(hadoopConfig)));
        //任务名称
        job.setJobName("OrcMain");
        //如果需要打成jar运行，需要下面这句
        job.setJarByClass(OrcMapAndReducerMain.class);
        //指定自定义的Mapper处理类和输入输出
        job.setMapperClass(OrcMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);
        //指定自定义的Reducer处理类和输入输出
        job.setReducerClass(OrcReducer.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(OrcStruct.class);
        //reduce个数
        job.setNumReduceTasks(1);
        //任务读取文件的类型
        job.setInputFormatClass(TextInputFormat.class);
        //任务输出文件的类型
        job.setOutputFormatClass(OrcOutputFormat.class);
        //job执行作业时输入和输出文件的路径
        FileInputFormat.addInputPath(job, new Path(dst));
        OrcOutputFormat.setOutputPath(job, new Path(dstOut));
        //执行job，直到完成
        job.waitForCompletion(true);
        logger.info("TrackingURL：{}", job.getTrackingURL());
        logger.info("Finished");
    }
}
