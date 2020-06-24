package com.cqx.common.utils.file;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtilTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(FileUtilTest.class);
    private FileUtil fileUtil;

    @Before
    public void setUp() throws Exception {
        fileUtil = new FileUtil();
    }

    @org.junit.Test
    public void write() throws Exception {
        String filename = "d:/tmp/logs/123.txt";
        // open file
//        fileUtil.createFile(filename, "GBK");
//        // test write
//        fileUtil.write("移动");
//        // close
//        fileUtil.closeWrite();
        for (String str : fileUtil.read(filename, "UTF-8")) {
            logger.info("{}", str.length());
        }
        fileUtil.closeRead();
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

    @Test
    public void createFile() throws FileNotFoundException, UnsupportedEncodingException {
        //多个同时写
        ThreadTool threadTool = new ThreadTool();
        final AtomicInteger atomicInteger = new AtomicInteger();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FileUtil fileUtil = new FileUtil();
                String filename = "d:/tmp/logs/test.log";
                int cnt = 0;
                int ai = atomicInteger.getAndIncrement();
                try {
                    // open file
                    fileUtil.createFile(filename, "UTF-8", true);
                    while (cnt < 1000) {
                        fileUtil.write(String.format("[%s]你好1234567890\r\n", ai));
                        cnt++;
                        SleepUtil.sleepMilliSecond(5);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    // close
                    fileUtil.closeWrite();
                }
            }
        };
        threadTool.addTask(runnable);
        threadTool.addTask(runnable);
        threadTool.addTask(runnable);
        threadTool.startTask();
    }
}