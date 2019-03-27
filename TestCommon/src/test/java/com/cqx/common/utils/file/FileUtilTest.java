package com.cqx.common.utils.file;

public class FileUtilTest {

    @org.junit.Test
    public void write() throws Exception {
        // open file
        FileUtil fileUtil = new FileUtil();
        fileUtil.createFile("d:/tmp/logs/123.txt", "GBK");
        // test write
        fileUtil.write("123", 1, 2);
        // close
        fileUtil.closeWrite();
    }
}