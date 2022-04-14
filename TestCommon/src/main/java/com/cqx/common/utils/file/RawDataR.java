package com.cqx.common.utils.file;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * \r
 *
 * @author chenqixu
 */
public class RawDataR implements Serializable {
    public static final byte r = "\r".getBytes()[0];
    private List<RawDataN> rawDataNList;
    private int len;

    public RawDataR(byte[] bytes) {
        len = bytes.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        rawDataNList = new ArrayList<>();
        for (byte b : bytes) {
            if (b == r) {
                int position = byteBuffer.position();
                byte[] buf = new byte[position];
                byteBuffer.flip();
                byteBuffer.get(buf, 0, position);
                rawDataNList.add(new RawDataN(buf));
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
            rawDataNList.add(new RawDataN(buf));
        }
    }

    public List<RawDataN> getRawDataNList() {
        return rawDataNList;
    }

    public byte[] restoreByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        int size = getRawDataNList().size();
        for (int i = 0; i < size; i++) {
            byteBuffer.put(getRawDataNList().get(i).restoreByte());
            if (i + 1 < size) {
                byteBuffer.put(r);
            }
        }
        return byteBuffer.array();
    }
}
