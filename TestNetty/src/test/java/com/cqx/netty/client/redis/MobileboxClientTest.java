package com.cqx.netty.client.redis;

import org.junit.Test;

public class MobileboxClientTest {

    @Test
    public void start() throws Exception {
        new MobileboxClient().start("127.0.0.1", 1099);
        new MobileboxClient().start("127.0.0.1", 1099);
        new MobileboxClient().start("127.0.0.1", 1099);
        new MobileboxClient().start("127.0.0.1", 1099);
        new MobileboxClient().start("127.0.0.1", 1099);
    }
}