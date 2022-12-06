package com.cqx.common.utils.system;

import com.cqx.common.utils.Constant;
import com.cqx.common.utils.coder.TBCDUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;

/**
 * 字节工具
 *
 * @author chenqixu
 */
public class ByteUtil {
    private static final Logger logger = LoggerFactory.getLogger(ByteUtil.class);
    private static final char[] BToA = "0123456789abcdef&".toCharArray();
    private static final Random random = new Random();

    /**
     * Byte转Bit
     * <pre>
     *     由于byte是有符号的，所以高位表示符号位，剩下7位用来表示数值
     *     负数，是对原码取反，再加1，叫反码，在计算机中经过反码优化后，可以把减法变成加法
     *     比如2
     *     原码(有符号int)：00000000 00000000 00000000 00000010
     *     >>有符号右移
     *     >>>无符号右移
     *     首先将byte转化为int, 再行运算
     *
     *     (byte) ((b >> 7) & 0x1)
     *     b转成int，然后有符号右移7位，就是将第8位变成了第1位
     *     然后和0x1进行与运算，计算规则：1与1为1，1与0为0，这里的0x1就是 00000001
     *     b >> 7：00000000 00000000 00000000 00000010 变成 00000000 00000000 00000000 00000000
     *     (b >> 7) & 0x1：00000000 00000000 00000000 00000000 变成 00000000 00000000 00000000 00000000
     *     (byte) ((b >> 7) & 0x1)：00000000 00000000 00000000 00000000 变成 00000000
     *
     *     (byte) ((b >> 6) & 0x1)
     *     b转成int，然后有符号右移6位，就是将第7位变成了第1位
     *     然后和0x1进行与运算，计算规则：1与1为1，1与0为0，这里的0x1就是 00000001
     *     b >> 6：00000000 00000000 00000000 00000010 变成 00000000 00000000 00000000 00000000
     *     (b >> 6) & 0x1：00000000 00000000 00000000 00000000 变成 00000000 00000000 00000000 00000000
     *     (byte) ((b >> 6) & 0x1)：00000000 00000000 00000000 00000000 变成 00000000
     *
     *     ……
     *
     *     (byte) ((b >> 1) & 0x1)
     *     b转成int，然后有符号右移1位，就是将第2位变成了第1位
     *     然后和0x1进行与运算，计算规则：1与1为1，1与0为0，这里的0x1就是 00000001
     *     b >> 1：00000000 00000000 00000000 00000010 变成 00000000 00000000 00000000 00000001
     *     (b >> 1) & 0x1：00000000 00000000 00000000 00000001 变成 00000000 00000000 00000000 00000001
     *     (byte) ((b >> 1) & 0x1)：00000000 00000000 00000000 00000000 变成 00000001
     * </pre>
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 0) & 0x1);
    }

    /**
     * byte数组转Bit字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToBit(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byteToBit(b));
        }
        return sb.toString();
    }

    /**
     * byte数组转Bit字符串，加上分隔符
     *
     * @param bytes
     * @return
     */
    public static String bytesToBitBySeparator(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byteToBit(b)).append(",");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * byte数值转short
     *
     * @param b
     * @return
     */
    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            // byte进行位运算，会先转成int
            // 所以这里符号位就变成了数值
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }

    public static byte[] longTo8ByteArray(long i) {
        byte[] result = new byte[8];
        result[0] = (byte) ((i >> 56) & 0xFF);
        result[1] = (byte) ((i >> 48) & 0xFF);
        result[2] = (byte) ((i >> 40) & 0xFF);
        result[3] = (byte) ((i >> 32) & 0xFF);
        result[4] = (byte) ((i >> 24) & 0xFF);
        result[5] = (byte) ((i >> 16) & 0xFF);
        result[6] = (byte) ((i >> 8) & 0xFF);
        result[7] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] longTo4ByteArray(long i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] intTo4ByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] intTo2ByteArray(int i) {
        byte[] result = new byte[2];
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 无符号byte
     *
     * @param b
     * @return
     */
    public static String unsignedByte(byte b) {
        byte[] unsignedArray = {0x00, b};
        return unsignedBytes(unsignedArray);
    }

    public static int unsignedByteToInt(byte b) {
        byte[] unsignedArray = {0x00, b};
        return new BigInteger(unsignedArray).intValue();
    }

    /**
     * 无符号short
     *
     * @param val
     * @return
     */
    public static String unsignedShort(short val) {
        return String.valueOf(Short.toUnsignedInt(val));
    }

    /**
     * 无符号int
     *
     * @param val
     * @return
     */
    public static String unsignedInt(int val) {
        return String.valueOf(Integer.toUnsignedLong(val));
    }

    /**
     * 字节数组转无符号
     *
     * @param bytes
     * @return
     */
    public static BigInteger unsignedBytesToBigInteger(byte[] bytes) {
        byte[] _header = {0x00};
        byte[] _short_bytes = ArrayUtil.arrayCopy(_header, bytes);
        return new BigInteger(_short_bytes);
    }

    public static String unsignedBytes(byte[] bytes) {
        return unsignedBytesToBigInteger(bytes).toString();
    }

    public static long unsignedBytesToLong(byte[] bytes) {
        return unsignedBytesToBigInteger(bytes).longValue();
    }

    public static int unsignedBytesToInt(byte[] bytes) {
        return unsignedBytesToBigInteger(bytes).intValue();
    }

    public static byte[] numberToBytes(short data) {
        return numberToBytes(String.valueOf(data), 2);
    }

    public static byte[] numberToBytes(int data) {
        return numberToBytes(String.valueOf(data), 4);
    }

    public static byte[] numberToBytes(long data) {
        return numberToBytes(String.valueOf(data), 8);
    }

    /**
     * 整数转字节数组，short、int、long
     *
     * @param data
     * @param size
     * @return
     */
    public static byte[] numberToBytes(String data, int size) {
        byte[] bytes = new BigInteger(data).toByteArray();
        if (size > bytes.length) {
            int diff = size - bytes.length;
            byte[] newbytes = new byte[diff];
            for (int i = 0; i < diff; i++) {
                newbytes[i] = 0x00;
            }
            bytes = ArrayUtil.arrayAdd(newbytes, bytes, bytes.length);
        } else if (bytes.length > size) {
            // 取低位
            int diff = bytes.length - size;
            byte[] newbytes = new byte[size];
            for (int i = diff, j = 0; i < bytes.length; i++, j++) {
                newbytes[j] = bytes[i];
            }
            bytes = newbytes;
        }
        return bytes;
    }

    /**
     * 返回Bit
     *
     * @param val
     * @return
     */
    public static String valToBit(String val) {
        return bytesToBit(new BigInteger(val).toByteArray());
    }

    /**
     * 随机生成byte数组
     *
     * @param len
     * @return
     */
    public static byte[] randomUnsignedNum(int len) {
        byte[] ret = new byte[len];
        random.nextBytes(ret);
        return ret;
    }

    /**
     * BitSet转成byte数组
     *
     * @param bitSet
     * @return
     */
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.length() / 8];
        for (int i = 0; i < bitSet.length(); i++) {
            int index = i / 8;
            int offset = 7 - i % 8;
            bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
        }
        return bytes;
    }

    /**
     * byte数组转成BitSet
     *
     * @param bytes
     * @return
     */
    public static BitSet byteArray2BitSet(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length * 8);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1);
            }
        }
        return bitSet;
    }

    /**
     * byte数组转BitSet
     *
     * @param bytes byte数组
     * @return
     */
    public static BitSet bytesToBitSet(byte[] bytes) {
        BitSet bs = new BitSet(bytes.length * 8);
        for (int i = 0; i < bytes.length; i++) {
            bs.set(i * 8, ((byte) ((bytes[i] >> 7) & 0x1)) == 1);
            bs.set(i * 8 + 1, ((byte) ((bytes[i] >> 6) & 0x1)) == 1);
            bs.set(i * 8 + 2, ((byte) ((bytes[i] >> 5) & 0x1)) == 1);
            bs.set(i * 8 + 3, ((byte) ((bytes[i] >> 4) & 0x1)) == 1);
            bs.set(i * 8 + 4, ((byte) ((bytes[i] >> 3) & 0x1)) == 1);
            bs.set(i * 8 + 5, ((byte) ((bytes[i] >> 2) & 0x1)) == 1);
            bs.set(i * 8 + 6, ((byte) ((bytes[i] >> 1) & 0x1)) == 1);
            bs.set(i * 8 + 7, ((byte) ((bytes[i] >> 0) & 0x1)) == 1);
        }
        return bs;
    }

    public static short bytesToShortH(byte[] bytes) {
        short s = (short) (((short) bytes[0] << 8) & 0xFF00);
        s |= bytes[1] & 0xFF;
        return s;
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexStringH(byte[] bytes) {
        return bytesToHexStringH(bytes, (String) null);
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes
     * @param separator
     * @return
     */
    public static String bytesToHexStringH(byte[] bytes, String separator) {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder(100);
            int byteLength = bytes.length;

            for (int i = 0; i < byteLength; ++i) {
                int val = bytes[i] & 255;
                String hexVal = Integer.toHexString(val);
                if (hexVal.length() == 1) {
                    sb.append("0");
                    sb.append(hexVal);
                } else {
                    sb.append(hexVal);
                }

                if (StringUtils.isNotEmpty(separator) && i < byteLength - 1) {
                    sb.append(separator);
                }
            }

            sb.trimToSize();
            return sb.toString();
        }
    }

    /**
     * bcd转asc码
     *
     * @param bytes
     * @return
     */
    public static String BCD2ASC(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            int h = ((bytes[i] & 0xf0) >>> 4);
            int l = (bytes[i] & 0x0f);
            temp.append(BToA[h]).append(BToA[l]);
        }
        return temp.toString();
    }

    /**
     * 获取指定长度的0xFF默认值字节数组
     *
     * @param size
     * @return
     */
    public static final byte[] getEmptyByte(int size) {
        byte[] BYTE_DEFAULT = new byte[size];
        for (int i = 0; i < size; i++) {
            BYTE_DEFAULT[i] = (byte) 0xff;
        }
        return BYTE_DEFAULT;
    }

    /**
     * 移除字节数组中有FF的字节
     *
     * @param bytes 源字节数组
     * @return 处理后的字节数组
     */
    public static final byte[] removeFF(byte[] bytes) {
        int size = bytes.length;
        int realsize = size;
        for (int i = size - 1; i >= 0; i--) {
            if (bytes[i] == Constant.BYTE_DEFAULT) {
                realsize = i;
            } else {
                break;
            }
        }

        logger.debug("realsize = {}", realsize);
        if (size == realsize) {
            return bytes;
        } else {
            byte[] bytesDest = new byte[realsize];
            for (int j = 0; j < realsize; j++) {
                bytesDest[j] = bytes[j];
            }
            return bytesDest;
        }
    }

    /**
     * 通过TBCD算法得到字符串
     *
     * @param bytes 字节数组
     * @return 解析后的字符串
     */
    public static final String getTBCD(byte[] bytes, int size) {
        if (size == 2 && Arrays.equals(bytes, Constant.BYTE2_DEFAULT)) {
            return StringUtils.EMPTY;
        } else if (size == 8 && Arrays.equals(bytes, Constant.BYTE8_DEFAULT)) {
            return StringUtils.EMPTY;
        } else if (size == 16 && Arrays.equals(bytes, Constant.BYTE16_DEFAULT)) {
            return StringUtils.EMPTY;
        } else if (Arrays.equals(bytes, getEmptyByte(size))) {
            return StringUtils.EMPTY;
        }
        try {
            bytes = removeFF(bytes);
            return TBCDUtil.toTBCD(bytes).toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("tbcd error. data = {}", bytesToHexStringH(bytes, " "));
            return StringUtils.EMPTY;
        }
    }

    /**
     * 字符串转ByteBuf
     *
     * @param msg
     * @return
     */
    public static ByteBuf strToByteBuf(String msg) {
        return Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
    }

    /**
     * Set进行异或操作
     *
     * @param vals
     * @param <T>
     * @return
     */
    public static <T> long bit_xor_vals_sets(Set<T> vals) {
        long rtn = 0L;
        for (T n : vals) {
            rtn = bit_xor(rtn, n);
        }
        return rtn;
    }

    /**
     * 异或操作
     *
     * @param a
     * @param b
     * @return
     */
    public static long bit_xor(Object a, Object b) {
        long rtn;

        if (a instanceof Long && b instanceof Long) {
            rtn = ((Long) a) ^ ((Long) b);
            return rtn;
        } else if (b instanceof Set) {
            long bs = bit_xor_vals_sets((Set) b);
            return bit_xor(a, bs);
        } else if (a instanceof Set) {
            long as = bit_xor_vals_sets((Set) a);
            return bit_xor(as, b);
        } else {
            long ai = Long.parseLong(String.valueOf(a));
            long bi = Long.parseLong(String.valueOf(b));
            rtn = ai ^ bi;
            return rtn;
        }
    }

    public static int byteToFormat(byte value) {
        int format;
        switch (value) {
            case (byte) 1:
                format = 1;
                break;
            case (byte) 2:
                format = 2;
                break;
            case (byte) 3:
                format = 3;
                break;
            case (byte) 4:
                format = 4;
                break;
            case (byte) 5:
                format = 5;
                break;
            case (byte) 6:
                format = 6;
                break;
            case (byte) 7:
                format = 8;
                break;
            case (byte) 8:
                format = 16;
                break;
            case (byte) 9:
                format = 32;
                break;
            case (byte) 10:
                format = 64;
                break;
            case (byte) 11:
                format = 128;
                break;
            case (byte) 12:
                format = 256;
                break;
            default:
                format = 0;
        }
        return format;
    }

    public static byte formatToByte(int length) {
        byte value;
        switch (length) {
            case 1:
                value = (byte) 1;
                break;
            case 2:
                value = (byte) 2;
                break;
            case 3:
                value = (byte) 3;
                break;
            case 4:
                value = (byte) 4;
                break;
            case 5:
                value = (byte) 5;
                break;
            case 6:
                value = (byte) 6;
                break;
            case 8:
                value = (byte) 7;
                break;
            case 16:
                value = (byte) 8;
                break;
            case 32:
                value = (byte) 9;
                break;
            case 64:
                value = (byte) 10;
                break;
            case 128:
                value = (byte) 11;
                break;
            case 256:
                value = (byte) 12;
                break;
            default:
                value = (byte) 0;
        }
        return value;
    }

    /**
     * 构造TLV格式数据
     *
     * @param tag
     * @param bytes
     * @return
     */
    public static byte[] buildTLV(int tag, byte[] bytes) {
        byte[] common = new byte[2];
        byte[] result;
        int length = bytes.length;
        int format = length;
        byte value = formatToByte(length);
        if (value == (byte) 0) format = 0;

        // tag低8位
        common[0] = (byte) (tag & 255);

        // Tag 12 bit，高6位为块索引，低6位为值区，这里不需要管索引块
        // Format 4bit
        // Tag 低8位(00+tag)
        // Format+Tag高4位(format低4位+0000)
        if (format == 0) {// T(2)L(2)V(L)
            // Format+Tag高4位(format低4位+0000)
            common[1] = (byte) 0x00;
            result = new byte[length + 4];
            // length被拆成了两段，前面是高8位，后面是低8位
            result[2] = (byte) (length >>> 8 & 255);
            result[3] = (byte) (length & 255);
            // 从result的第四位开始拷贝完整的bytes到result
            System.arraycopy(bytes, 0, result, 4, bytes.length);
        } else {// 退化为T(2)V(F)格式
            result = new byte[format + 2];
            // 获取Format的低四位，因为索引块是预留字段，全部为0即可
            common[1] = (byte) (value << 4 >>> 4);
//            //==============================
//            StringBuilder builder = new StringBuilder();
//            // 获取Format的低四位
//            String lowFormat = byteToLowBit(value);
//            // 获取Tag的高4位（高8位的低4位）
//            String highTag = byteToLowBit((byte) (tag >>> 8 & 255));
//            builder.append(lowFormat);
//            builder.append(highTag);
//            common[1] = bitToByte(builder.toString());
//            //==============================
            // 从result的第二位开始拷贝完整的bytes到result
            System.arraycopy(bytes, 0, result, 2, bytes.length);
        }
        System.arraycopy(common, 0, result, 0, common.length);
        return result;
    }

    /**
     * 获取低四位
     *
     * @param value
     * @return
     */
    public static String byteToLowBit(byte value) {
        return byteToBit(value).substring(4, 8);
    }

    /**
     * 获取低四位转成int
     *
     * @param value
     * @return
     */
    public static int getLowBitToInt(byte value) {
        return unsignedByteToInt((byte) (value << 4 >>> 4));
    }

    /**
     * 获取低四位byte
     *
     * @param value
     * @return
     */
    public static byte getLowBit(byte value) {
        return (byte) (value << 4 >>> 4);
    }

    /**
     * bit转byte
     *
     * @param str
     * @return
     */
    public static byte bitToByte(String str) {
        if (str.length() != 8) throw new NullPointerException("长度需要为8！");
        BitSet bitSet = new BitSet(8);
        for (int i = 0; i < 8; i++) {
            bitSet.set(i, str.charAt(i) == '1');
        }
        return bitSet2ByteArray(bitSet)[0];
    }
}
