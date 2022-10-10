package com.cqx.common.utils.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;

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

    @Test
    public void TLVTest() {
        // int是占用了4个byte，一个byte是8位，总共32位，有符号，符号位占1位，所以最大值2^31=2147483648
        int tag = 149;
        byte b1 = (byte) (tag >>> 8);
        byte b2 = (byte) (tag >>> 8 & 255);
        byte b3 = (byte) (tag & 255);
        logger.info("b1:{}", ByteUtil.byteToBit(b1));
        logger.info("b2:{}", ByteUtil.byteToBit(b2));
        logger.info("b3:{}", ByteUtil.byteToBit(b3));
        logger.info("255:{}", ByteUtil.valToBit("255"));
        logger.info("55:{}", ByteUtil.valToBit("55"));
        logger.info("low 4bit:{}", ByteUtil.byteToLowBit((byte) 55));
        BitSet bs1 = ByteUtil.bytesToBitSet(new byte[]{55});
        logger.info("bytesToBitSet1:{}，len:{}", bs1, bs1.length());
        BitSet bs2 = ByteUtil.byteArray2BitSet(new byte[]{55});
        logger.info("bytesToBitSet2:{}，len:{}", bs2, bs2.length());
        logger.info("bitSet2ByteArray:{}", ByteUtil.bitSet2ByteArray(bs1));
        logger.info("bitToByte:{}", ByteUtil.unsignedByte(ByteUtil.bitToByte("00110111")));
        logger.info("bitToByte:{}", ByteUtil.unsignedByte(ByteUtil.bitToByte(ByteUtil.valToBit("55"))));
    }
}