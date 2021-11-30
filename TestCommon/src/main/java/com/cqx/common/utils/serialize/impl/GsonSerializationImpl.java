package com.cqx.common.utils.serialize.impl;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.gson.DateDeserializer;
import com.cqx.common.utils.serialize.gson.DateSerializer;
import com.cqx.common.utils.serialize.gson.ParameterizedTypeImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

/**
 * Gson
 *
 * @author chenqixu
 */
public class GsonSerializationImpl<T> implements ISerialization<T> {
    private Gson gson;
    private Type type;
    // 默认的构造
    private Gson gsonDefault = new GsonBuilder().create();
    // 排除一些不需要序列化反序列化的字段，每个字段都需要Expose注解
    private Gson gsonExpose = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    // 支持long转时间
    private Gson gsonDateLong = new GsonBuilder()
            .registerTypeAdapter(java.util.Date.class, new DateSerializer())
            .registerTypeAdapter(java.util.Date.class, new DateDeserializer())
            .setDateFormat(DateFormat.LONG)
            .create();
    private Class<T> tClass;

    @Override
    public void setTClass(Class<T> tClass) {
        this.tClass = tClass;
        // 判断下字段有没Expose注解，如果有就使用gsonExpose
        for (Field field : tClass.getDeclaredFields()) {
            Expose expose = field.getAnnotation(Expose.class);
            if (expose != null) {
                gson = gsonExpose;
                break;
            }
        }
        // 判断下是否是List，如果是则需要使用ParameterizedTypeImpl进行类型构造，防止泛型出错
        // todo 这里有问题
        if (tClass.isAssignableFrom(List.class)) {
            type = new ParameterizedTypeImpl(List.class, new Class[]{tClass});
        }
        // 判断下是否是Map，如果是则需要使用ParameterizedTypeImpl进行类型构造，防止泛型出错
        else if (tClass.isAssignableFrom(Map.class)) {
            // 不支持
            throw new UnsupportedOperationException("不支持Map类型！");
        }
        // 使用默认值
        if (gson == null) {
            gson = gsonDefault;
        }
    }

    @Override
    public byte[] serialize(T t) throws IOException {
        if (t == null) {
            return null;
        } else {
            try {
                return gson.toJson(t).getBytes();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize object", e);
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        } else {
            try {
                if (type != null) {
                    return gson.fromJson(new String(bytes), this.type);
                } else {
                    return gson.fromJson(new String(bytes), this.tClass);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize " + tClass.getName() + " type", e);
            }
        }
    }
}
