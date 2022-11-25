package com.cqx.common.utils.file.writer;

import java.io.IOException;

/**
 * IFileWrite
 *
 * @author chenqixu
 */
public interface IFileWriter {

    void write(String str) throws IOException;

    void write(String s, int off, int len) throws IOException;

    void write(byte b[]) throws IOException;

    void write(byte b[], int off, int len) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
