package com.bussiness.bi.bigdata.net.bean;

import java.io.Serializable;

/**
 * NetBean
 *
 * @author chenqixu
 */
public class NetBean implements Serializable {
    private int head;
    private NetBody netBody;

    public static NetBean newbuilder() {
        return new NetBean();
    }

    public int getHead() {
        return head;
    }

    public NetBean setHead(int head) {
        this.head = head;
        return this;
    }

    public NetBody getNetBody() {
        return netBody;
    }

    public NetBean setNetBody(NetBody netBody) {
        this.netBody = netBody;
        return this;
    }

    public String toString() {
        return "head：" + head + "，netBody：" + netBody;
    }
}
