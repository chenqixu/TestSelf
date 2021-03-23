package com.newland.bi.bigdata.net.udp;

import org.junit.Test;

import java.io.IOException;

public class UDPClientAPITest {

    @Test
    public void sendData() throws Exception {
        UDPClientAPI udpClientAPI = new UDPClientAPI("112.5.185.70", 500);
        udpClientAPI.simple("");
    }
}