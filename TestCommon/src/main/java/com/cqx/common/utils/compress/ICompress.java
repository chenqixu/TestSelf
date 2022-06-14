package com.cqx.common.utils.compress;

import java.io.IOException;

/**
 * ICompress
 *
 * @author chenqixu
 */
public interface ICompress {

    void compress(String sourceFileName) throws IOException;

    void compress(String sourceFileName, String dstFileName) throws IOException;

    byte[] uncompress(String sourceFileName) throws IOException;

    byte[] uncompress(byte[] dataBytes) throws IOException;

    void uncompress(String sourceFileName, String dstFileName) throws IOException;
}
