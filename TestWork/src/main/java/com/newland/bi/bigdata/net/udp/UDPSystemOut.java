package com.newland.bi.bigdata.net.udp;

/**
 * UDPSystemOut
 *
 * @author chenqixu
 */
public class UDPSystemOut implements IUDPListener {

    @Override
    public void onReceiveData(byte[] val) {
        System.out.println(new String(val));
    }
}
