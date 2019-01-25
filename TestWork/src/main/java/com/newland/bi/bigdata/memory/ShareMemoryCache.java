package com.newland.bi.bigdata.memory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * 共享内存缓存
 *
 * @author chenqixu
 */
public class ShareMemoryCache {

    private RandomAccessFile raFile;
    private FileChannel fc;
    private MappedByteBuffer mapBuf;
    private int mode;
    private FileLock fileLock;

    public ShareMemoryCache() throws Exception {

    }

    public void register() {

    }

    public void createRandomAccessFile(String filename
            , MemoryCacheMode memoryCacheMode) throws IOException {
        // 获得一个只读的随机存取文件对象
        raFile = new RandomAccessFile(filename, memoryCacheMode.getCode());
        // 获得相应的文件通道
        fc = raFile.getChannel();
        System.out.println("fc：" + fc);
        // 获得文件的独占锁，该方法不产生堵塞，立刻返回
        fileLock = fc.tryLock();
        System.out.println("fileLock：" + fileLock + " isShared：" + fileLock.isShared() + " isValid：" + fileLock.isValid());
        fileLock.release();
//        // 取得文件的实际大小，以便映像到共享内存
//        int size = (int) fc.size();
//        // 获得共享内存缓冲区
//        mapBuf = fc.map(memoryCacheMode.getMapMode(), 0, size);
//        // 获取头部消息：存取权限
//        mode = mapBuf.getInt();
    }

    public void read() {
//        byte[] buff = new byte[1024];
//        int hasRead = 0;
        String text;
        try {
            while ((text = raFile.readLine()) != null) {
                //打印读取的内容,并将字节转为字符串输入
//                System.out.println(new String(buff, 0, hasRead));
                System.out.println(new String(text.getBytes("ISO-8859-1"), "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg) {
        try {
            raFile.writeBytes(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (raFile != null) {
            try {
                raFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fc != null) {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileLock != null) {
            try {
                fileLock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean StartWrite() {
        if (mode == 0) { // 标志为0，则表示可写
            mode = 1; // 置标志为1，意味着别的应用不可写该共享内存
            mapBuf.flip();
            mapBuf.putInt(mode); // 写如共享内存的头部信息
            return true;
        } else {
            return false; // 指明已经有应用在写该共享内存，本应用不可写该共享内存
        }
    }

    public boolean StopWrite() {
        mode = 0; // 释放写权限
        mapBuf.flip();
        mapBuf.putInt(mode); // 写入共享内存头部信息
        return true;
    }

    public enum MemoryCacheMode {
        READ_ONLY("r", FileChannel.MapMode.READ_ONLY),
        READ_WRITE("rw", FileChannel.MapMode.READ_WRITE);

        private final String code;
        private final FileChannel.MapMode mapMode;

        private MemoryCacheMode(String code, FileChannel.MapMode mapMode) {
            this.code = code;
            this.mapMode = mapMode;
        }

        public String getCode() {
            return this.code;
        }

        public FileChannel.MapMode getMapMode() {
            return this.mapMode;
        }
    }
}
