package com.cqx.common.utils.io;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class IOUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(IOUtilTest.class);
    private IOUtil ioUtil;
    private FileUtil fileUtil;

    @Before
    public void setUp() throws Exception {
        String fileName = "d:\\tmp\\data\\syncos\\3.txt";
        fileName = "d:\\tmp\\data\\dpi\\dpi_ltedata\\streaminput\\LTE_S1UHTTP_010531112002_20190507000000.txt";
        fileName = "d:\\tmp\\data\\syncos\\1.txt";
        ioUtil = new IOUtil(fileName);
//        ioUtil.newWrite();
        ioUtil.newRead();
        fileUtil = new FileUtil();
        fileUtil.setReader(fileName);
    }

    @After
    public void tearDown() throws Exception {
        if (ioUtil != null) {
//            ioUtil.closeWrite();
            ioUtil.closeRed();
        }
        if (fileUtil != null) {
            fileUtil.closeRead();
        }
    }

    @Test
    public void write() throws IOException {
//        ioUtil.write("1你2好不1\r\n是45I、。\rab\ncde€-+/[]{}~!@#，".getBytes());
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        String str;
        int cnt = 0;
        timeCostUtil.start();
        while ((str = ioUtil.readLine(2048)) != null) {
            cnt++;
//            if (cnt >= 16800 && cnt <= 16805) {
//                logger.info("【cnt】{}，【str】{}，【Bytes】 {}", cnt, str, str.getBytes());
//            }
//            if (cnt > 16805) break;
//            if (cnt % 100010 == 0) {
//                logger.info("cnt print：{}", cnt);
//                break;
//            }
        }
        timeCostUtil.stop();
        logger.info("【end】cnt：{}，cost：{}", cnt, timeCostUtil.getCost());
        logger.info("========我是分割线==========");
        timeCostUtil.start();
        FileCount fileCount = new FileCount() {
            @Override
            public void run(String content) throws IOException {
                count("read");
            }
        };
        fileUtil.read(fileCount);
        timeCostUtil.stop();
        logger.info("cnt：{}，cost：{}", fileCount.getCount("read"), timeCostUtil.getCost());
    }

    @Test
    public void read() throws IOException {
        byte[] first = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] second = {10, 11, 12, 13};
        byte[] news = Arrays.copyOfRange(first, 3, first.length);
        logger.info("{}", news);
        news = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, news, first.length, second.length);
        logger.info("{}", news);

//        ioUtil.write("1你2好不1\r\n是45I、。\rab\ncde€-+/[]{}~!@#，".getBytes());
        String str;
        while ((str = ioUtil.readLine(4)) != null) {
            logger.info("{}", str);
        }
    }
}