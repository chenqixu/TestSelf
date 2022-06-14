package com.bussiness.bi.bigdata.net.udp;

import com.bussiness.bi.bigdata.net.udp.UDPClientAPI;
import org.junit.Test;

public class UDPClientAPITest {

    @Test
    public void sendData() throws Exception {
        UDPClientAPI udpClientAPI = new UDPClientAPI("112.5.185.70", 500);
        udpClientAPI.simple("");
    }
}