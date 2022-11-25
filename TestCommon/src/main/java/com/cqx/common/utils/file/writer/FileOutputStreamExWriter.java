package com.cqx.common.utils.file.writer;

import java.io.*;

/**
 * 继承FileOutputStream，实现IFileWriter接口
 *
 * @author chenqixu
 */
public class FileOutputStreamExWriter extends FileOutputStream implements IFileWriter {
    public FileOutputStreamExWriter(String name) throws FileNotFoundException {
        super(name);
    }

    public FileOutputStreamExWriter(String name, boolean append) throws FileNotFoundException {
        super(name, append);
    }

    public FileOutputStreamExWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public FileOutputStreamExWriter(File file, boolean append) throws FileNotFoundException {
        super(file, append);
    }

    public FileOutputStreamExWriter(FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public void write(String str) throws IOException {
        write(str.getBytes());
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        throw new UnsupportedOperationException("不支持的操作!");
    }
}
