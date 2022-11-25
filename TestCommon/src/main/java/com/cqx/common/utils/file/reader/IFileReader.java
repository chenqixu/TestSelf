package com.cqx.common.utils.file.reader;

import java.io.IOException;

/**
 * IFileReader
 *
 * @author chenqixu
 */
public interface IFileReader {

    String readLine() throws IOException;

    int read(byte b[], int off, int len) throws IOException;

    void close() throws IOException;
}
