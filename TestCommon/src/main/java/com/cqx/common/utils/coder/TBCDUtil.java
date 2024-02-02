package com.cqx.common.utils.coder;

import com.cqx.common.utils.system.ByteUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TBCD规则：把单字节的低四位和高四位互换，字节顺序不变
 */
public class TBCDUtil {
    private static final Logger logger = LoggerFactory.getLogger(TBCDUtil.class);
    private static final String cTBCDSymbolString = "0123456789*#abc";
    private static final byte[] HEX_CHAR = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final StringBuilder EMPTY_STIRNG = new StringBuilder(StringUtils.EMPTY);
    private static char[] cTBCDSymbols = cTBCDSymbolString.toCharArray();

    /*
     * This method converts a TBCD string to a character string.
     */
    public static StringBuilder toTBCD(byte[] tbcd) {
        int size = (tbcd == null ? 0 : tbcd.length);
        StringBuilder buffer = new StringBuilder(2 * size);
        for (int i = 0; i < size; ++i) {
            int octet = tbcd[i];
            int n2 = (octet >> 4) & 0xF;
            int n1 = octet & 0xF;

            if (n1 == 15) {
                throw new NumberFormatException("Illegal filler in octet n=" + i);
            }
            buffer.append(cTBCDSymbols[n1]);

            if (n2 == 15) {
                if (i != size - 1) throw new NumberFormatException("Illegal filler in octet n=" + i);
            } else buffer.append(cTBCDSymbols[n2]);
        }

        return buffer;
    }

    /*
     * This method converts a character string to a TBCD string.
     */
    public static byte[] parseTBCD(String tbcd) {
        int length = (tbcd == null ? 0 : tbcd.length());
        int size = (length + 1) / 2;
        byte[] buffer = new byte[size];

        for (int i = 0, i1 = 0, i2 = 1; i < size; ++i, i1 += 2, i2 += 2) {

            char c = tbcd.charAt(i1);
            int n2 = getTBCDNibble(c, i1);
            int octet = 0;
            int n1 = 15;
            if (i2 < length) {
                c = tbcd.charAt(i2);
                n1 = getTBCDNibble(c, i2);
            }
            octet = (n1 << 4) + n2;
            buffer[i] = (byte) (octet & 0xFF);
        }

        return buffer;
    }

    private static int getTBCDNibble(char c, int i1) {
        int n = Character.digit(c, 10);

        if (n < 0 || n > 9) {
            switch (c) {
                case '*':
                    n = 10;
                    break;
                case '#':
                    n = 11;
                    break;
                case 'a':
                    n = 12;
                    break;
                case 'b':
                    n = 13;
                    break;
                case 'c':
                    n = 14;
                    break;
                default:
                    throw new NumberFormatException("Bad character '" + c + "' at position " + i1);
            }
        }
        return n;
    }

    /*
     * Helper function that dumps an array of bytes in the hexadecimal format.
     */
    public static final StringBuilder dumpBytes(byte[] buffer) {
        if (buffer == null) {
            return EMPTY_STIRNG;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < buffer.length; i++) {
            sb.append("0x").append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4])).append((char) (HEX_CHAR[buffer[i] & 0x000F])).append(" ");
        }

        return sb;
    }

    /**
     * 字节数组转TBCD字节数组
     *
     * @param bytes
     * @return
     */
    public static final byte[] bytesToTBCD(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            logger.info("[原始] byte={}, bit={}", bytes[i], ByteUtil.byteToBit(bytes[i]));
            // 低四位左移
            byte lowb = (byte) (bytes[i] << 4);
            logger.info("[低四位左移] lowb.byte={}, bit={}", lowb, ByteUtil.byteToBit(lowb));
            // 高四位右移，左边是符号位，需要& 0xf变为0
            byte highb = (byte) (bytes[i] >> 4 & 0xf);
            logger.info("[高四位右移] highb.byte={}, bit={}", highb, ByteUtil.byteToBit(highb));
            // 高四位和低四位交换
            byte nb = (byte) (lowb | highb);
            logger.info("[高四位和低四位交换] nb.byte={}, bit={}", nb, ByteUtil.byteToBit(nb));
            result[i] = nb;
        }
        return result;
    }

    /**
     * TBCD字节数组还原
     *
     * @param bytes
     * @return
     */
    public static final byte[] TBCDToBytes(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            logger.info("[原始] byte={}, bit={}", bytes[i], ByteUtil.byteToBit(bytes[i]));
            // 高四位右移，还原低四位
            byte lowb = (byte) (bytes[i] >> 4 & 0xf);
            logger.info("[高四位右移，还原低四位] lowb.byte={}, bit={}", lowb, ByteUtil.byteToBit(lowb));
            // 低四位左移，还原高四位
            byte highb = (byte) (bytes[i] << 4);
            logger.info("[低四位左移，还原高四位] highb.byte={}, bit={}", highb, ByteUtil.byteToBit(highb));
            // 低四位和高四位交换
            byte nb = (byte) (lowb | highb);
            logger.info("[低四位和高四位交换] nb.byte={}, bit={}", nb, ByteUtil.byteToBit(nb));
            result[i] = nb;
        }
        return result;
    }
}
