package com.cqx.common.utils.serialize.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.DefaultIdStrategy;
import com.dyuproject.protostuff.runtime.Delegate;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtils {
    private final static Delegate<Timestamp> TIMESTAMP_DELEGATE = new TimestampDelegate();
    private final static DefaultIdStrategy idStrategy = ((DefaultIdStrategy) RuntimeEnv.ID_STRATEGY);
    private final static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static ThreadLocal<LinkedBuffer> threadBuffer = new ThreadLocal<LinkedBuffer>() {
        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE * 2);
        }
    };

    static {
        idStrategy.registerDelegate(TIMESTAMP_DELEGATE);
    }

    public static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz, idStrategy);
//            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }

    /**
     * 序列化方法，把指定对象序列化成字节数组
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        return serialize(obj, clazz);
    }

    /**
     * 序列化方法，把指定对象序列化成字节数组
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj, Class<T> clazz) {
//        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = threadBuffer.get();
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
//        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}

