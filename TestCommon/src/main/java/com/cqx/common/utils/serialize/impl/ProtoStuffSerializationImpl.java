package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.protostuff.ProtostuffUtils;

/**
 * ProtoStuffSerializationImpl
 *
 * @author chenqixu
 */
public class ProtoStuffSerializationImpl<T> implements ISerialization {
    private Class<T> tClass;

    @Override
    public void setTClass(Class tClass) {
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(Object o) {
        return ProtostuffUtils.serialize(o);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return ProtostuffUtils.deserialize(bytes, tClass);
    }
}
