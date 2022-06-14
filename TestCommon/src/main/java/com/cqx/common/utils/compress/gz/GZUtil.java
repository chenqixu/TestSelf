package com.cqx.common.utils.compress.gz;

import com.cqx.common.utils.compress.AbstractCompress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZUtil
 *
 * @author chenqixu
 */
public class GZUtil extends AbstractCompress {
    private static final Logger logger = LoggerFactory.getLogger(GZUtil.class);
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
        } else if (outStream instanceof FileOutputStream) {
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

    @Override
    public void compress(String sourceFileName, String dstFileName) throws IOException {
        if (sourceFileName != null) {
            File file = new File(sourceFileName);
            if (file.exists() && file.isFile()) {
                try (FileInputStream in = new FileInputStream(sourceFileName);
                     GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(dstFileName))) {
                    byte[] buffer = new byte[BUFF_SIZE];
                    int ret;
                    while ((ret = in.read(buffer)) > 0) {
                        gzip.write(buffer, 0, ret);
                    }
                } catch (IOException e) {
                    logger.error(String.format("%s gzip compress error.", sourceFileName), e);
                    throw e;
                }
            }
        }
    }

    @Override
    public byte[] uncompress(String sourceFileName) throws IOException {
        if (sourceFileName != null) {
            File file = new File(sourceFileName);
            if (file.exists() && file.isFile()) {
                return uncompress(new FileInputStream(sourceFileName));
            }
        }
        return null;
    }

    @Override
    public byte[] uncompress(byte[] dataBytes) throws IOException {
        if (dataBytes == null || dataBytes.length == 0) {
            return null;
        }
        return uncompress(new ByteArrayInputStream(dataBytes));
    }

    private byte[] uncompress(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream ungzip = new GZIPInputStream(in)
        ) {
            byte[] buffer = new byte[BUFF_SIZE];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("gzip uncompress error.", e);
            throw e;
        }
    }
}
