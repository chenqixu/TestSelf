package com.cqx.common.utils.system;

import sun.net.util.IPAddressUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

/**
 * 网络工具
 *
 * @author chenqixu
 */
public class NetUtil {

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
            BitSet qbs = ByteUtil.bytesToBitSet(query_host.getAddress());
            BitSet sbs = ByteUtil.bytesToBitSet(source_host.getAddress());
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
}
