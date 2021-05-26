package com.cqx.common.utils.serialize.kryo.registrar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

public class SingleRegistrar<T> implements IKryoRegistrar {
    final Class<T> klass;
    final Serializer<T> serializer;

    public SingleRegistrar(Class<T> cls, Serializer<T> ser) {
        klass = cls;
        serializer = ser;
    }

    @Override
    public void apply(Kryo k) {
        k.register(klass, serializer);
    }
}
