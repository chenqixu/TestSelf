package com.newland.bi.bigdata.net.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Socket;

/**
 * AcceptDealFile
 *
 * @author chenqixu
 */
public class AcceptDealFile extends IAcceptDeal {

    private static Logger logger = LoggerFactory.getLogger(AcceptDealFile.class);
    private IClientDeal<File> iClientDeal;

    public AcceptDealFile(Socket client) {
        super(client);
    }

    @Override
    public void init() throws Exception {
        iClientDeal = new IClientDealFile();
        iClientDeal.initServer(client);
    }

    @Override
    public void run() {
        try {
            iClientDeal.read();
            close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            iClientDeal.closeServer();
            closeSocket();
        }
    }
}
