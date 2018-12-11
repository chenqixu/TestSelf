package com.mr.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

/**
 * hadoop配置工具类
 *
 * @author chenqixu
 */
public class HadoopConfUtil {

    /**
     * 通过配置获取分布式文件对象
     *
     * @param hadoopConfig
     * @return
     * @throws IOException
     */
    public static FileSystem getFileSystem(Configuration hadoopConfig) throws IOException {
        return FileSystem.get(hadoopConfig);
    }

    /**
     * 获取路径
     *
     * @param local
     * @param remote
     * @return
     */
    public static String getPath(String local, String remote) {
        if (!isWindow()) {
            return remote;
        }
        return local;
    }

    /**
     * 添加第三方jar包
     *
     * @param job
     * @param path
     */
    public static void addArchiveToClassPath(Job job, String path) {
        if (!isWindow()) {
            try {
                job.addArchiveToClassPath(new Path(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置hadoop用户
     *
     * @param user_name
     */
    public static void setHadoopUser(String user_name) {
        if (!isWindow()) {
            System.setProperty("HADOOP_USER_NAME", user_name);
        }
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    public static Configuration getLocalConf() {
        Configuration hadoopConfig = new Configuration();
        String path;
        if (isWindow()) {
            path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\src\\main\\resources\\conf75\\";
            hadoopConfig.set("mapreduce.framework.name", "local");
            hadoopConfig.set("fs.defaultFS", "file:///");
        }else{
            path = "/etc/hadoop/conf/";
        }
        hadoopConfig.addResource(new Path(path + "core-site.xml"));
        hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
        return hadoopConfig;
    }

    /**
     * 是否是本地测试
     *
     * @return
     */
    private static boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }
}
