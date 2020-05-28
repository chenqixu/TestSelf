package com.cqx.netty.client.redis;

import com.cqx.netty.bean.MobileboxBean;
import com.cqx.netty.util.IClient;
import com.cqx.netty.util.IClientHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔百盒Redis查询客户端
 *
 * @author chenqixu
 */
public class MobileboxClient {
    private IClient<String> iClient;

    public void start(String host, int port) throws Exception {
        iClient = IClient.newbuilder()
                .setHost(host)
                .setPort(port)
                .setiClientHandler(new IClientHandler() {
                    @Override
                    protected ByteBuf sendRequest() {
                        String id = getParams("id");
                        ByteBuf byteBuf = MobileboxBean.newbuilder().setHead(1).setBody(id.getBytes()).serialize();
                        return byteBuf;
                    }

                    @Override
                    protected void dealResponse(ByteBuf buf) {
                        MobileboxBean responseBean = new MobileboxBean(buf);
                        setResult(new String(responseBean.getBody()));
                        /**
                         * 释放同步锁
                         */
                        releaseSync();
                    }
                });
        Map<String, String> params = new HashMap<>();
        params.put("id", "004403000003101016127868F700D901");
        String result = iClient.queryBlocking(params);
        System.out.println("result：" + result);
    }
}
