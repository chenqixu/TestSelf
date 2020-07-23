package com.cqx.common.utils.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * BufferedReaderUtil
 *
 * @author chenqixu
 */
public class BufferedReaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(BufferedReaderUtil.class);
    private BufferedReader reader;
    private String charSet;
    private String fileName;
    private ZipInputStream zis;

    public BufferedReaderUtil(String fileName) throws IOException {
        this(new FileInputStream(fileName), fileName);
    }

    public BufferedReaderUtil(String fileName, String charSet) throws IOException {
        this(new FileInputStream(fileName), fileName, charSet);
    }

    public BufferedReaderUtil(InputStream inputStream, String fileName) throws IOException {
        this(inputStream, fileName, null);
    }

    public BufferedReaderUtil(InputStream inputStream, String fileName, String charSet)
            throws IOException {
        this.fileName = fileName;
        this.charSet = charSet;
        if (this.charSet == null || this.charSet.isEmpty()) {
            this.charSet = "UTF-8";
        }
        if (fileName.endsWith(".zip")) {
            zis = new ZipInputStream(inputStream, Charset.forName(this.charSet));
            reader = new BufferedReader(new InputStreamReader(zis, this.charSet));
            //读第一个文件
            ZipEntry zipEntry = zis.getNextEntry();
            String zipEntryName = zipEntry.getName();
            logger.info("fileName：{}，读取第一个文件：{}", fileName, zipEntryName);
        } else if (fileName.endsWith(".tar.gz")) {
            throw new UnsupportedOperationException("不支持.tar.gz格式！");
        } else if (fileName.endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream), this.charSet));
        } else {
            reader = new BufferedReader(new InputStreamReader(inputStream, this.charSet));
        }
    }

    public boolean nextFile() throws IOException {
        boolean result = false;
        if (fileName.endsWith(".zip") && zis != null) {
            //读下一个文件
            ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry != null) {
                String zipEntryName = zipEntry.getName();
                result = true;
                logger.info("fileName：{}，读取下一个文件：{}", fileName, zipEntryName);
            }
        }
        return result;
    }

    public String readLine(int cnt) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append("[fileName]").append(fileName);
        result.append("[charSet]").append(charSet);
        try {
            if (reader != null) {
                int lineCnt = 1;
                while (cnt > 0) {
                    result.append("[line").append(lineCnt).append("]").append(reader.readLine());
                    cnt--;
                    lineCnt++;
                }
            }
        } finally {
            if (reader != null) reader.close();
        }
        return result.toString();
    }

    public String readLineSimple() throws IOException {
        return readLine(2);
    }

    public String readLine() throws IOException {
        if (reader != null) {
            return reader.readLine();
        }
        return null;
    }
}
