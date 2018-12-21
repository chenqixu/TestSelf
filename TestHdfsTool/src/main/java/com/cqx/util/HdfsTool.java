package com.cqx.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

public class HdfsTool {

    private static final Logger logger = LoggerFactory.getLogger(HdfsTool.class);

    /**
     * hdfs上获得文件大小
     *
     * @param fs
     * @param path
     * @return
     */
    public static long getFileSize(FileSystem fs, Path path) {
        try {
            if (fs.exists(path)) {
                return fs.listStatus(path)[0].getLen();
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        }
        return 0L;
    }

    /**
     * 文件或路径是否存在
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean isExist(FileSystem fs, String path) throws IOException {
        return fs.exists(new Path(path));
    }

    public static FSDataOutputStream createFile(FileSystem fs, String path) throws IOException {
        return fs.create(new Path(path));
    }

    public static FSDataOutputStream appendFile(FileSystem fs, String path) throws IOException {
        return fs.append(new Path(path));
    }

    public static void closeFileSystem(FileSystem fs) throws IOException {
        if (fs != null)
            fs.close();
    }

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
                logger.error(e.getMessage(), e);
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
        } else {
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
