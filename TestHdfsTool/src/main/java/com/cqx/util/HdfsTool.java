package com.cqx.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapreduce.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

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
        logger.info("getFileSize：{}", path);
        try {
            if (fs.exists(path) && fs.isFile(path)) {
                return fs.getFileStatus(path).getLen();
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
        logger.info("isExist：{}", path);
        return fs.exists(new Path(path));
    }

    public static FSDataOutputStream createFile(FileSystem fs, String path) throws IOException {
        logger.info("createFile：{}", path);
        return fs.create(new Path(path));
    }

    public static FSDataOutputStream appendFile(FileSystem fs, String path) throws IOException {
        logger.info("appendFile：{}", path);
        return fs.append(new Path(path));
    }

    public static void closeFileSystem(FileSystem fs) throws IOException {
        logger.info("closeFileSystem：{}", fs);
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
        return FileSystem.newInstance(hadoopConfig);
    }

    public static boolean recoverLease(Configuration hadoopConfig, String path) {
        logger.info("start recoverLease");
        boolean result = false;
        DistributedFileSystem fs = null;
        try {
            fs = new DistributedFileSystem();
            fs.initialize(URI.create(path), hadoopConfig);
            result = fs.recoverLease(new Path(path));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.info("recoverLease end，result：{}", result);
        return result;
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
//        if (!isWindow()) {
        System.setProperty("HADOOP_USER_NAME", user_name);
//        }
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    public static Configuration getLocalConf(String conf_path) {
        Configuration hadoopConfig = new Configuration();
        String path;
        if (isWindow()) {
            path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\src\\main\\resources\\conf75\\";
        } else {
            if (conf_path != null)
                path = conf_path;
            else
                path = "/etc/hadoop/conf/";
        }
        hadoopConfig.addResource(new Path(path + "core-site.xml"));
        hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
        hadoopConfig.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        if (isWindow()) {
            hadoopConfig.set("mapreduce.framework.name", "local");
            hadoopConfig.set("fs.defaultFS", "file:///");
            hadoopConfig.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        } else {
        }
        logger.info("hadoopConfig：{}", hadoopConfig);
        return hadoopConfig;
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    public static Configuration getRemoteConf() {
        Configuration hadoopConfig = new Configuration();
        String path;
        path = "D:\\tmp\\etc\\hadoop\\conf\\";
        hadoopConfig.addResource(new Path(path + "core-site.xml"));
        hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
        hadoopConfig.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        logger.info("hadoopConfig：{}", hadoopConfig);
        return hadoopConfig;
    }

    /**
     * 是否是本地测试
     *
     * @return
     */
    public static boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取文件状态
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public static FileStatus getFileInfo(FileSystem fs, String path) throws IOException {
        logger.info("getFileInfo：{}", path);
        return fs.getFileStatus(new Path(path));
    }

    public static void ls(FileSystem fs, String path) throws IOException {
        if (fs != null) {
            logger.info("ls：{}", path);
            for (FileStatus fileStatus : fs.listStatus(new Path(path))) {
                logger.info("{} {} {} {} {}", fileStatus.getPermission(), fileStatus.getOwner(),
                        fileStatus.getGroup(), fileStatus.getLen(), fileStatus.getPath());
            }
        }
    }

    public static boolean mkdir(FileSystem fs, String path) throws IOException {
        if (fs != null && !fs.exists(new Path(path))) {
            logger.info("mkdir：{}", path);
            return fs.mkdirs(new Path(path));
        }
        return false;
    }

    public static FSDataInputStream openFile(FileSystem fs, String path) throws IOException {
        if (fs != null && fs.exists(new Path(path))) {
            logger.info("openFile：{}", path);
            return fs.open(new Path(path));
        }
        return null;
    }

    public static void copyBytes(FileSystem fs, String inputFile, String outputFile) throws IOException {
        if (fs != null && fs.exists(new Path(inputFile))) {
            logger.info("copyBytes：{} to {}.", inputFile, outputFile);
            delete(fs, outputFile);
            InputStream in = HdfsTool.openFile(fs, inputFile);
            OutputStream out = HdfsTool.createFile(fs, outputFile);
            IOUtils.copyBytes(in, out, fs.getConf());
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
//            IOUtils.writeFully(FileChannel, ByteBuffer, 10);
            logger.info("copyBytes closeStream.");
        }
    }

    public static boolean delete(FileSystem fs, String path) throws IOException {
        if (fs != null) {
            logger.info("delete：{}", path);
            return fs.deleteOnExit(new Path(path));
        }
        return false;
    }

}
