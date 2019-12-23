package com.cqx.util;

import com.cqx.common.utils.system.SleepUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

public class HdfsToolTest {
    //配置文件
    private Configuration conf = null;
    //分布式文件系统
    private FileSystem fs = null;

    @Before
    public void setUp() throws Exception {
//        HdfsTool.setHadoopUser("udapdev");
        HdfsTool.setHadoopUser("edc_base");
    }

    private void runThread(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Configuration conf = null;
                FileSystem fs = null;
                try {
                    conf = HdfsTool.getRemoteConf();
                    fs = HdfsTool.getFileSystem(conf);
                    System.out.println(this + "###" + HdfsTool.getFileInfo(fs, path));
                    OutputStream fsDataOutputStream = HdfsTool.appendFile(fs, path);
                    System.out.println(this + "###append###" + fsDataOutputStream);
                } catch (Exception e) {
                    HdfsTool.recoverLease(conf, path);
                    try {
                        System.out.println(this + "###" + HdfsTool.getFileInfo(fs, path));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Test
    public void getFileInfo() throws Exception {
        final String path = "hdfs://edc01:8020/usr/test/data/rc_hw/x2/201801010000/data0/x2_data0_2018010100_0_000111.tmp";
        runThread(path);
//        SleepUtil.sleepSecond(60);
//        runThread(path);
//        HdfsTool.closeFileSystem(fs);
        SleepUtil.sleepSecond(2);
    }

    @Test
    public void IOTest() throws Exception {
        Configuration conf = null;
        FileSystem fs = null;
        try {
            conf = HdfsTool.getRemoteConf();
            fs = HdfsTool.getFileSystem(conf);
//            HdfsTool.ls(fs, "/test0304.txt");
//            HdfsTool.ls(fs, "/tmp/cqx");
//            HdfsTool.mkdir(fs, "/tmp/cqx");
            HdfsTool.copyBytes(fs, "/tmp/cqx/task_102629185128.2019031310231400-worker-6813.log.tgz", "/tmp/cqx/2.tgz");
        } finally {
            HdfsTool.closeFileSystem(fs);
        }
    }

    @Test
    public void writeTest() throws Exception {
        Configuration conf;
        FileSystem fs;
        conf = HdfsTool.getRemoteConf();
        fs = HdfsTool.getFileSystem(conf);
        String path = "/cqx/data/mrinput/2.txt";
        OutputStream outputStream = HdfsTool.createFile(fs, path);
        String content = "1234567890";
        outputStream.write(content.getBytes());
        outputStream.close();
        fs.close();
    }
}