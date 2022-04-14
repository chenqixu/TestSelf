package com.cqx.common.utils.file;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * \n
 *
 * @author chenqixu
 */
public class RawDataN implements Serializable {
    public static final byte n = "\n".getBytes()[0];
    private List<byte[]> dataList;
    private int len;

    public RawDataN(byte[] bytes) {
        len = bytes.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        dataList = new ArrayList<>();
        for (byte b : bytes) {
            if (b == n) {
                int position = byteBuffer.position();
                byte[] buf = new byte[position];
                byteBuffer.flip();
                byteBuffer.get(buf, 0, position);
                dataList.add(buf);
                byteBuffer.clear();
            } else {
                byteBuffer.put(b);
            }
        }
        if (byteBuffer.position() > 0) {
            int position = byteBuffer.position();
            byte[] buf = new byte[position];
            byteBuffer.flip();
            byteBuffer.get(buf, 0, position);
            dataList.add(buf);
        }
    }

    public List<byte[]> getDataList() {
        return dataList;
    }

    public byte[] restoreByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        int size = getDataList().size();
        for (int i = 0; i < size; i++) {
            byteBuffer.put(getDataList().get(i));
            if (i + 1 < size) {
                byteBuffer.put(n);
            }
        }
        return byteBuffer.array();
    }
}
