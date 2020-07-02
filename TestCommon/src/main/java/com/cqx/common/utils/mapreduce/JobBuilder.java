package com.cqx.common.utils.mapreduce;

import com.cqx.common.utils.hdfs.HdfsTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JobBuilder
 *
 * @author chenqixu
 */
public class JobBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JobBuilder.class);
    private Configuration configuration;
    private FileSystem fs;
    private Job job;
    private HdfsTool hdfsTool = new HdfsTool();
    private Class<? extends OutputFormat> outputFormatClass;
    private boolean hasReduce = false;

    public static JobBuilder newbuilder(String user_name) {
        HdfsTool.setHadoopUser(user_name);
        return new JobBuilder();
    }

    public static JobBuilder newbuilder() {
        return new JobBuilder();
    }

    /**
     * 创建hadoop配置
     *
     * @param confPath
     * @return
     */
    public JobBuilder buildConf(String confPath) {
        configuration = new Configuration();
        configuration.addResource(new Path(confPath + "/core-site.xml"));
        configuration.addResource(new Path(confPath + "/hdfs-site.xml"));
        configuration.addResource(new Path(confPath + "/mapred-site.xml"));
        configuration.addResource(new Path(confPath + "/yarn-site.xml"));
        logger.info("configuration：{}", configuration);
//        List<String> params = new ArrayList<>();
//        params.add("mapreduce.framework.name");
//        params.add("yarn.resourcemanager.address");
//        params.add("fs.defaultFS");
//        params.add("mapreduce.app-submission.cross-platform");
//        params.add("yarn.resourcemanager.hostname");
//        for (String p : params)
//            logger.info("{}：{}", p, configuration.get(p));
        return this;
    }

    /**
     * 本地模式
     *
     * @return
     */
    public JobBuilder setLocalMode() {
        if (configuration != null) {
            configuration.set("mapreduce.framework.name", "local");
        }
        return this;
    }

    /**
     * 创建FileSystem
     *
     * @return
     * @throws IOException
     */
    public JobBuilder buildFileSystem() throws IOException {
        if (configuration != null)
            fs = FileSystem.get(configuration);
        return this;
    }

    /**
     * 创建任务
     *
     * @param jobClass
     * @param jarName
     * @return
     * @throws IOException
     */
    public JobBuilder buildJob(Class jobClass, String jarName) throws IOException {
        if (configuration != null && jobClass != null) {
            //创建任务
            job = Job.getInstance(configuration, jobClass.getName());
            job.setJarByClass(jobClass);
            //如果是window系统，需要设置jar，测试了linux不需要
            if (hdfsTool.isWindow() && jarName != null && jarName.length() > 0)
                job.setJar(jarName);
            return this;
        }
        return null;
    }

    /**
     * job执行作业时输入文件的路径
     *
     * @param dst
     * @return
     * @throws IOException
     */
    public JobBuilder addInputPath(String dst) throws IOException {
        if (job != null) {
            FileInputFormat.addInputPath(job, new Path(dst));
            return this;
        }
        return null;
    }

    /**
     * 删除输出目录，并设置输出文件格式
     *
     * @param outputPath
     * @return
     * @throws IOException
     */
    public JobBuilder deleteAndSetOutPutPath(String outputPath, Class<? extends OutputFormat> outputFormatClass) throws IOException {
        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }
        if (job != null) {
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            if (outputFormatClass != null) {
                job.setOutputFormatClass(outputFormatClass);
                this.outputFormatClass = outputFormatClass;
            } else {
                logger.error("请设置输出文件格式！");
                return null;
            }
            return this;
        }
        return null;
    }

    /**
     * 指定自定义的Mapper任务处理类
     *
     * @param cls
     * @return
     */
    public JobBuilder setMapperClass(Class<? extends Mapper> cls) {
        if (job != null) {
            job.setMapperClass(cls);
            return this;
        }
        return null;
    }

    /**
     * 指定自定义的Reducer任务处理类
     *
     * @param cls
     * @return
     */
    public JobBuilder setReducerClass(Class<? extends Reducer> cls) {
        if (job != null) {
            job.setReducerClass(cls);
            hasReduce = true;
            return this;
        }
        return null;
    }

    /**
     * 设置Map输出结果的Key和Value的类型
     *
     * @param keyClass
     * @param valueClass
     * @return
     */
    public JobBuilder setMapOutputKeyValueClass(Class<?> keyClass, Class<?> valueClass) {
        if (job != null) {
            job.setMapOutputKeyClass(keyClass);
            job.setMapOutputValueClass(valueClass);
            return this;
        }
        return null;
    }

    /**
     * 设置最后输出结果的Key和Value的类型
     *
     * @param keyClass
     * @param valueClass
     * @return
     */
    public JobBuilder setOutputKeyValueClass(Class<?> keyClass, Class<?> valueClass) {
        if (job != null) {
            job.setOutputKeyClass(keyClass);
            job.setOutputValueClass(valueClass);
            return this;
        }
        return null;
    }

    /**
     * 设置Reduce个数
     *
     * @param tasks
     * @return
     */
    public JobBuilder setNumReduceTasks(int tasks) {
        if (job != null) {
            job.setNumReduceTasks(tasks);
            return this;
        }
        return null;
    }

    /**
     * 避免reduce输出为空文件
     */
    private void skipReduceNullFile() {
        if (job != null && hasReduce) {
            LazyOutputFormat.setOutputFormatClass(job, outputFormatClass);
        }
    }

    /**
     * 提交任务并等待运行结果
     *
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public boolean waitForCompletion() throws InterruptedException, IOException, ClassNotFoundException {
        if (job != null) {
            //如果有Reduce，避免输出空文件
            skipReduceNullFile();
            //提交任务并等待运行结果
            return job.waitForCompletion(true);
        }
        return false;
    }

}
