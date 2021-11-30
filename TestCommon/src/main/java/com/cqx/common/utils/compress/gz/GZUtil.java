package com.cqx.common.utils.compress.gz;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * GZUtil
 *
 * @author chenqixu
 */
public class GZUtil {
    private OutputStream outStream;
    private GZIPOutputStream gzout;
    private boolean isMemory;
    private boolean isFile;
    private volatile boolean isClose;
    private volatile boolean isFlush;
    private long size;

    private GZUtil(OutputStream outStream, boolean syncFlush) throws IOException {
        if (outStream instanceof ByteArrayOutputStream) {
            isMemory = true;
        }
        if (outStream instanceof FileOutputStream) {
            isFile = true;
        }
        this.outStream = outStream;
        gzout = new GZIPOutputStream(outStream, syncFlush);
    }

    /**
     * 基于内存构造
     *
     * @return
     * @throws IOException
     */
    public static GZUtil buildMemory() throws IOException {
        return buildMemory(false);
    }

    public static GZUtil buildMemory(boolean syncFlush) throws IOException {
        return new GZUtil(new ByteArrayOutputStream(), syncFlush);
    }

    /**
     * 基于文件构造
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static GZUtil buildFile(String fileName) throws IOException {
        return buildFile(fileName, false);
    }

    public static GZUtil buildFile(String fileName, boolean syncFlush) throws IOException {
        return new GZUtil(new FileOutputStream(new File(fileName)), syncFlush);
    }

    public void write(byte[] msg) throws IOException {
        gzout.write(msg);
        isFlush = false;
    }

    public void flush() throws IOException {
        gzout.flush();
        isFlush = true;
    }

    public void close() throws IOException {
        if (!isClose) {
            flush();
            size();
            size = size + 10;//如果落地成文件，会少10字节，所以这里需要加上
            gzout.close();
            outStream.close();
            isFlush = true;
            isClose = true;
        } else {
            throw new IOException("不允许重复close !");
        }
    }

    public long size() throws IOException {
        if (isFlush) {
            if (isMemory) {
                size = ((ByteArrayOutputStream) outStream).toByteArray().length;
                return size;
            }
            if (isFile) {
                if (((FileOutputStream) outStream).getChannel().isOpen()) {
                    size = ((FileOutputStream) outStream).getChannel().size();
                    return size;
                } else {
                    return size;
                }
            }
        }
        return size;
    }

    public void saveMemoryToFile(String fileName) throws IOException {
        if (isFlush && isMemory)
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(((ByteArrayOutputStream) outStream).toByteArray());
            }
    }
}
