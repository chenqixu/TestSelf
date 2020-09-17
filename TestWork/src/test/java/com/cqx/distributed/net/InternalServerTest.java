package com.cqx.distributed.net;

import com.cqx.distributed.resource.ResourceServiceBean;
import com.cqx.netty.util.ICallBack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalServerTest {

    private static final Logger logger = LoggerFactory.getLogger(InternalServerTest.class);
    private InternalServer internalServer;
    private InternalClient internalClient;
    private int port = 11909;
    private String host = "127.0.0.1";

    @Before
    public void setUp() throws Exception {
        internalServer = new InternalServer();
        internalClient = new InternalClient(new ICallBack<String>() {
            @Override
            public void callBack(String s) {
                logger.info("callBack：{}", s);
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        internalServer.close();
    }

    @Test
    public void start() throws Exception {
        //启动服务端
        internalServer.start(port);
        //客户端初始化
        internalClient.init(host, port);
        //客户端发送 & 异步回调处理
        internalClient.register(new ResourceServiceBean());
    }
}