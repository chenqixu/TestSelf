package com.cqx.util;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HdfsToolFactoryTest {

    private static Logger logger = LoggerFactory.getLogger(HdfsToolFactoryTest.class);
    private HdfsToolFactory hdfsToolFactory;
    private String filepath = "/tmp/cqx/1.txt";
    private String filepathtmp = "/tmp/cqx/1.txt.tmp";

    @Before
    public void setUp() throws IOException {
        HdfsTool.setHadoopUser("udapdev");
        hdfsToolFactory = HdfsToolFactory.builder();
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
        hdfsToolFactory.close();
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
        hdfsToolFactory.close();
    }

    @Test
    public void copyBytesSkipEnd() throws Exception {
        hdfsToolFactory.copyBytesSkipEnd(filepath, filepathtmp, 21);
        hdfsToolFactory.close();
    }

    @Test
    public void delete() throws IOException {
        boolean delete = hdfsToolFactory.delete(filepathtmp);
        logger.info("delete：{}", delete);
        boolean isexist = hdfsToolFactory.isExist(filepathtmp);
        logger.info("isexist：{}", isexist);
        hdfsToolFactory.close();
    }
}