package com.cqx.common.utils.file;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class FileUtilTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(FileUtilTest.class);
    private FileUtil fileUtil;

    @Before
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

    @Test
    public void rename() {
        String source = "d:\\tmp\\data\\dpi\\dpi_ltedata\\errdata\\20190822.txt";
        String dist = "d:\\tmp\\data\\dpi\\dpi_ltedata\\errdata\\xxxxx.sm";
        FileUtil.rename(source, dist);
    }

    @Test
    public void readerByThread() throws Exception {
        try {
            fileUtil.setReader("d:\\tmp\\data\\dpi\\dpi_ltedata\\LTE_S1UHTTP_008388787002_20190411080100.txt");
            fileUtil.read(new IFileRead() {
                @Override
                public void run(String content) throws IOException {
                    logger.info(content);
                }

                @Override
                public void tearDown() throws IOException {

                }
            }, 3);
        } finally {
            fileUtil.closeRead();
        }
    }
}