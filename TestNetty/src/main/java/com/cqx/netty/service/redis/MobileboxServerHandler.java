package com.cqx.netty.service.redis;

import com.cqx.netty.bean.MobileboxBean;
import com.cqx.netty.util.IServerHandler;
import com.cqx.netty.util.MemoryCache;
import io.netty.buffer.ByteBuf;

/**
 * 服务端处理类
 *
 * @author chenqixu
 */
public class MobileboxServerHandler extends IServerHandler {

    MemoryCache memoryCache;

    @Override
    protected void init() {
        memoryCache = new MemoryCache();
        memoryCache.init();
    }

    @Override
    protected ByteBuf dealHandler(ByteBuf buf) {
        MobileboxBean responseBean = MobileboxBean.newbuilder().setHead(2);
        MobileboxBean requestBean = new MobileboxBean(buf);
        if (requestBean.getHead() == 1) {
            responseBean.setBody(memoryCache.getCache().get(
                    new String(requestBean.getBody())).getBytes());
        }
        return responseBean.serialize();
    }
}
