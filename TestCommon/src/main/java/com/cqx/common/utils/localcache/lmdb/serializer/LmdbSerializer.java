package com.cqx.common.utils.localcache.lmdb.serializer;

import java.nio.ByteBuffer;

/**
 * LmdbSerializer
 *
 * @author chenqixu
 */
public interface LmdbSerializer<T> {

    ByteBuffer serialize(T t);

    T deserialize(ByteBuffer byteBuffer);
}
