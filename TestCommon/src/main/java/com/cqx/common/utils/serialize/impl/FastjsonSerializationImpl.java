package com.cqx.common.utils.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.serialize.ISerialization;

/**
 * fastjson
 *
 * @author chenqixu
 */
public class FastjsonSerializationImpl<T> implements ISerialization<T> {
    private Class<T> tClass;

    @Override
    public void setTClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    public byte[] serialize(T t) {
        if (t == null) {
            return null;
        } else {
            try {
                return JSON.toJSONString(t).getBytes();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize object", e);
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                return JSON.parseObject(new String(bytes), tClass);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize " + tClass.getName() + " type", e);
            }
        }
    }
}
