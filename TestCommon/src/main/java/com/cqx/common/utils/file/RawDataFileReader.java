package com.cqx.common.utils.file;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.ProtoStuffSerializationImpl;

import java.io.IOException;

/**
 * RawDataFileReader
 *
 * @author chenqixu
 */
public class RawDataFileReader {
    private ISerialization<RawDataR> iSerialization;

    public RawDataFileReader() {
        iSerialization = new ProtoStuffSerializationImpl<>();
        iSerialization.setTClass(RawDataR.class);
    }

    public byte[] readToBytes(byte[] buff) throws IOException {
        RawDataR rawDataR = iSerialization.deserialize(buff);
        return rawDataR.restoreByte();
    }
}
