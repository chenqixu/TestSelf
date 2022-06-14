package com.bussiness.bi.bigdata.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * IpUtil
 *
 * @author chenqixu
 */
public class IpUtil {

    public static String ip = null;
    public static String hostName = null;
    private static Logger logger = LoggerFactory.getLogger(IpUtil.class);

    static {
        init();
    }

    public static void init() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress().toString();
            hostName = addr.getHostAddress().toString();
        } catch (Exception e) {
            logger.error("无法获取IP地址", e);
            ip = "无法获取IP地址";
        }
    }
}
