package com.cqx.netty.util;

import com.cqx.netty.service.redis.MobileboxServerHandler;
import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

    private Utils utils;

    @Before
    public void setUp() {
        utils = new Utils();
    }

    @Test
    public void readBuf() {
    }

    @Test
    public void writeBuf() {
        utils.writeBuf();
    }

    @Test
    public void classTest() throws InstantiationException, IllegalAccessException {
        utils.classTest();
    }

    @Test
    public void genrate() throws Exception {
        Utils.genrate(MobileboxServerHandler.class, null);
    }
}