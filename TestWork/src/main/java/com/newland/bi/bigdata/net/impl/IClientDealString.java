package com.newland.bi.bigdata.net.impl;

import com.newland.bi.bigdata.net.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * IClientDealString
 *
 * @author chenqixu
 */
public class IClientDealString extends IClientDeal {

    private static final String LANG = "utf-8";
    private static Logger logger = LoggerFactory.getLogger(IClientDealString.class);
    private BufferedReader br = null;
    private PrintWriter pw = null;

    @Override
    public void newReader(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is, Charset.forName(LANG)));
    }

    @Override
    public void newWriter(OutputStream os) {
        pw = new PrintWriter(os, true);
    }

    @Override
    protected String read() throws IOException {
        return br.readLine();
    }

    @Override
    protected void write(Object value) {
        pw.println((String) value);
    }

    @Override
    protected void check(Object value) {
        throwNullException(br, "Reader is null ! please newReader first !");
        throwNullException(pw, "Writer is null ! please newWriter first !");
        if (!(value instanceof String)) {
            throw new UnsupportedOperationException("不支持的类型！");
        }
    }

    @Override
    public void close() {
        NetUtils.closeStream(br);
        NetUtils.closeStream(pw);
    }
}
