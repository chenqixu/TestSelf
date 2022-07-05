package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To Hex
 *
 * @author chenqixu
 */
public class HexRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) throws Exception {
        return ByteUtil.bytesToHexStringH(bytes);
    }
}
