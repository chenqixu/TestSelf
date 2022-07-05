package com.cqx.common.utils.net.asnone.cdr;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bytes To IA5String
 *
 * @author chenqixu
 */
public class IA5StringRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) {
        return new String(bytes, UTF_8);
    }
}
