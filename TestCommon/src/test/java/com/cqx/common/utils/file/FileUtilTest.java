package com.cqx.common.utils.file;

import org.junit.Test;

public class FileUtilTest {

    FileUtil fileUtil;

    public void setUp() throws Exception {
        fileUtil = new FileUtil();
    }

    @org.junit.Test
    public void write() throws Exception {
        // open file
        fileUtil.createFile("d:/tmp/logs/123.txt", "GBK");
        // test write
        fileUtil.write("123", 1, 2);
        // close
        fileUtil.closeWrite();
    }

    @Test
    public void createSymbolicLink() throws Exception {
        String sourceFilePath = "d:\\tmp\\a\\position.xml";
        String linkPath = "d:\\tmp\\a\\link_position.xml";
        FileUtil.createSymbolicLink(sourceFilePath, linkPath);
        FileUtil.del(linkPath);
    }
}