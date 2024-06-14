package com.cqx.common.utils.file;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(FileUtilTest.class);
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
                public void run(byte[] content) throws IOException {
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

    @Test
    public void readOuYuanFile() throws Exception {
//        try {
//            fileUtil.setReader("d:\\tmp\\data\\hblog\\zrr.dat", "x-mswin-936");
//            fileUtil.read(new FileResult() {
//                int cnt = 0;
//
//                @Override
//                public void run(String content) throws IOException {
//                    cnt++;
//                    if (cnt < 10) logger.info("{}：{}", "zrr.dat", content);
//                }
//            });
//        } finally {
//            fileUtil.closeRead();
//        }

        try {
            fileUtil.setReader("D:\\tmp\\data\\P9963520200618000001.AVL", "GBK");
            fileUtil.read(new FileResult() {
                int cnt = 0;

                @Override
                public void run(String content) throws IOException {
                    cnt++;
                    if (cnt < 10) logger.info("{}，{}", content.length(), content);
                }
            });
        } finally {
            fileUtil.closeRead();
        }

        try {
            fileUtil.setReader("D:\\tmp\\data\\P9963520200618000002.AVL", "UTF-8");
            fileUtil.read(new FileResult() {
                int cnt = 0;

                @Override
                public void run(String content) throws IOException {
                    cnt++;
                    if (cnt < 10) logger.info("{}，{}", content.length(), content);
                }
            });
        } finally {
            fileUtil.closeRead();
        }

        try {
            fileUtil.setReader("D:\\tmp\\data\\P9963520200618000003.AVL", "GBK");
            fileUtil.read(new FileResult() {
                int cnt = 0;

                @Override
                public void run(String content) throws IOException {
                    cnt++;
                    if (cnt < 10) logger.info("{}，{}", content.length(), content);
                }
            });
        } finally {
            fileUtil.closeRead();
        }

//        fileUtil.createFile("d:\\tmp\\data\\hblog\\zrr.dat1", "GB18030");
//        try {
//            fileUtil.write("€12345你好" + "\r\n");
//        } finally {
//            fileUtil.closeWrite();
//        }
//        try {
//            fileUtil.setReader("d:\\tmp\\data\\hblog\\zrr.dat1", "GB18030");
//            fileUtil.read(new FileResult() {
//                int cnt = 0;
//
//                @Override
//                public void run(String content) throws IOException {
//                    cnt++;
//                    if (cnt < 10) logger.info("{}：{}", "zrr.dat1", content);
//                }
//            });
//        } finally {
//            fileUtil.closeRead();
//        }
    }

    @Test
    public void copyFile() throws IOException {
        FileUtil.copyFile("d:\\tmp\\data\\dpi\\dpi_s1mme\\streambackup\\LTE_S1MME_028470736002_20190603110100.txt",
                "d:\\tmp\\data\\dpi\\dpi_s1mme\\streaminput\\LTE_S1MME_028470736002_20190603110100.txt");
    }

    @Test
    public void printTest() {
        logger.info("File.separator：{}", File.separator);
    }

    @Test
    public void readCapFile() throws Exception {
        try (FileInputStream fis = new FileInputStream(new File("d:\\tmp\\data\\xdr\\VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap"));
             FileOutputStream fos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\new.cap"));
        ) {
            byte[] buff = new byte[fis.available()];
            int ret = fis.read(buff);
            String s_iso_8859_1 = new String(buff, StandardCharsets.ISO_8859_1);
            int index_r = s_iso_8859_1.indexOf("\r");
            int index_n = s_iso_8859_1.indexOf("\n");
            logger.info("ret: {}, r: {}, n: {}", ret, index_r, index_n);
            s_iso_8859_1 = s_iso_8859_1.replaceAll("\r", "tagr");
            s_iso_8859_1 = s_iso_8859_1.replaceAll("\n", "tagn");
            fos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
        }
        try (FileInputStream newFis = new FileInputStream(new File("d:\\tmp\\data\\xdr\\new.cap"));
             FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\newFos.cap"))) {
            byte[] buff = new byte[newFis.available()];
            int ret = newFis.read(buff);
            String s_iso_8859_1 = new String(buff, StandardCharsets.ISO_8859_1);
            s_iso_8859_1 = s_iso_8859_1.replaceAll("tagr", "\r");
            s_iso_8859_1 = s_iso_8859_1.replaceAll("tagn", "\n");
            newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    @Test
    public void readAndWrite() throws Exception {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream(new File("d:\\tmp\\data\\xdr\\VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap"));
             FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\newFos.cap"))
        ) {
            int buffSize = 4096;
            int fileSize = fis.available();
            byte[] buff = new byte[buffSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                newFos.write(buff, 0, bytesRead);
            }
            long endTime = System.currentTimeMillis();
            logger.info("buff size：{}，file size：{}，read and write：{}"
                    , buffSize
                    , fileSize
                    , endTime - startTime);
        }
    }

    @Test
    public void readAndReplace() throws Exception {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream("d:\\tmp\\data\\xdr\\newFos.cap");
             FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\newFos1.cap"))
        ) {
            int buffSize = 4096;
            int fileSize = fis.available();
            byte[] buff = new byte[buffSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                String s_iso_8859_1 = new String(buff, 0, bytesRead, StandardCharsets.ISO_8859_1);
                s_iso_8859_1 = s_iso_8859_1.replaceAll("\r", "tagr");
                s_iso_8859_1 = s_iso_8859_1.replaceAll("\n", "tagn");
                newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
            }
            long endTime = System.currentTimeMillis();
            logger.info("buff size：{}，file size：{}，read and replace：{}"
                    , buffSize
                    , fileSize
                    , endTime - startTime);
        }
    }

    @Test
    public void readAndReplaceList() throws Exception {
        long startTime = System.currentTimeMillis();
        File fs = new File("d:\\tmp\\data\\xdr\\");
        int buffSize = 4096;
        int fileSize = 0;
        int fileCount = 0;
        byte[] buff = new byte[buffSize];
        File[] files = fs.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);
                 FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\newFos1.cap"), true)
            ) {
                fileCount++;
                fileSize += fis.available();
                while (fis.read(buff) != -1) {
                    String s_iso_8859_1 = new String(buff, StandardCharsets.ISO_8859_1);
                    s_iso_8859_1 = s_iso_8859_1.replaceAll("\r", "tagr");
                    s_iso_8859_1 = s_iso_8859_1.replaceAll("\n", "tagn");
                    newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
                }
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("buff size：{}，file size：{}，fileCount：{}，read and replace：{}"
                , buffSize
                , fileSize
                , fileCount
                , endTime - startTime);
    }

    @Test
    public void restore() throws Exception {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream("d:\\tmp\\data\\xdr\\newFos1.cap");
             FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\restore.cap"))
        ) {
            int buffSize = 4096;
            int fileSize = fis.available();
            byte[] buff = new byte[buffSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                String s_iso_8859_1 = new String(buff, 0, bytesRead, StandardCharsets.ISO_8859_1);
                s_iso_8859_1 = s_iso_8859_1.replaceAll("tagr", "\r");
                s_iso_8859_1 = s_iso_8859_1.replaceAll("tagn", "\n");
                newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
            }
            long endTime = System.currentTimeMillis();
            logger.info("buff size：{}，file size：{}，read and replace：{}"
                    , buffSize
                    , fileSize
                    , endTime - startTime);
        }
    }

    @Test
    public void readDataRaw() throws Exception {
        try (FileInputStream fis = new FileInputStream(new File("d:\\Work\\ETL\\上网日志查询2022\\data\\移动3.0输出单据样例-华为4G\\rds\\2022\\01\\04\\07_0\\R093500.idxX"))
        ) {
            int len = 2;
            byte[] buf = new byte[len];
            int ret = fis.read(buf, 0, len);
            logger.info("{}", new String(buf));
        }
    }

    @Test
    public void writeTest() throws Exception {
        // 按字节写，性能测试
        String outputFile = "d:\\tmp\\data\\xdr\\xntest.data";
        byte[] buf = new byte[4096];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0x00;
        }
        long cnt = 0L;
        long size = 0L;
        long startFirst = System.currentTimeMillis();
        long start = startFirst;
        // 1 MB = 1024 KB = 1048576 BYTE
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            while (cnt < 10) {
                fos.write(buf, 0, 4096);
                size += 4096;
                if (size % 1048576 == 0) {
                    cnt++;
                    long end = System.currentTimeMillis();
                    System.out.println(String.format("当前生成1MB耗时 %s ms，总耗时 %s ms，速度 %s MB/S"
                            , end - start, end - startFirst, size * 1000 / 1024 / 1024 / (end - startFirst)));
                    start = end;
                }
            }
        }
    }

    @Test
    public void readCapFileToSer() throws Exception {
        RawDataFileWriter rawDataFileWriter = new RawDataFileWriter("d:\\tmp\\data\\xdr\\VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap");
        try (FileOutputStream fos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\new.cap"))) {
            fos.write(rawDataFileWriter.getSrcBytes());
        }
        RawDataFileReader rawDataFileReader = new RawDataFileReader();
        try (FileInputStream newFis = new FileInputStream(new File("d:\\tmp\\data\\xdr\\new.cap"));
             FileOutputStream newFos = new FileOutputStream(new File("d:\\tmp\\data\\xdr\\newFos.cap"))) {
            byte[] buff = new byte[newFis.available()];
            int ret = newFis.read(buff);
            newFos.write(rawDataFileReader.readToBytes(buff));
        }
    }

    @Test
    public void bytebuffTest() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        byteBuffer.put((byte) 3);
        int position = byteBuffer.position();
        logger.info("pos: {}, remaining: {}", position, byteBuffer.remaining());
        byte[] buf = new byte[position];
        byteBuffer.flip();
        byteBuffer.get(buf, 0, position);
        for (byte b : buf) {
            logger.info("{}", b);
        }
        byteBuffer.clear();
        byteBuffer.put((byte) 4);
        byteBuffer.put((byte) 5);
        position = byteBuffer.position();
        logger.info("pos: {}, remaining: {}", position, byteBuffer.remaining());
    }

    @Test
    public void LVReadAndWrite() throws IOException {
        String path = "I:\\Document\\Workspaces\\Git\\FujianBI\\edc-bigdata-comm\\edc-bigdata-comm-sdtp\\edc-bigdata-comm-sdtpServer\\target\\test-classes\\";
        path = "d:\\tmp\\data\\sdtp\\byte_noparser\\";
        for (String _fileName : FileUtil.listFileEndWith(path, ".dat")) {
            fileUtil.setLVReader(FileUtil.endWith(path) + _fileName);
            FileCount fc = new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    count("CNT");
                    logger.info("read: {}", content);
                }
            };
            fileUtil.read(fc);
            fileUtil.closeRead();
            logger.info("read: {}, CNT: {}", fileUtil.getReaderName(), fc.getCount("CNT"));
        }
    }

    @Test
    public void FileParallelReadTest() throws Exception {
        String path = "d:\\tmp\\data\\sdtp\\text\\202303251645_LTE_591_0591_FJ163_S1-MME_20230325165512_0000.txt";
        AtomicInteger parallelNum = new AtomicInteger(0);
        AtomicInteger consumerNum = new AtomicInteger(0);
        FileParallelRead fileParallelRead = new FileParallelRead(3) {
            @Override
            public byte[] parallelDeal(String content) throws Exception {
                int p = parallelNum.incrementAndGet();
                if (p % 10000 == 0) {
                    logger.info("parallelNum={}", p);
                    throw new Exception("test");
                }
                return content.getBytes();
            }

            @Override
            public void consumer(byte[] bytes) throws Exception {
                int c = consumerNum.incrementAndGet();
                if (c % 10000 == 0) {
                    logger.info("consumerNum={}", c);
                }
            }
        };
        try {
            fileUtil.setReader(path);
            fileUtil.read(fileParallelRead);
        } finally {
            fileUtil.closeRead();
        }
    }

    @Test
    public void ouyuanTest() throws Exception {
        try {
            fileUtil.createFile("d:\\tmp\\data\\tag\\a_11100_2024060304_IOP-91072_00_001.txt", "UTF-8");
//            fileUtil.write("11100");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("福建爱优腾活跃用户客户群");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("0102530510791700823623687733257");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("福建爱优腾活跃用户");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("M");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("ouyangfan");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("588582291");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("20240603040655");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("20250101120100");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("20240603040655");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("20250101120100");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("福建爱优腾活跃用户");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("爱优腾活跃用户");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
//            fileUtil.write("和 (符合全部条件)");
//            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
            fileUtil.write("\u20AC".getBytes(StandardCharsets.UTF_8));
        } finally {
            fileUtil.closeWrite();
        }
        try (FileInputStream fis = new FileInputStream("d:\\tmp\\data\\tag\\a_11100_2024060304_IOP-91072_00_001.dat.txt")) {
            int len = fis.available();
            byte[] bytes = new byte[len];
            int r = fis.read(bytes);
            byte[] bs = "\u20AC".getBytes(StandardCharsets.UTF_8);
            byte b1 = (byte) 0x80;
            // -30 -126 -84
            // -128 13 10
            for (String str : fileUtil.read("d:\\tmp\\data\\tag\\a_11100_2024060304_IOP-91072_00_001.dat", "ms936")) {
                logger.info("{}", str);
            }
        } finally {
            fileUtil.closeRead();
        }
    }
}