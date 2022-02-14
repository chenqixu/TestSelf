package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;

import java.io.*;

/**
 * java
 *
 * @author chenqixu
 */
public class JavaSerializationImpl<T> implements ISerialization<T> {
    private Class<T> tClass;

    @Override
    public void setTClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    public byte[] serialize(T object) {
        if (object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
            } catch (IOException var3) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), var3);
            }
            return baos.toByteArray();
        }
    }

    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ois.readObject();
            } catch (IOException var2) {
                throw new IllegalArgumentException("Failed to deserialize object", var2);
            } catch (ClassNotFoundException var3) {
                throw new IllegalStateException("Failed to deserialize object type", var3);
            }
        }
    }
}
