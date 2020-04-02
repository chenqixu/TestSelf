package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

/**
 * 共享映像文件
 *
 * @author chenqixu
 */
public class ShareCacheFile {
    private static final MyLogger logger = MyLoggerFactory.getLogger(ShareCacheFile.class);
    private int flen; //开辟共享内存大小
    private int fsize = 0; //文件的实际大小
    private String shareFileName; //共享内存文件名
    private String sharePath; //共享内存路径
    private MappedByteBuffer mapBuf = null; //定义共享内存缓冲区
    private FileChannel fc = null; //定义相应的文件通道
    private FileLock fl = null;  //定义文件区域锁定的标记。
    private Properties p = null;
    private RandomAccessFile RAFile = null; //定义一个随机存取文件对象

    /**
     * @param path     共享内存文件路径
     * @param fileName 共享内存文件名
     * @param size     共享内存文件大小，单位MB
     */
    public ShareCacheFile(String path, String fileName, int size) {
        if (path.length() != 0) {
            FileUtil.CreateDir(path);
            this.sharePath = path + File.separator;
        } else {
            this.sharePath = path;
        }
        this.shareFileName = fileName;
        this.flen = size * 1024 * 1024;//单位MB

        try {
            // 获得一个只读的随机存取文件对象   "rw" 打开以便读取和写入。如果该文件尚不存在，则尝试创建该文件。
            RAFile = new RandomAccessFile(this.sharePath + this.shareFileName + ".sm", "rw");
            //获取相应的文件通道
            fc = RAFile.getChannel();
            //获取实际文件的大小
            fsize = (int) fc.size();
            if (fsize < flen) {
                byte bb[] = new byte[flen - fsize];
                //创建字节缓冲区
                ByteBuffer bf = ByteBuffer.wrap(bb);
                bf.clear();
                //设置此通道的文件位置。
                fc.position(fsize);
                //将字节序列从给定的缓冲区写入此通道。
                fc.write(bf);
                fc.force(false);

                fsize = flen;
            }
            //将此通道的文件区域直接映射到内存中。
            mapBuf = fc.map(FileChannel.MapMode.READ_WRITE, 0, fsize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param position 锁定区域开始的位置；必须为非负数
     * @param len      锁定区域的大小；必须为非负数
     * @param buff     写入的数据
     * @return
     */
    public synchronized int write(int position, int len, byte[] buff) throws IOException {
        if (position >= fsize || position + len >= fsize) {
            return 0;
        }
        //定义文件区域锁定的标记。
        FileLock fl = null;
        try {
            //获取此通道的文件给定区域上的锁定。
            fl = fc.lock(position, len, false);
            if (fl != null) {
                //位置移动
                mapBuf.position(position);
                ByteBuffer bf1 = ByteBuffer.wrap(buff);
                //写入数据
                mapBuf.put(bf1);
            }
        } catch (Exception e) {
            if (fl != null) {
                try {
                    fl.release();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
            return 0;
        } finally {
            //释放此锁定。
            if (fl != null) fl.release();
        }

        return len;
    }

    /**
     * @param position 锁定区域开始的位置；必须为非负数
     * @param len      锁定区域的大小；必须为非负数
     * @param buff     要取的数据
     * @return
     */
    public synchronized int read(int position, int len, byte[] buff) throws IOException {
        if (position >= fsize) {
            return 0;
        }
        //定义文件区域锁定的标记。
        FileLock fl = null;
        try {
            //获取此通道的文件给定区域上的锁定。
            fl = fc.lock(position, len, false);
            if (fl != null) {
                //位置移动
                mapBuf.position(position);
                //判断最大能读取的长度
                if (mapBuf.remaining() < len) {
                    //设置最大能读取的长度
                    len = mapBuf.remaining();
                }
                //读取长度大于0
                if (len > 0) {
                    //读取数据到缓存byte[]
                    mapBuf.get(buff, 0, len);
                }
            }
        } catch (Exception e) {
            if (fl != null) {
                try {
                    fl.release();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
            return 0;
        } finally {
            //释放此锁定。
            if (fl != null) fl.release();
        }
        return len;
    }

    /**
     * 完成，关闭相关操作
     */
    protected void finalize() throws Throwable {
        closeSMFile();
    }

    /**
     * 关闭共享内存操作
     */
    public synchronized void closeSMFile() {
        if (fc != null) {
            try {
                fc.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            fc = null;
        }

        if (RAFile != null) {
            try {
                RAFile.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            RAFile = null;
        }
        mapBuf = null;
    }

    /**
     * 检查退出
     *
     * @return true-成功，false-失败
     */
    public synchronized boolean checkToExit() throws IOException {
        byte bb[] = new byte[1];
        if (read(1, 1, bb) > 0) {
            if (bb[0] == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 复位退出
     */
    public synchronized void resetExit() throws IOException {
        byte bb[] = new byte[1];
        bb[0] = 0;
        write(1, 1, bb);
    }

    /**
     * 退出
     */
    public synchronized void toExit() throws IOException {
        byte bb[] = new byte[1];
        bb[0] = 1;
        write(1, 1, bb);
    }
}
