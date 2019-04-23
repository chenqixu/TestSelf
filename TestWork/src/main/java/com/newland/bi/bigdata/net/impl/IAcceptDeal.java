package com.newland.bi.bigdata.net.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * IAcceptDeal
 *
 * @author chenqixu
 */
public abstract class IAcceptDeal extends Thread {

    private static Logger logger = LoggerFactory.getLogger(IAcceptDeal.class);
    protected Socket client;
    protected boolean status;

    public IAcceptDeal(Socket client) {
        this.client = client;
        this.status = true;
        logger.info("{} accetp client：{}", this, client);
    }

    public abstract void init() throws Exception;

    public abstract void run();

    protected void closeSocket() {
        if (client != null) {
            try {
                logger.info("disconnect，client：{}", client);
                client.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void close() {
        status = false;
    }
}
