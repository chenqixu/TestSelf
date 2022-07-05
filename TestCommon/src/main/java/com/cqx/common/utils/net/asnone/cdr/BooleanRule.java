package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To Boolean
 *
 * @author chenqixu
 */
public class BooleanRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) throws Exception {
        // false是全0
        return ByteUtil.unsignedByte(bytes[0]).equals("0") ? "false" : "true";
    }
}
