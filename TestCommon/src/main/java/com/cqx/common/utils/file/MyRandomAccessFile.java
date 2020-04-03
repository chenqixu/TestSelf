package com.cqx.common.utils.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * MyRandomAccessFile
 *
 * @author chenqixu
 */
public class MyRandomAccessFile {
    int index = 0;
    private RandomAccessFile randomAccessFile;
    // 内容缓存，比较消耗内存
    private String data;

    public MyRandomAccessFile(String filename) throws FileNotFoundException {
        randomAccessFile = new RandomAccessFile(filename, MemoryCacheMode.READ_WRITE.getCode());
    }

    public MyRandomAccessFile(String filename, boolean isGetData) throws IOException {
        this(filename);
        if (isGetData)
            data = readAll();
    }

    public void write(String str) throws IOException {
        write(str.getBytes());
    }

    public void write(byte[] b) throws IOException {
        write(b, b.length);
    }

    public void write(byte[] b, int len) throws IOException {
        randomAccessFile.seek(index);
        randomAccessFile.write(b, 0, len);
        index += len;
    }

    public void write(long pos, byte[] b) throws IOException {
        randomAccessFile.seek(pos);
        randomAccessFile.write(b, 0, b.length);
    }

    public void write(long pos, String msg) throws IOException {
        write(pos, msg.getBytes());
    }

    public String read(long pos, int len) throws IOException {
        byte[] b = new byte[len];
        randomAccessFile.seek(pos);
        randomAccessFile.read(b, 0, len);
        return new String(b);
    }

    private String readAll() throws IOException {
        int len = (int) randomAccessFile.length();
        byte[] b = new byte[len];
        randomAccessFile.seek(0);
        randomAccessFile.read(b, 0, len);
        return new String(b);
    }

    public void close() throws IOException {
        if (randomAccessFile != null) {
            // 关闭文件
            randomAccessFile.close();
        }
    }

    public int getIndex() {
        return index;
    }

    public String getData() {
        return data;
    }
}
