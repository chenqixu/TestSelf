package com.cqx.common.utils.serialize;

import java.io.IOException;

/**
 * 序列化、反序列化
 *
 * @author chenqixu
 */
public interface ISerialization<T> {

    void setTClass(Class<T> tClass);

    byte[] serialize(T t) throws IOException;

    T deserialize(byte[] bytes) throws IOException;
}
