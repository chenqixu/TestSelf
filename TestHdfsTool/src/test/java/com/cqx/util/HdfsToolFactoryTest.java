package com.cqx.util;

import com.cqx.common.utils.system.SleepUtil;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.FutureTask;

public class HdfsToolFactoryTest {

    private static Logger logger = LoggerFactory.getLogger(HdfsToolFactoryTest.class);
    private HdfsToolFactory hdfsToolFactory;
    private String filepath = "/tmp/cqx/1.txt";
    private String filepathtmp = "/tmp/cqx/1.txt.tmp";

    @Before
    public void setUp() throws IOException {
//        HdfsTool.setHadoopUser("udapdev");
//        hdfsToolFactory = HdfsToolFactory.builder();
        HdfsTool.setHadoopUser("edc_base");
        hdfsToolFactory = HdfsToolFactory.builder("D:\\tmp\\etc\\hadoop\\conf75\\");
    }

    @Test
    public void write() throws Exception {
        FSDataOutputStream fsDataOutputStream = hdfsToolFactory.appendFile(filepath);
        hdfsToolFactory.startWrite(fsDataOutputStream, filepath);
        long filesize = hdfsToolFactory.getFileSize(filepath);
        logger.info("filesize：{}", filesize);
        hdfsToolFactory.write("123\n".getBytes());
        hdfsToolFactory.closeStream(fsDataOutputStream);
        filesize = hdfsToolFactory.getFileSize(filepath);
        logger.info("filesize：{}", filesize);
    }

    @Test
    public void write_check() throws Exception {
        FSDataOutputStream fsDataOutputStream = hdfsToolFactory.appendFile(filepath);
        hdfsToolFactory.startWrite(fsDataOutputStream, filepath);
        long filesize = hdfsToolFactory.getFileSize(filepath);
        logger.info("filesize：{}", filesize);
        hdfsToolFactory.write("123\n".getBytes(), 4);
        hdfsToolFactory.closeStream(fsDataOutputStream);
        filesize = hdfsToolFactory.getFileSize(filepath);
        logger.info("filesize：{}", filesize);
    }

    @Test
    public void copyBytesSkipEnd() throws Exception {
        hdfsToolFactory.copyBytesSkipEnd(filepath, filepathtmp, 21);
    }

    @Test
    public void delete() throws Exception {
        boolean delete = hdfsToolFactory.delete(filepathtmp);
        logger.info("delete：{}", delete);
        boolean isexist = hdfsToolFactory.isExist(filepathtmp);
        logger.info("isexist：{}", isexist);
    }

    @Test
    public void getFileInfo() throws Exception {
        logger.info("countFileLine：{}", hdfsToolFactory.countFileLine("/tmp/xi_test_4g.csv"));
    }

    @Test
    public void pathTest() throws Exception {
        Path path = new Path("/tmp/cqx/lte_http_20190412_data1_000111");
        logger.info("name：{}，parent：{}，toString：{}", path.getName(), path.getParent(), path.toString());
    }

    @Test
    public void copyFromLocalFile() throws Exception {
//        String path = "file:///d:/tmp/data/dpi/dpi_gndata/";
////        String src = path + "Uar_68_04_http_dnssession_60_20190409_152100_20190409_152159.csv";
//        String src = path;
//        String[] srcs = {
//                path + "Uar_103_01_rtsp_session_60_20190411_081400_20190411_081459.csv",
//                path + "Uar_103_04_rtsp_session_60_20190411_085600_20190411_085659.csv",
//                path + "Uar_103_07_rtsp_session_60_20190411_082700_20190411_082759.csv"
//        };
//        String hdfsDst = "/tmp/test/dpi/a.txt";
////        String localDst = "file:///d:/tmp/data/dpi/a.txt";
//        String localDst = "file:///d:/tmp/data/tasklog/a.log";
//        logger.info("start copy");
//        hdfsToolFactory.copyFromLocalFile(localDst, hdfsDst);
////        hdfsToolFactory.copyFromLocalFile(srcs, hdfsDst);
////        hdfsToolFactory.copyFromLocalFileToLocal(srcs, localDst, hdfsDst);
//        logger.info("end copy");

//        Thread t = new ThreadTools(hdfsToolFactory);
//        t.start();
//        ThreadTools.delayTask(t, 50);
//        t.join();

        FutureTools futureTools = new FutureTools(hdfsToolFactory);
        FutureTask futureTask = futureTools.submit();
        ThreadTools.cancelTask(futureTask, 50);
        try {
            futureTask.get();
        } catch (CancellationException e) {
            logger.error("任务被取消了");
        }
        logger.info("主线程休眠开始");
        SleepUtil.sleepMilliSecond(5000);
        logger.info("主线程休眠完成");
    }

    @Test
    public void mergeFile() throws Exception {
        String path = "d:/tmp/data/dpi/dpi_gndata/";
        String[] srcs = {
                path + "Uar_103_01_rtsp_session_60_20190411_081400_20190411_081459.csv",
                path + "Uar_103_04_rtsp_session_60_20190411_085600_20190411_085659.csv",
                path + "Uar_103_07_rtsp_session_60_20190411_082700_20190411_082759.csv"
        };
        List<String> list = new ArrayList<>(Arrays.asList(srcs));
        String localDst = "d:/tmp/data/dpi/a.txt";
        hdfsToolFactory.mergeFile(list, localDst);
    }

    @Test
    public void mergeFileByPath() throws Exception {
        String srcPath = "d:/tmp/data/dpi/dpi_gndata/";
        String dsc = "d:/tmp/data/dpi/a.txt";
        hdfsToolFactory.mergeFileByPath(srcPath, dsc);
    }

    @After
    public void setDown() {
        hdfsToolFactory.close();
    }
}