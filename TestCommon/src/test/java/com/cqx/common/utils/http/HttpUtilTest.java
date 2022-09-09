package com.cqx.common.utils.http;

import org.junit.Test;

import java.util.HashMap;

public class HttpUtilTest {

    @Test
    public void doGet() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.doGet("http://127.0.0.1:19090/nl-edc-cct-sys-ms-dev/v1/session/code?data=你好");
    }

    @Test
    public void doPost() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.doPost("http://127.0.0.1:19090/nl-edc-cct-sys-ms-dev/v1/session/code?data=你好", new HashMap<>(), "GBK");
    }

    @Test
    public void doPut() {
    }
}