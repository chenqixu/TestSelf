package com.cqx.common.utils.serialize;

import java.nio.ByteBuffer;

public abstract class AbstractSerializer<T> implements Serializer<T> {
    ThreadLocal<ByteBuffer> threadLocal = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            return ByteBuffer.allocateDirect(512);
        }
    };

    public ByteBuffer serialize(T t) {
        byte[] bs = t2bs(t);
        ByteBuffer bb = threadLocal.get();
        bb.clear();
        bb.put(bs);
        bb.flip();
        return bb;
    }

    public T deserialize(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        byte[] bs = new byte[bb.remaining()];
        bb.get(bs);
        return bs2t(bs);
    }

    public abstract byte[] t2bs(T t);

    public abstract T bs2t(byte[] vs);
}
