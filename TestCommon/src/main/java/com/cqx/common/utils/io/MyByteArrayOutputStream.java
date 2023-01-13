package com.cqx.common.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 字节数组输出流<br>
 * <pre>
 *     PrintStream写入到字节数组输出流
 *     在字节数组输出流实现以下功能：
 *     针对换行符进行切割，对切割后的数据进行个性化处理
 * </pre>
 *
 * @author chenqixu
 */
public class MyByteArrayOutputStream extends ByteArrayOutputStream {

    /**
     * Line separator string.  This is the value of the line.separator
     * property at the moment that the stream was created.
     */
    private String lineSeparator;
    private StringBuilder sb = new StringBuilder();
    private IMyByteArrayOutputStreamParser iMyByteArrayOutputStreamParser;

    public MyByteArrayOutputStream(IMyByteArrayOutputStreamParser iMyByteArrayOutputStreamParser) {
        super();
        init();
        this.iMyByteArrayOutputStreamParser = iMyByteArrayOutputStreamParser;
    }

    public MyByteArrayOutputStream(int size, IMyByteArrayOutputStreamParser iMyByteArrayOutputStreamParser) {
        super(size);
        init();
        this.iMyByteArrayOutputStreamParser = iMyByteArrayOutputStreamParser;
    }

    public static PrintStream buildPrintStream(IMyByteArrayOutputStreamParser iMyByteArrayOutputStreamParser) {
        return new PrintStream(new MyByteArrayOutputStream(iMyByteArrayOutputStreamParser));
    }

    public static PrintStream buildPrintStream(int size, IMyByteArrayOutputStreamParser iMyByteArrayOutputStreamParser) {
        return new PrintStream(new MyByteArrayOutputStream(size, iMyByteArrayOutputStreamParser));
    }

    private void init() {
        lineSeparator = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("line.separator"));
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) {
        super.write(b, off, len);
        if (len > 0) {
            sb.append(super.toString());
            reset();
            while (true) {
                int _find = sb.indexOf(lineSeparator);
                if (_find >= 0) {
                    String _new = sb.substring(0, _find);
                    sb.delete(0, _find + lineSeparator.length());
                    iMyByteArrayOutputStreamParser.parser(_new);
                } else {
                    break;
                }
            }
        }
    }

    public interface IMyByteArrayOutputStreamParser {

        void parser(String value);
    }
}
