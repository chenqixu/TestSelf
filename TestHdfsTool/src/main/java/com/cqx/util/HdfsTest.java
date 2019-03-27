package com.cqx.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * HdfsTest
 *
 * @author chenqixu
 */
public class HdfsTest {

    /**
     * 写一个文件到hdfs，写100次（没有sync），计时
     *
     * @param fs
     * @param filepath
     * @param fsfilepath
     * @throws IOException
     */
    public static void write(FileSystem fs, String filepath, String fsfilepath) throws IOException {
        System.out.println("filepath：" + filepath + "，fsfilepath：" + fsfilepath);
        FSDataOutputStream out = null;
        FileInputStream in = null;
        Date start = new Date();
        try {
            out = fs.create(new Path(fsfilepath));
            int i = 0;
            while (i < 100) {
                in = new FileInputStream(filepath);
                IOUtils.copyBytes(in, out, 4096);
                IOUtils.closeStream(in);
                long size = fs.getFileStatus(new Path(fsfilepath)).getLen();
                System.out.println("i：" + i + "，size：" + size);
                i++;
            }
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }
        long size = fs.getFileStatus(new Path(fsfilepath)).getLen();
        Date end = new Date();
        System.out.println("start：" + start + "，end：" + end + "，time：" + (end.getTime() - start.getTime()) + "，size：" + size);
    }

    /**
     * 写一个文件到hdfs，写100次，每次需要sync，计时
     *
     * @param fs
     * @param filepath
     * @param fsfilepath
     * @throws IOException
     */
    public static void writeHsync(FileSystem fs, String filepath, String fsfilepath) throws IOException {
        System.out.println("filepath：" + filepath + "，fsfilepath：" + fsfilepath);
        FSDataOutputStream out = null;
        FileInputStream in = null;
        Date start = new Date();
        try {
            out = fs.create(new Path(fsfilepath));
            IOUtils.closeStream(out);
            int i = 0;
            while (i < 100) {
                out = fs.append(new Path(fsfilepath));
                in = new FileInputStream(filepath);
                IOUtils.copyBytes(in, out, 4096);
                IOUtils.closeStream(in);
                IOUtils.closeStream(out);
                long size = fs.getFileStatus(new Path(fsfilepath)).getLen();
                System.out.println("i：" + i + "，size：" + size);
                i++;
            }
        } finally {
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
        }
        long size = fs.getFileStatus(new Path(fsfilepath)).getLen();
        Date end = new Date();
        System.out.println("start：" + start + "，end：" + end + "，time：" + (end.getTime() - start.getTime()) + "，size：" + size);
    }

    /**
     * 获取文件大小
     *
     * @param fs
     * @param file
     * @throws InterruptedException
     */
    public static void getFileSize(FileSystem fs, String file) throws InterruptedException {
        int i = 0;
        Date start = new Date();
        while (i < 1000) {
            try {
                long size = fs.getFileStatus(new Path(file)).getLen();
                System.out.println("file：" + file + "，size：" + size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
        Date end = new Date();
        System.out.println("start：" + start + "，end：" + end + "，time：" + (end.getTime() - start.getTime()));
    }

    public static void main(String[] args) throws Exception {
        String path = "/etc/hadoop/conf/";
        String file = "/1.txt";
        String localfile = "";
        path = args[0];
        file = args[1];
        Configuration hadoopConfig = null;
        FileSystem fs = null;
        try {
            if (args.length == 2) {
                hadoopConfig = new Configuration();
                hadoopConfig.addResource(new Path(path + "core-site.xml"));
                hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
                hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
                hadoopConfig.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
                hadoopConfig.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
                // 3bch
                hadoopConfig.set("hadoop.security.bdoc.access.id", "77563690667c9a44241e");
                hadoopConfig.set("hadoop.security.bdoc.access.key", "9ab02768668d9c3ec7c07023aa26bb6c125c2bab");
                fs = FileSystem.newInstance(hadoopConfig);
                getFileSize(fs, file);
            } else if (args.length == 3) {
                localfile = args[2];
                hadoopConfig = new Configuration();
                hadoopConfig.addResource(new Path(path + "core-site.xml"));
                hadoopConfig.addResource(new Path(path + "hdfs-site.xml"));
                hadoopConfig.addResource(new Path(path + "mapred-site.xml"));
                hadoopConfig.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
                hadoopConfig.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");
                // 3bch
                hadoopConfig.set("hadoop.security.bdoc.access.id", "77563690667c9a44241e");
                hadoopConfig.set("hadoop.security.bdoc.access.key", "9ab02768668d9c3ec7c07023aa26bb6c125c2bab");
                fs = FileSystem.newInstance(hadoopConfig);
                writeHsync(fs, file, localfile);
            } else {
                System.out.println("args enougth!");
                System.exit(-1);
            }
        } finally {
            if (fs != null)
                fs.close();
        }
    }
}
