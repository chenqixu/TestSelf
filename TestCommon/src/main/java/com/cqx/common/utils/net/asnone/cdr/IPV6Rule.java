package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To IPV6
 *
 * @author chenqixu
 */
public class IPV6Rule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) {
        String hexStr = ByteUtil.bytesToHexStringH(bytes);
        String v1 = hexStr.substring(0, 4);
        String v2 = hexStr.substring(4, 8);
        String v3 = hexStr.substring(8, 12);
        String v4 = hexStr.substring(12, 16);
        String v5 = hexStr.substring(16, 20);
        String v6 = hexStr.substring(20, 24);
        String v7 = hexStr.substring(24, 28);
        String v8 = hexStr.substring(28, 32);
        return String.format("%s:%s:%s:%s:%s:%s:%s:%s"
                , v1, v2, v3, v4, v5, v6, v7, v8);
    }
}
