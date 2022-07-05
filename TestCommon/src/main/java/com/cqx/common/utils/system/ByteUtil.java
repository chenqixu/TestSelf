package com.cqx.common.utils.system;

import com.cqx.common.utils.Constant;
import com.cqx.common.utils.coder.TBCDUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.util.IPAddressUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

/**
 * 字节工具
 *
 * @author chenqixu
 */
public class ByteUtil {
    private static final char[] BToA = "0123456789abcdef&".toCharArray();
    private static final Logger logger = LoggerFactory.getLogger(ByteUtil.class);
    private static final Random random = new Random();
    private int[] fieldsLenArray = {2, 8, 8, 8, 8, 2, 1, 1, 1, 3, 1, 4, 5, 1, 1, 1, 1, 1, 1, 2, 1, 4, 5, 2, 5, 1, 2, 1, 4, 2, 2, 1, 2, 4, 4, 5, 3, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 4};
    private byte[] head = {0x00};

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
    public static String unsignedBytes(byte[] bytes) {
        return new BigInteger(bytes).toString();
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
     * 数组b1和数组b2相拼接
     *
     * @param b1
     * @param b2
     * @param b2Len
     * @return
     */
    public static byte[] arrayAdd(byte[] b1, byte[] b2, int b2Len) {
        byte[] n1 = new byte[b1.length + b2Len];
        System.arraycopy(b1, 0, n1, 0, b1.length);
        System.arraycopy(b2, 0, n1, b1.length, b2Len);
        return n1;
    }

    /**
     * 功能：判断一个IP是不是在一个网段下的
     * 格式：isInRange("192.168.8.3", "192.168.9.10/22");
     *
     * @param ip
     * @param cidr
     * @return
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8)
                | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);
        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 功能：判断一个IP是不是在一个网段下的</br>
     * 格式：isInRange("192.168.8.3", "192.168.9.10", 22);
     *
     * @param srcIp         数据里的IP
     * @param conditionIp   查询条件IP
     * @param conditionMask 查询条件IP的掩码位数
     * @return
     * @throws UnknownHostException
     */
    public static boolean isInRange(String srcIp, String conditionIp, int conditionMask) throws UnknownHostException {
        // 先判断是IPv4还是IPv6
        boolean isIPv4 = IPAddressUtil.isIPv4LiteralAddress(srcIp);
        boolean isIPv6 = IPAddressUtil.isIPv6LiteralAddress(srcIp);
        System.out.println(String.format("isIPv4：%s，isIPv6：%s", isIPv4, isIPv6));
        if (isIPv4) {
            InetAddress query_host = InetAddress.getByName(srcIp);
            InetAddress source_host = InetAddress.getByName(conditionIp);
            // 生成掩码
            int mask = 0xFFFFFFFF << (32 - conditionMask);
            int query = new BigInteger(query_host.getAddress()).intValue();
            int source = new BigInteger(source_host.getAddress()).intValue();
            // 判断和掩码与操作后的两个子网结果是否一致
            return (query & mask) == (source & mask);
        } else if (isIPv6) {
            InetAddress query_host = InetAddress.getByName(srcIp);
            InetAddress source_host = InetAddress.getByName(conditionIp);
            // 生成BitSet
            BitSet qbs = bytesToBitSet(query_host.getAddress());
            BitSet sbs = bytesToBitSet(source_host.getAddress());
            // 生成掩码
            BitSet mask = new BitSet();
            for (int i = 0; i < conditionMask; i++) {
                mask.set(i, true);
            }
            qbs.and(mask);// 和掩码做与操作
            sbs.and(mask);// 和掩码做与操作
            // 判断和掩码与操作后的两个子网结果是否一致
            return qbs.equals(sbs);
        }
        return false;
    }

    /**
     * BitSet转成byte数组
     *
     * @param bitSet
     * @return
     */
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.size() / 8];
        for (int i = 0; i < bitSet.size(); i++) {
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
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1 ? true : false);
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

    public static String bytesToHexStringH(byte[] bytes) {
        return bytesToHexStringH(bytes, null);
    }

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
            return TBCDUtils.toTBCD(bytes).toString();
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
}
