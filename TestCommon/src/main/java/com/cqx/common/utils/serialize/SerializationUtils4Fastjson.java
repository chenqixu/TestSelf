package com.cqx.common.utils.serialize;

import com.alibaba.fastjson.JSON;

/**
 * SerializationUtils4Fastjson
 *
 * @author chenqixu
 */
public class SerializationUtils4Fastjson {

    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        } else {
            try {
                return JSON.toJSONString(object).getBytes();
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize object", e);
            }
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> T) {
        if (bytes == null) {
            return null;
        } else {
            try {
                return JSON.parseObject(new String(bytes), T);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize " + T.getName() + " type", e);
            }
        }
    }
}
