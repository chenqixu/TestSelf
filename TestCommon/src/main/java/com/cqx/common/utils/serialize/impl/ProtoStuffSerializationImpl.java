package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.protostuff.ProtostuffUtils;

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
    public byte[] serialize(T o) {
        return ProtostuffUtils.serialize(o, tClass);
    }

    @Override
    public T deserialize(byte[] bytes) {
        return ProtostuffUtils.deserialize(bytes, tClass);
    }
}
