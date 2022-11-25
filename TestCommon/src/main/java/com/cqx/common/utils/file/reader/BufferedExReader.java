package com.cqx.common.utils.file.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * 继承BufferedReader，实现IFileReader接口
 *
 * @author chenqixu
 */
public class BufferedExReader extends BufferedReader implements IFileReader {
    public BufferedExReader(Reader in, int sz) {
        super(in, sz);
    }

    public BufferedExReader(Reader in) {
        super(in);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        throw new UnsupportedOperationException("不支持的操作!");
    }
}
