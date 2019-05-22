package com.newland.bi.bigdata.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLocalBackTest {

    private static Logger logger = LoggerFactory.getLogger(FileLocalBackTest.class);
    private FileLocalBack fileLocalBack;
    private String fileName;
    private String localBackPath;

    @Before
    public void setUp() throws Exception {
        fileName = "test.txt";
        localBackPath = "d:\\tmp\\data\\dpi\\localback\\";
        fileLocalBack = new FileLocalBack(fileName, localBackPath);
    }

    @Test
    public void getSize() throws Exception {
        fileLocalBack.start();
        fileLocalBack.write("123");
        fileLocalBack.write("456");
        fileLocalBack.write("789");
        logger.info("size：{}", fileLocalBack.getSize());
    }

    @Test
    public void close() throws Exception {
        fileLocalBack.start();
        fileLocalBack.write("123");
        fileLocalBack.write("456");
        fileLocalBack.write("789");
        fileLocalBack.close();
    }

    @After
    public void tearDown() throws Exception {
        fileLocalBack.close();
        logger.info("size：{}", fileLocalBack.getSize());
    }
}