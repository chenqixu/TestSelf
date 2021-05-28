package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.kryo.KryoUtils;

import java.io.IOException;

/**
 * kryo
 *
 * @author chenqixu
 */
public class KryoSerializationImpl<T> implements ISerialization<T> {
    private Class<T> tClass;

    @Override
    public void setTClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(Object t) {
        if (t == null) {
            return null;
        } else {
            return KryoUtils.serializeObject(t, tClass);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        } else {
            return KryoUtils.deserializeObject(bytes, tClass);
        }
    }
}
