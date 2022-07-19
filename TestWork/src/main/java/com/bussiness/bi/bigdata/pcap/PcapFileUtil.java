package com.bussiness.bi.bigdata.pcap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * PcapFileUtil
 *
 * @author chenqixu
 */
public class PcapFileUtil {

    public static void main(String[] args) throws Exception {
        PcapFileUtil ft = new PcapFileUtil();
        String type = null;
        String readFile = null;
        String writeFile = null;
        if (args.length >= 3) {
            type = args[0];
            readFile = args[1];
            writeFile = args[2];
        }
        // 合并使用
        String read2File = null;
        if (args.length >= 4) {
            read2File = args[2];
            writeFile = args[3];
        }
        if (type == null) {
            System.err.println("type is null!");
            System.exit(-1);
        }
        switch (type) {
            case "rw":
                ft.readAndWrite(readFile, writeFile);
                break;
            case "rp":
                ft.readAndReplace(readFile, writeFile);
                break;
            case "rpl":
                ft.readAndReplaceList(readFile, writeFile);
                break;
            case "xn":
                ft.writeTest(readFile, writeFile);
                break;
            case "merge":
                ft.merge(readFile, read2File, writeFile);
                break;
            default:
                System.err.println("[" + type + "] type is not Support!");
                break;
        }
    }

    public void merge(List<String> readFileList, String writeFile) throws Exception {
        AtomicBoolean isFirst = new AtomicBoolean(true);
        int buffSize = 4096;
        byte[] buff = new byte[buffSize];
        int bytesRead;
        try (FileOutputStream newFos = new FileOutputStream(new File(writeFile))) {
            for (String readFile : readFileList) {
                try (FileInputStream fis = new FileInputStream(new File(readFile))) {
                    if (isFirst.getAndSet(false)) {
                        // 第一个文件需要保留开头的24个字节
                    } else {
                        // 跳过开头的24个字节
                        byte[] header = new byte[24];
                        int skipHeader = fis.read(header);
                        if (skipHeader != 24) {
                            throw new NullPointerException(String.format("%s 异常文件, header不足24字节!", readFile));
                        }
                    }
                    while ((bytesRead = fis.read(buff)) != -1) {
                        newFos.write(buff, 0, bytesRead);
                    }
                }
            }
        }
    }

    public void merge(String readFile, String read2File, String writeFile) throws Exception {
        List<String> readFileList = new ArrayList<>();
        readFileList.add(readFile);
        readFileList.add(read2File);
        merge(readFileList, writeFile);
    }

    public void readAndWrite(String readFile, String writeFile) throws Exception {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream(new File(readFile));
             FileOutputStream newFos = new FileOutputStream(new File(writeFile))
        ) {
            int buffSize = 4096;
            int fileSize = fis.available();
            byte[] buff = new byte[buffSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                newFos.write(buff, 0, bytesRead);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(String.format("buff size：%s，file size：%s，read and write：%s"
                    , buffSize
                    , fileSize
                    , endTime - startTime));
        }
    }

    public void readAndReplace(String readFile, String writeFile) throws Exception {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream(new File(readFile));
             FileOutputStream newFos = new FileOutputStream(new File(writeFile))
        ) {
            int buffSize = 4096;
            int fileSize = fis.available();
            byte[] buff = new byte[buffSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                String s_iso_8859_1 = new String(buff, 0, bytesRead, StandardCharsets.ISO_8859_1);
                s_iso_8859_1 = s_iso_8859_1.replaceAll("\r", "newlandxdrtagr");
                s_iso_8859_1 = s_iso_8859_1.replaceAll("\n", "newlandxdrtagn");
                newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
            }
            long endTime = System.currentTimeMillis();
            System.out.println(String.format("buff size：%s，file size：%s，read and replace：%s"
                    , buffSize
                    , fileSize
                    , endTime - startTime));
        }
    }

    public void readAndReplaceList(String readFile, String writeFile) throws Exception {
        long startTime = System.currentTimeMillis();
        File fs = new File(readFile);
        int fileSize = 0;
        int buffSize = 4096;
        int fileCount = 0;
        byte[] buff = new byte[buffSize];
        File[] files = fs.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);
                 FileOutputStream newFos = new FileOutputStream(new File(writeFile), true)
            ) {
                fileCount++;
                fileSize += fis.available();
                int bytesRead;
                while ((bytesRead = fis.read(buff)) != -1) {
                    String s_iso_8859_1 = new String(buff, 0, bytesRead, StandardCharsets.ISO_8859_1);
                    s_iso_8859_1 = s_iso_8859_1.replaceAll("\r", "newlandxdrtagr");
                    s_iso_8859_1 = s_iso_8859_1.replaceAll("\n", "newlandxdrtagn");
                    newFos.write(s_iso_8859_1.getBytes(StandardCharsets.ISO_8859_1));
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("buff size：%s，file size：%s，fileCount：%s，read and replace：%s"
                , buffSize
                , fileSize
                , fileCount
                , endTime - startTime));
    }

    public void writeTest(String outputFile, String maxs) throws Exception {
        int max = Integer.valueOf(maxs);
        byte[] buf = new byte[4096];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) 1;
        }
        long cnt = 0L;
        long size = 0L;
        long startFirst = System.currentTimeMillis();
        long start = startFirst;
        // 1 MB = 1024 KB = 1048576 BYTE
        long splitSize = 104857600;
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            while (cnt < max) {
                fos.write(buf, 0, 4096);
                size += 4096;
                if (size % splitSize == 0) {
                    cnt++;
                    long end = System.currentTimeMillis();
                    System.out.println(String.format("当前生成 %s MB耗时 %s ms，总耗时 %s ms，速度 %s MB/S"
                            , splitSize / 1024 / 1024, end - start, end - startFirst, size * 1000 / 1024 / 1024 / (end - startFirst)));
                    start = end;
                }
            }
        }
    }
}
