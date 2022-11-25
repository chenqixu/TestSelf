package com.cqx.common.utils.file.reader;

import java.io.*;

/**
 * 继承FileInputStream，实现IFileReader接口
 *
 * @author chenqixu
 */
public class FileInputStreamExReader extends FileInputStream implements IFileReader {
    public FileInputStreamExReader(String name) throws FileNotFoundException {
        super(name);
    }

    public FileInputStreamExReader(File file) throws FileNotFoundException {
        super(file);
    }

    public FileInputStreamExReader(FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException("不支持的操作!");
    }
}
