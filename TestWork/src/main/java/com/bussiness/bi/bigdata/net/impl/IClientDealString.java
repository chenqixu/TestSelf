package com.bussiness.bi.bigdata.net.impl;

import com.bussiness.bi.bigdata.net.NetUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * IClientDealString
 *
 * @author chenqixu
 */
public class IClientDealString extends IClientDeal<String> {

    private static final String LANG = "utf-8";
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
    protected void write(String value) {
        pw.println(value);
    }

    @Override
    protected void check(String value) {
        throwNullException(br, "Reader is null ! please newReader first !");
        throwNullException(pw, "Writer is null ! please newWriter first !");
    }

    @Override
    public void closeClient() {
        NetUtils.closeStream(br);
        NetUtils.closeStream(pw);
    }

    @Override
    public void closeServer() {
        NetUtils.closeStream(br);
        NetUtils.closeStream(pw);
    }
}
