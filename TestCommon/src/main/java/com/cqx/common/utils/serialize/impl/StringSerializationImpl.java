package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;

import java.io.IOException;

/**
 * String
 *
 * @author chenqixu
 */
public class StringSerializationImpl implements ISerialization<String> {

    @Override
    public void setTClass(Class aClass) {
        //
    }

    @Override
    public byte[] serialize(String o) throws IOException {
        return o.getBytes();
    }

    @Override
    public String deserialize(byte[] bytes) throws IOException {
        return new String(bytes);
    }
}
