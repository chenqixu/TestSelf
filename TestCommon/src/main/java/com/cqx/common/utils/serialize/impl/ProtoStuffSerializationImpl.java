package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.protostuff.ProtostuffUtils;

import java.io.IOException;

/**
 * ProtoStuffSerializationImpl
 *
 * @author chenqixu
 */
public class ProtoStuffSerializationImpl<T> implements ISerialization<T> {
    private Class<T> tClass;

    @Override
    public void setTClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(T o) throws IOException {
        return ProtostuffUtils.serialize(o, tClass);
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        return ProtostuffUtils.deserialize(bytes, tClass);
    }
}
