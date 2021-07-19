package com.cqx.common.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 用于缓存文件读取位置
 *
 * @author chenqixu
 */
public class TagFile implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(TagFile.class);
    private final int MAX_LEN = 20;
    private final byte[] NULL_BYTE = new byte[MAX_LEN];
    private final String NULL_VALUE = new String(NULL_BYTE);
    private BaseRandomAccessFile baseRandomAccessFile;

    public TagFile(String fileName) throws FileNotFoundException {
        baseRandomAccessFile = new BaseRandomAccessFile(fileName);
    }

    /**
     * 从文件读取文件缓存的位置
     *
     * @return
     * @throws IOException
     */
    public long readTag() throws IOException {
        String value = baseRandomAccessFile.read(0L, MAX_LEN);
        value = value.replace("a", "");
        // 如果是空，要设置为0
        if (value.equals(NULL_VALUE)) {
            value = "0";
        }
        return Long.valueOf(value);
    }

    /**
     * 把文件位置缓存写入文件
     *
     * @param pos
     * @return
     * @throws IOException
     */
    public boolean writeTag(long pos) throws IOException {
        StringBuilder value = new StringBuilder(String.valueOf(pos));
        if (value.length() < MAX_LEN) {
            for (int i = value.length(); i < MAX_LEN; i++) {
                value.append("a");
            }
            logger.debug("{} {}", value.toString(), value.length());
        }
        return baseRandomAccessFile.write(0L, value.toString());
    }

    /**
     * 资源释放
     */
    @Override
    public void close() {
        if (baseRandomAccessFile != null) {
            try {
                baseRandomAccessFile.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
