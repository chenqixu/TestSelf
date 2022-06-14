package com.bussiness.bi.bigdata.net.udp;

/**
 * IUDPListener
 *
 * @author chenqixu
 */
public interface IUDPListener {

    /**
     * 通过监听器回调有效数据
     *
     * @param val
     */
    void onReceiveData(byte[] val);
}
