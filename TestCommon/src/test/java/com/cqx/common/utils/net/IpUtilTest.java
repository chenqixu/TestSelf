package com.cqx.common.utils.net;

import org.junit.Before;
import org.junit.Test;
import sun.net.util.IPAddressUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getIp() throws Exception {
        byte[] ipbytes = new byte[16];
        for (int i = 0; i < 12; i++) {
            ipbytes[i] = (byte) 0xff;
        }

        String ipv4 = "10.1.8.200";
        byte[] ipv4_bytes = IPAddressUtil.textToNumericFormatV4(ipv4);
        ipbytes[12] = ipv4_bytes[0];
        ipbytes[13] = ipv4_bytes[1];
        ipbytes[14] = ipv4_bytes[2];
        ipbytes[15] = ipv4_bytes[3];

        System.out.println(InetAddress.getByAddress(ipbytes));
    }

    @Test
    public void isIPv4InRange() throws UnknownHostException {
        Rule rule = new Rule();
        System.out.println(IpUtil.isIPv4InRange("10.1.8.203", "10.1.8.204", rule.getMask()));
    }

    class Rule {
        int mask;

        public int getMask() {
            return mask;
        }

        public void setMask(int mask) {
            this.mask = mask;
        }
    }
}