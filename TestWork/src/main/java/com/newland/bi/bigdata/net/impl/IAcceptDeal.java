package com.newland.bi.bigdata.net.impl;

import java.net.Socket;

/**
 * IAcceptDeal
 *
 * @author chenqixu
 */
public abstract class IAcceptDeal extends Thread {
    protected Socket client;
    protected boolean status;

    public IAcceptDeal(Socket client) {
        this.client = client;
    }

    public abstract void run();

    public void close() {
        status = false;
    }
}
