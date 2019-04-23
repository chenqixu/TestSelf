package com.newland.bi.bigdata.net.impl;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * AcceptDealString
 *
 * @author chenqixu
 */
public class AcceptDealString extends IAcceptDeal {

    public static final String LANG = "utf-8";
    private static Logger logger = LoggerFactory.getLogger(AcceptDealString.class);
    private BufferedReader br = null;
    private PrintWriter pw = null;

    public AcceptDealString(Socket client) {
        super(client);
        logger.info("{} accetp client：{}", this, client);
        try {
            br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    Charset.forName(LANG)));
            pw = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        status = true;
    }

    @Override
    public void run() {
        String content;
        try {
            while (status) {
                while ((content = br.readLine()) != null) {
                    logger.info("client：{}，read content：{}", client, content);
                    pw.println(content + "|replay");
                }
                SleepUtils.sleepMilliSecond(50);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (pw != null)
                pw.close();
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
