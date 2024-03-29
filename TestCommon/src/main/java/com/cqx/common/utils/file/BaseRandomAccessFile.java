package com.cqx.common.utils.file;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * BaseRandomAccessFile
 *
 * @author chenqixu
 */
public class BaseRandomAccessFile implements Closeable {
    private int writeIndex = 0;
    private RandomAccessFile randomAccessFile;
    //内容缓存，比较消耗内存
    private String data;
    //文件名
    private String fileName;
    //文件通道，用来解决写入冲突
    private FileChannel fileChannel;
    //写入时候是否需要锁定
    private boolean isLock;

    public BaseRandomAccessFile(String filename) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(filename, MemoryCacheMode.READ_WRITE.getCode());
        this.fileChannel = this.randomAccessFile.getChannel();
        this.fileName = filename;
    }

    public BaseRandomAccessFile(String filename, boolean isGetData) throws IOException {
        this(filename);
        if (isGetData) {
            this.data = readAll();
        }
    }

    public void seek(long pos) throws IOException {
        randomAccessFile.seek(pos);
    }

    public void write(String str) throws IOException {
        write(str.getBytes());
    }

    public void write(byte[] b) throws IOException {
        write(b, b.length);
    }

    public void write(byte[] b, int len) throws IOException {
        randomAccessFile.seek(writeIndex);
        randomAccessFile.write(b, 0, len);
        writeIndex += len;
    }

    public boolean write(long pos, byte[] b) throws IOException {
        FileLock fileLock = null;
        try {
            if (isLock) {//有锁
                //对要写的块进行块锁定，锁不共享
                if (fileChannel != null) fileLock = fileChannel.tryLock(pos, b.length, false);
                if (fileLock != null) {
                    randomAccessFile.seek(pos);
                    randomAccessFile.write(b, 0, b.length);
                    return true;
                }
            } else {//没锁
                randomAccessFile.seek(pos);
                randomAccessFile.write(b, 0, b.length);
                return true;
            }
        } catch (OverlappingFileLockException e) {
            //抢不到锁抛出的异常，不做处理
//            return false;
        } finally {
            if (fileLock != null) fileLock.release();
        }
        return false;
    }

    public boolean write(long pos, String msg) throws IOException {
        return write(pos, msg.getBytes());
    }

    public String read(long pos, int len) throws IOException {
        byte[] b = new byte[len];
        randomAccessFile.seek(pos);
        randomAccessFile.read(b, 0, len);
        return new String(b);
    }

    public byte[] readByte(long pos, int len) throws IOException {
        byte[] b = new byte[len];
        randomAccessFile.seek(pos);
        randomAccessFile.read(b, 0, len);
        return b;
    }

    public String readLine() throws IOException {
        return randomAccessFile.readLine();
    }

    private String readAll() throws IOException {
        int len = (int) randomAccessFile.length();
        byte[] b = new byte[len];
        randomAccessFile.seek(0);
        randomAccessFile.read(b, 0, len);
        return new String(b);
    }

    public long getFilePointer() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    @Override
    public void close() throws IOException {
        //文件通道
        if (fileChannel != null) {
            fileChannel.close();
        }
        //映像文件
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    public int getWriteIndex() {
        return writeIndex;
    }

    public long length() throws IOException {
        return randomAccessFile.length();
    }

    public String getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }
}
