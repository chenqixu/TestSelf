package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To Integer
 *
 * @author chenqixu
 */
public class IntegerRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) {
        return String.valueOf(Integer.valueOf(ByteUtil.bytesToHexStringH(bytes), 16));
    }
}
