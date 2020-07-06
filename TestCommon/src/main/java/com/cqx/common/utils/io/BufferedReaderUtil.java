package com.cqx.common.utils.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * BufferedReaderUtil
 *
 * @author chenqixu
 */
public class BufferedReaderUtil {

    private BufferedReader reader;
    private String charSet;
    private String fileName;

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
            reader = new BufferedReader(new InputStreamReader(new ZipInputStream(inputStream), this.charSet));
        } else if (fileName.endsWith(".tar.gz")) {
            throw new UnsupportedOperationException("不支持.tar.gz格式！");
        } else if (fileName.endsWith(".gz")) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputStream), this.charSet));
        } else {
            reader = new BufferedReader(new InputStreamReader(inputStream, this.charSet));
        }
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

    public String readLine() throws IOException {
        return readLine(2);
    }
}
