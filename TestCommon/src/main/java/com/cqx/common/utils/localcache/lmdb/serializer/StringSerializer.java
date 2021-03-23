package com.cqx.common.utils.localcache.lmdb.serializer;

import java.nio.charset.StandardCharsets;

/**
 * LmdbSerializerString
 *
 * @author chenqixu
 */
public class StringSerializer extends AbstractLmdbSerializer<String> {

    @Override
    byte[] beanToBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    String bytesToBean(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
