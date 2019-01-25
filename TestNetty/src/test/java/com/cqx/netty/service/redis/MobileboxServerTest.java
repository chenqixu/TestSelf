package com.cqx.netty.service.redis;

public class MobileboxServerTest {

    @org.junit.Test
    public void start() throws Exception {
        new MobileboxServer().start(1099);
    }
}