package com.cqx.common.utils.net;

import sun.net.util.IPAddressUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

/**
 * Ip工具类
 *
 * @author chenqixu
 */
public class IpUtil {

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
        if (isIPv4) {// IPv4
            InetAddress query_host = InetAddress.getByName(srcIp);
            InetAddress source_host = InetAddress.getByName(conditionIp);
            // 生成掩码
            int mask = 0xFFFFFFFF << (32 - conditionMask);
            int query = new BigInteger(query_host.getAddress()).intValue();
            int source = new BigInteger(source_host.getAddress()).intValue();
            // 判断和掩码与操作后的两个子网结果是否一致
            return (query & mask) == (source & mask);
        } else if (isIPv6) {// IPv6
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
     * IPv4掩码判断<br>
     * 功能：判断一个IP是不是在一个网段下的</br>
     * 格式：isInRange("192.168.8.3", "192.168.9.10", 22);
     *
     * @param srcIp         数据里的IP
     * @param conditionIp   查询条件IP
     * @param conditionMask 查询条件IP的掩码位数
     * @return
     * @throws UnknownHostException
     */
    public static boolean isIPv4InRange(String srcIp, String conditionIp, int conditionMask) throws UnknownHostException {
        InetAddress query_host = InetAddress.getByName(srcIp);
        InetAddress source_host = InetAddress.getByName(conditionIp);
        // 生成掩码
        int mask = 0xFFFFFFFF << (32 - conditionMask);
        int query = new BigInteger(query_host.getAddress()).intValue();
        int source = new BigInteger(source_host.getAddress()).intValue();
        // 判断和掩码与操作后的两个子网结果是否一致
        return (query & mask) == (source & mask);
    }

    /**
     * IPv6掩码判断<br>
     * 功能：判断一个IP是不是在一个网段下的</br>
     * 格式：isInRange("192.168.8.3", "192.168.9.10", 22);
     *
     * @param srcIp         数据里的IP
     * @param conditionIp   查询条件IP
     * @param conditionMask 查询条件IP的掩码位数
     * @return
     * @throws UnknownHostException
     */
    public static boolean isIPv6InRange(String srcIp, String conditionIp, int conditionMask) throws UnknownHostException {
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
}
