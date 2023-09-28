package com.cqx.common.utils.system;

import com.cqx.common.utils.io.MyByteArrayOutputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
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

        logger.info("intTo4ByteArray 10:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.intTo4ByteArray(10)));
        logger.info("numberToBytes 10:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes("10", 4)));
        logger.info("numberToBytes 10:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes(10)));

        logger.info("intTo2ByteArray 10:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.intTo2ByteArray(10)));
        logger.info("numberToBytes 10:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes("10", 2)));

        logger.info("longTo8ByteArray 300000000:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.longTo8ByteArray(30000000000L)));
        logger.info("numberToBytes 300000000:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes("30000000000", 8)));
        logger.info("numberToBytes 300000000:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes(30000000000L)));

        logger.info("doubleToLongBits 2.25d:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes(Double.doubleToLongBits(2.25d))));
        logger.info("doubleToRawLongBits 2.25d:{}", ByteUtil.bytesToBitBySeparator(ByteUtil.numberToBytes(Double.doubleToRawLongBits(2.25d))));

        logger.info("doubleToRawLongBits -> numberToBytes -> unsignedBytesToLong -> longBitsToDouble：{}", Double.longBitsToDouble(
                ByteUtil.unsignedBytesToLong(
                        ByteUtil.numberToBytes(
                                Double.doubleToRawLongBits(2.25d)))));

        logger.info("(byte) 0x12：{}，(byte) 12：{}", (byte) 0x12, (byte) 12);
    }

    @Test
    public void intToByte() {
        logger.info(String.format("compare 0x02 & int 2, result=%s", 0x02 == ByteUtil.intToByte(2)));
        logger.info(String.format("compare 0x01 & int 1, result=%s", 0x01 == ByteUtil.intToByte(1)));
        logger.info(String.format("compare 0x00 & int 0, result=%s", 0x00 == ByteUtil.intToByte(0)));
    }

    @Test
    public void buildTLV() {
        byte[] bytes = {1};
        logger.info("{}", ByteUtil.bytesToHexStringH(ByteUtil.buildTLV(0, bytes)));
        logger.info("{}", ByteUtil.bytesToHexStringH(new byte[]{ByteUtil.getLowBit((byte) 1)}));
        logger.info("{}", ByteUtil.byteToBit(ByteUtil.getLowBit((byte) 1)));
        logger.info("{}", ByteUtil.bytesToHexStringH(new byte[]{ByteUtil.getLowBitToHigh((byte) 1)}));
        logger.info("{}", ByteUtil.byteToBit(ByteUtil.getLowBitToHigh((byte) 1)));
        logger.info("format={}", ByteUtil.byteToFormat(ByteUtil.getHighBit(ByteUtil.getLowBitToHigh((byte) 1))));
        logger.info("[hexStringToBytes]10460070FFF00008601424501={}", ByteUtil.hexStringToBytes("10460070FFF00008601424501"));
        logger.info("[bytesToHexStringH]={}", ByteUtil.bytesToHexStringH(ByteUtil.hexStringToBytes("10460070FFF00008601424501")));
        logger.info("[bytesToHexStringH]={}", ByteUtil.bytesToHexStringH1(ByteUtil.hexStringToBytes("10460070FFF00008601424501")));
        logger.info("[bytesToHexStringH]={}", ByteUtil.bytesToHexStringH2(ByteUtil.hexStringToBytes("10460070FFF00008601424501")));
        logger.info("[bytesToHexStringH]={}", ByteUtil.bytesToHexStringH3(ByteUtil.hexStringToBytes("10460070FFF00008601424501")));

        logger.info("getLowBitToHigh={}", ByteUtil.byteToBit((byte) 8));
        logger.info("getLowBitToHigh={}", ByteUtil.byteToBit(ByteUtil.getLowBitToHigh((byte) 8)));
        logger.info("getHighBit={}", ByteUtil.getHighBit(ByteUtil.getLowBitToHigh((byte) 8)));
        logger.info("unsignedByte={}", ByteUtil.unsignedByte(ByteUtil.getLowBitToHigh((byte) 8)));
        logger.info("format={}", ByteUtil.byteToFormat(ByteUtil.getHighBit(ByteUtil.getLowBitToHigh((byte) 8))));
        logger.info("getLowBitToHigh={}", ByteUtil.byteToBit((byte) 12));
        logger.info("getLowBitToHigh={}", ByteUtil.byteToBit(ByteUtil.getLowBitToHigh((byte) 12)));
    }

    @Test
    public void HexTest() {
        logger.info("{}", ByteUtil.unsignedBytesToInt(ByteUtil.hexStringToBytes("2e20006f")));
    }

    @Test
    public void ECGIToECI() {
        // 460-00-278387-64	71267136
        String eNBId = Integer.toHexString(278387);
        String CellId = Integer.toHexString(64);
        long ECI = Long.valueOf(eNBId + CellId, 16);
        logger.info("eNBId={}, CellId={}, ECI={}", eNBId, CellId, ECI);
    }

    @Test
    public void systemOut() {
        PrintStream out = System.out;
        PrintStream sysout = MyByteArrayOutputStream.buildPrintStream(value -> {
            if (value.contains("test123")) {
                System.setOut(out);
//                logger.info("catch!");
                System.out.println("catch!");
            }
        });
        System.setOut(sysout);
        for (int i = 0; i < 200; i++) {
//            System.out.println("test" + i);
            logger.info("test" + i);
//            sysout.println("test" + i);
        }
    }

    @Test
    public void stringPrint() {
        String str = "\n";
        for (char c : str.toCharArray()) {
            logger.info("char {}", (int) c);
        }
    }
}