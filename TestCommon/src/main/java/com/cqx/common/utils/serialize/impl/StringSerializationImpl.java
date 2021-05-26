package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;

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
    public byte[] serialize(String o) {
        return o.getBytes();
    }

    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes);
    }
}
