package com.cqx.common.utils.file.writer;

import com.cqx.common.utils.system.ArrayUtil;
import com.cqx.common.utils.system.ByteUtil;

import java.io.*;

/**
 * 继承FileOutputStream，实现IFileWriter接口<br>
 * 数据格式：长度+内容，长度占4个字节(int)<br>
 * 使用：先写入数据长度，然后写入内容
 *
 * @author chenqixu
 */
public class FileOutputStreamLVWriter extends FileOutputStream implements IFileWriter {
    public FileOutputStreamLVWriter(String name) throws FileNotFoundException {
        super(name);
    }

    public FileOutputStreamLVWriter(String name, boolean append) throws FileNotFoundException {
        super(name, append);
    }

    public FileOutputStreamLVWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public FileOutputStreamLVWriter(File file, boolean append) throws FileNotFoundException {
        super(file, append);
    }

    public FileOutputStreamLVWriter(FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public void write(String str) throws IOException {
        byte[] bytes = str.getBytes("ISO_8859_1");
        // 写入长度位+内容
        write(ArrayUtil.arrayCopy(ByteUtil.numberToBytes(bytes.length), bytes));
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
        throw new UnsupportedOperationException("不支持的操作!");
    }
}
