package com.cqx.common.utils.serialize;

import java.nio.ByteBuffer;

public interface Serializer<T> {

    ByteBuffer serialize(T t);

    T deserialize(ByteBuffer bs);
}
