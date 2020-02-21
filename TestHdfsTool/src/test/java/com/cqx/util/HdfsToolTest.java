package com.cqx.util;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HdfsToolTest {
    private static final Logger logger = LoggerFactory.getLogger(HdfsToolTest.class);
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
    public void deleteTest() throws Exception {
        FileSystem fs = null;
        try {
            Configuration conf;
            conf = HdfsTool.getRemoteConf();
            fs = HdfsTool.getFileSystem(conf);
            for (int i = 4; i < 10; i++) {
                String path = "/cqx/data/mrinput/" + i + ".txt";
                if (HdfsTool.isExist(fs, path)) HdfsTool.delete(fs, path);
            }
        } finally {
            if (fs != null) fs.close();
        }
    }

    @Test
    public void writeTest() throws Exception {
//        DFSOutputStream dfsOutputStream;
        List<WriteHdfsThread> writeHdfsThreadList = new ArrayList<>();
        for (int i = 4; i < 5; i++) {
            writeHdfsThreadList.add(new WriteHdfsThread(i));
        }
        for (WriteHdfsThread writeHdfsThread : writeHdfsThreadList) {
            writeHdfsThread.start();
        }
        for (WriteHdfsThread writeHdfsThread : writeHdfsThreadList) {
            writeHdfsThread.join();
        }
    }

    class WriteHdfsThread extends Thread {
        int file_cnt = 0;

        WriteHdfsThread(int file_cnt) {
            this.file_cnt = file_cnt;
        }

        public void run() {
            FileSystem fs = null;
            OutputStream outputStream = null;
            try {
                TimeCostUtil writeCost = new TimeCostUtil();
                TimeCostUtil allCost = new TimeCostUtil();
                Configuration conf;
                conf = HdfsTool.getRemoteConf();
//                conf.set("dfs.replication", "1");
                fs = HdfsTool.getFileSystem(conf);
                String path = "/cqx/data/mrinput/" + file_cnt + ".txt";
                outputStream = HdfsTool.createFile(fs, path);
                String content = "1234567890";
                // 数据6000行，每行1000字节
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 100; i++) {
                    sb.append(content).append("\r\n");
                }

                int cnt = 0;
                while (cnt < 30) {
                    cnt++;
                    SleepUtil.sleepMilliSecond(1);

                    allCost.start();
                    long long_cost = 0L;
                    for (int i = 0; i < 6000; i++) {
                        writeCost.start();
                        outputStream.write(sb.toString().getBytes());
                        writeCost.end();
                        long cost = writeCost.getCost();
                        if (cost > 1) {
                            long_cost += cost;
                            logger.debug("file_cnt：{}，seq：{}，writeCost：{}", file_cnt, i, cost);
                        }
                    }
                    allCost.end();
                    long all_cost = allCost.getCost();
                    logger.info("ile_cnt：{}，cnt：{}，allCost：{}，long_cost：{}", file_cnt, cnt, all_cost, long_cost);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                try {
                    if (fs != null) fs.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}