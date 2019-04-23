package com.mr.util;

import com.mr.bean.DBBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * JobBuilder
 *
 * @author chenqixu
 */
public class JobBuilder {
    private Configuration configuration;
    private FileSystem fs;
    private Job job;

    public static JobBuilder newbuilder() {
        return new JobBuilder();
    }

    public JobBuilder buildConf(String confPath) {
        configuration = new Configuration();
        configuration.addResource(new Path(confPath + "/core-site.xml"));
        configuration.addResource(new Path(confPath + "/hdfs-site.xml"));
        configuration.addResource(new Path(confPath + "/mapred-site.xml"));
        configuration.addResource(new Path(confPath + "/yarn-site.xml"));
        return this;
    }

    public JobBuilder buildFileSystem() throws IOException {
        if (configuration != null)
            fs = FileSystem.get(configuration);
        return this;
    }

    public JobInterface buildJob(Class jobClass, String jarName) throws IOException {
        if (configuration != null && jobClass != null) {
            job = new Job(configuration, jobClass.getName());
            job.setJarByClass(jobClass);
            if (HadoopConfUtil.isWindow() && jarName != null && jarName.length() > 0)
                job.setJar(jarName);
            return new JobImpl();
        }
        return null;
    }

    public JobBuilder buildDBConfig(DBBean dbBean) {
        if (configuration != null)
            DBConfiguration.configureDB(configuration, dbBean.getDbType().getDriverClass(),
                    dbBean.getDbUrl(), dbBean.getUserName(), dbBean.getPasswd());
        return this;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public FileSystem getFs() {
        return fs;
    }

    public Job getJob() {
        return job;
    }

    class JobImpl implements JobInterface {

        public JobInterface setLocalMode() {
            if (configuration != null) {
                configuration.set("mapreduce.framework.name", "local");
            }
            return this;
        }

        /**
         * 设置输入数据格式化的类和设置数据来源
         *
         * @param cls
         * @param tableName
         * @param conditions
         * @param orderBy
         * @param fieldNames
         */
        public JobInterface setDBInputFormat(Class cls, String tableName, String conditions, String orderBy, String... fieldNames) {
            if (job != null) {
                job.setInputFormatClass(DBInputFormat.class);
                DBInputFormat.setInput(job, cls, tableName, conditions, orderBy, fieldNames);
            }
            return this;
        }

        /**
         * 设置自定义的Mapper类和Mapper输出的key和value的类型
         *
         * @param mapperClass
         * @param outKeyClass
         * @param outvalueClass
         */
        public JobInterface setMapper(Class mapperClass, Class outKeyClass, Class outvalueClass) {
            if (job != null) {
                job.setMapperClass(mapperClass);
                job.setMapOutputKeyClass(outKeyClass);
                job.setMapOutputValueClass(outvalueClass);
            }
            return this;
        }

        /**
         * 指定Reducer类和输出key和value的类型
         *
         * @param reducerClass  可以为空
         * @param outKeyClass
         * @param outvalueClass
         */
        public JobInterface setReducer(Class reducerClass, Class outKeyClass, Class outvalueClass) {
            if (job != null) {
                if (reducerClass != null)
                    job.setReducerClass(reducerClass);
                job.setOutputKeyClass(outKeyClass);
                job.setOutputValueClass(outvalueClass);
            }
            return this;
        }

        public JobInterface setNullReducer(Class outKeyClass, Class outvalueClass) {
            setReducer(null, outKeyClass, outvalueClass);
            return this;
        }

        /**
         * 如果输出目录存在就删除
         *
         * @param outputPath
         * @return
         * @throws IOException
         */
        public JobInterface deleteAndSetOutPutPath(String outputPath, Class outputFormatClass) throws IOException {
            if (fs.exists(new Path(outputPath))) {
                fs.delete(new Path(outputPath), true);
            }
            if (job != null) {
                FileOutputFormat.setOutputPath(job, new Path(outputPath));
                job.setOutputFormatClass(outputFormatClass);
            }
            return this;
        }

        public boolean waitForCompletion() throws InterruptedException,
                IOException, ClassNotFoundException {
            if (job != null)
                return job.waitForCompletion(true);
            return false;
        }
    }
}
