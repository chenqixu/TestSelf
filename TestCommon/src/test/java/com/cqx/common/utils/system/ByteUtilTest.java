package com.cqx.common.utils.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(ByteUtilTest.class);

    @Test
    public void bit_xor() {
        long a1 = 1000L;
        long a2 = 1001L;
        long a3 = ByteUtil.bit_xor(a1, a2);
        long a4 = ByteUtil.bit_xor(a3, a2);
        logger.info("{}", a4);
    }
}