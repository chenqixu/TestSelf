package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.AbstractSerializer;
import com.cqx.common.utils.serialize.protostuff.ProtostuffUtils;

public class ProtoStuffSerializer<T> extends AbstractSerializer<T> {
    private final Class<T> clazz;

    public ProtoStuffSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] t2bs(T t) {
        return ProtostuffUtils.serialize(t);
    }

    @Override
    public T bs2t(byte[] bs) {
        return ProtostuffUtils.deserialize(bs, clazz);
    }
}
