package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To IPV4
 *
 * @author chenqixu
 */
public class IPV4Rule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) {
        String hexStr = ByteUtil.bytesToHexStringH(bytes);
        String v1 = hexStr.substring(0, 2);
        String v2 = hexStr.substring(2, 4);
        String v3 = hexStr.substring(4, 6);
        String v4 = hexStr.substring(6, 8);
        return String.format("%s.%s.%s.%s"
                , Integer.valueOf(v1, 16)
                , Integer.valueOf(v2, 16)
                , Integer.valueOf(v3, 16)
                , Integer.valueOf(v4, 16)
        );
    }
}
