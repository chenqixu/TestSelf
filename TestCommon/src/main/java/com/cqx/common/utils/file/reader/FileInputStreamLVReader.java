package com.cqx.common.utils.file.reader;

import com.cqx.common.utils.system.ByteUtil;

import java.io.*;

/**
 * 继承FileInputStream，实现IFileReader接口<br>
 * 数据格式：长度+内容，长度占4个字节(int)<br>
 * 使用：先读取长度，然后根据读取出来的长度读取对应长度的内容
 *
 * @author chenqixu
 */
public class FileInputStreamLVReader extends FileInputStream implements IFileReader {
    private final int _lv_length = 4;
    private int _lv_read_off = 0;

    public FileInputStreamLVReader(String name) throws FileNotFoundException {
        super(name);
    }

    public FileInputStreamLVReader(File file) throws FileNotFoundException {
        super(file);
    }

    public FileInputStreamLVReader(FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public String readLine() throws IOException {
        // 先读取长度
        byte[] length = new byte[_lv_length];
        int _ret = read(length, 0, _lv_length);
        // 位移
        _lv_read_off += _ret;
        if (_ret > 0) {
            // 根据长度读取内容
            int _content_length = ByteUtil.unsignedBytesToInt(length);
            byte[] content = new byte[_content_length];
            _ret = read(content, 0, _content_length);
            // 位移
            _lv_read_off += _ret;
            return new String(content, "ISO_8859_1");
        }
        return null;
    }
}
