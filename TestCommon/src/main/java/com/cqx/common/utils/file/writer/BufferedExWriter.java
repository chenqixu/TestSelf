package com.cqx.common.utils.file.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 继承BufferedWriter，实现IFileWriter接口
 *
 * @author chenqixu
 */
public class BufferedExWriter extends BufferedWriter implements IFileWriter {
    public BufferedExWriter(Writer out) {
        super(out);
    }

    public BufferedExWriter(Writer out, int sz) {
        super(out, sz);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(new String(b));
    }

    /**
     * element b[off] is the first byte written and b[off+len-1] is the last byte written by this operation.
     *
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] _b = new byte[len];
        for (int j = 0, i = off; i < len; i++) {
            _b[j] = b[i];
            j++;
        }
        write(_b);
    }
}
