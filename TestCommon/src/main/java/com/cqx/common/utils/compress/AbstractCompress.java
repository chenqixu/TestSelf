package com.cqx.common.utils.compress;

import java.io.IOException;

/**
 * AbstractCompress
 *
 * @author chenqixu
 */
public abstract class AbstractCompress implements ICompress {
    public static int BUFF_SIZE = 2048;

    public void compress(String sourceFileName) throws IOException {
    }

    public void compress(String sourceFileName, String dstFileName) throws IOException {
    }

    public byte[] uncompress(String sourceFileName) throws IOException {
        return null;
    }

    public byte[] uncompress(byte[] dataBytes) throws IOException {
        return null;
    }

    public void uncompress(String sourceFileName, String dstFileName) throws IOException {
    }
}
