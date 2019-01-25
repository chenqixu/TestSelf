package com.cqx.netty.service.redis;

import com.cqx.netty.util.IServer;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔百盒Redis缓存服务
 * <pre>
 *     1、定时缓存Redis数据到内存
 *     2、提供查询服务
 * </pre>
 *
 * @author chenqixu
 */
public class MobileboxServer {

    public void start(int port) throws Exception {
        Map<String, String> params = new HashMap<>();
        IServer.newbuilder()
                .setPort(port)
                .setParams(params)
                .setCls(MobileboxServerHandler.class)
                .start();
    }

}
