package com.newland.bi.bigdata.net.impl;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * AcceptDealString
 *
 * @author chenqixu
 */
public class AcceptDealString extends IAcceptDeal {

    private static Logger logger = LoggerFactory.getLogger(AcceptDealString.class);
    private IClientDeal<String> iClientDeal;

    public AcceptDealString(Socket client) {
        super(client);
    }

    @Override
    public void init() throws Exception {
        iClientDeal = new IClientDealString();
        iClientDeal.initClient(client);
    }

    @Override
    public void run() {
        String content;
        try {
            while (status) {
                while ((content = iClientDeal.read()) != null) {
                    logger.info("client：{}，read content：{}", client, content);
                    iClientDeal.write(content + "|replay");
                }
                SleepUtils.sleepMilliSecond(50);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            iClientDeal.closeServer();
            closeSocket();
        }
    }

}
