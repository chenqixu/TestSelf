package com.cqx.common.utils.hdfs;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class HdfsToolTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(HdfsToolTest.class);
    private static final String conf = "d:\\tmp\\etc\\hadoop\\conf75\\";
    private HdfsTool hdfsTool;

    @Before
    public void setUp() throws Exception {
        HdfsTool.setHadoopUser("edc_base");
        HdfsBean hdfsBean = new HdfsBean();
        hdfsTool = new HdfsTool(conf, hdfsBean);
    }

    @After
    public void tearDown() throws Exception {
        hdfsTool.closeFileSystem();
    }

    @Test
    public void createFile() throws Exception {
        //欧元测试
        try (OutputStream os = hdfsTool.createFile("/cqx/data/ouyuan")) {
//            os.write("€".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            os.write("\r".getBytes(StandardCharsets.UTF_8));
//            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\n".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\r\n".getBytes(StandardCharsets.UTF_8));
            os.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            os.write("\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    public void createFileCodeTest() throws Exception {
        try (OutputStream os = hdfsTool.createFile("/data/otherdata/code_test/gbk1.txt");){
//             OutputStreamWriter osw = new OutputStreamWriter(os, "GBK")) {
//            osw.write("你好");
            os.write("你好GBK\n".getBytes("GBK"));
            os.write("你好MS936\n".getBytes("MS936"));
            os.write("\u20ACGBK\n".getBytes("GBK"));
            os.write("\u20ACMS936\n".getBytes("MS936"));
        }
    }

    @Test
    public void delete() throws IOException {
        hdfsTool.delete("/cqx/data/ouyuan");
    }

    @Test
    public void openFile() throws Exception {
        try (InputStream is = hdfsTool.openFile("/cqx/data/ouyuan");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String str;
            while ((str = br.readLine()) != null) {
                logger.info("{}", str);
            }
        }
    }

    @Test
    public void all() throws Exception {
        delete();
        createFile();
        openFile();
    }

    @Test
    public void ls() throws Exception {
        String scan_path = "hdfs://master75/user/bdoc/20/services/hdfs/17/yz/bigdata/if_upload_hb_netlog/[date:yyyyMMddHHmmss]/[type]/[content]";
//        scan_path = "hdfs://master75/user/bdoc/20/services/hdfs/17/yz/bigdata/if_upload_hb_netlog/[date:yyyyMMddHHmmss]/nat/[content]";
        //按[切割，找到]，把内容截取出来
        String[] arr = scan_path.split("/", -1);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("[")) {
                int index = arr[i].indexOf("]");
                String param = arr[i].substring(1, index);
                logger.info("i：{}，param：{}", i, param);
                scan_path = scan_path.replace("[" + param + "]", "*");
            }
        }
        logger.info("scan_path：{}", scan_path);
        for (String path : hdfsTool.lsPath(scan_path)) {
            logger.info("path：{}", path);
        }
    }

    @Test
    public void muWrite() {
        //多个同时写
        ThreadTool threadTool = new ThreadTool();
        final AtomicInteger atomicInteger = new AtomicInteger();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try (OutputStream os = hdfsTool.createFile("/cqx/data/zhongxing/1.txt")) {
                    int cnt = 0;
                    int ai = atomicInteger.getAndIncrement();
                    while (cnt < 1000) {
                        os.write(String.format("[%s]你好1234567890\r\n", ai).getBytes(StandardCharsets.UTF_8));
                        cnt++;
                        SleepUtil.sleepMilliSecond(5);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        threadTool.addTask(runnable);
        threadTool.addTask(runnable);
        threadTool.addTask(runnable);
        threadTool.startTask();
    }

    @Test
    public void touch() throws Exception {
//        hdfsTool.lsPath("/cqx/data/*");
//        hdfsTool.touch("/cqx/data/a.complete");
//        hdfsTool.delete("/cqx/data/a.complete");
//        hdfsTool.rename("/cqx/data/a.complete", "/cqx/data/b.complete");
        hdfsTool.delete("/cqx/data/b.complete");
    }
}