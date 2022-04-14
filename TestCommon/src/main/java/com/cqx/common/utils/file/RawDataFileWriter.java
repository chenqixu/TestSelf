package com.cqx.common.utils.file;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.ProtoStuffSerializationImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * RawDataFileWriter
 *
 * @author chenqixu
 */
public class RawDataFileWriter {
    private ISerialization<RawDataR> iSerialization;
    private String file;

    public RawDataFileWriter(String file) {
        this.file = file;
        iSerialization = new ProtoStuffSerializationImpl<>();
        iSerialization.setTClass(RawDataR.class);
    }

    public byte[] getSrcBytes() throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(file))) {
            byte[] buff = new byte[fis.available()];
            int ret = fis.read(buff);
            RawDataR rawDataR = new RawDataR(buff);
            return iSerialization.serialize(rawDataR);
        }
    }
}
