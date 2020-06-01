package com.cqx.netty.util;

import com.cqx.netty.service.redis.MobileboxServerHandler;
import io.netty.buffer.ByteBuf;
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
        //非内部类
        System.out.println(Utils.genrate(MobileboxServerHandler.class, null));
        //内部类
        System.out.println(Utils.genrate(TestIServerHandler.class, null));
    }

    class TestIServerHandler extends IServerHandler {
        @Override
        protected void init() {
        }

        @Override
        protected ByteBuf dealHandler(ByteBuf buf) {
            return null;
        }
    }
}