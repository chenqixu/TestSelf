package com.cqx.common.utils.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class FileCountTest {

    private FileCount fileCount;
    private FileUtil fileUtil;

    @Before
    public void setUp() throws Exception {
        fileUtil = new FileUtil();
        fileUtil.setReader("d:\\tmp\\data\\dpi\\dpi_ltedata\\LTE_S1UHTTP_008388787002_20190411080100.txt");
        fileCount = new FileCount() {
            @Override
            public void run(String content) throws IOException {
                count("read");
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        if (fileUtil != null) fileUtil.closeRead();
    }

    @Test
    public void getCount() throws IOException {
        fileUtil.read(fileCount);
        System.out.println(fileCount.getCount("read"));
    }
}