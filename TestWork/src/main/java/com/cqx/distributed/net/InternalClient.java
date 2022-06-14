package com.cqx.distributed.net;

import com.alibaba.fastjson.JSON;
import com.cqx.distributed.resource.ResourceServiceBean;
import com.cqx.netty.bean.NettyBaseBean;
import com.cqx.netty.util.IClient;
import com.cqx.netty.util.IClientHandler;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 内部客户端
 *
 * @author chenqixu
 */
public class InternalClient {
    private static final Logger logger = LoggerFactory.getLogger(InternalClient.class);
    private IClient iClient;

    public InternalClient() {
    }

    public void init(String host, int port) {
        iClient = IClient.newbuilder()
                .setHost(host)
                .setPort(port)
                .setiClientHandler(new RegisterClientHandler());
    }

    public void register(ResourceServiceBean resourceServiceBean) throws Exception {
        if (iClient != null) {
            Map<String, String> params = new HashMap<>();
            params.put("head", String.valueOf(ServerCodeEnum.Register.getServerCode()));
            params.put("body", JSON.toJSONString(resourceServiceBean));
            iClient.queryNoBlocking(params);
        }
    }

    class RegisterClientHandler extends IClientHandler {
        @Override
        protected ByteBuf channelReadSend() {
            ByteBuf byteBuf = null;
            //发送
            String _head = getParams("head");
            String _body = getParams("body");
            int head = Integer.valueOf(_head);
            switch (ServerCodeEnum.valueOf(head)) {
                case Register://注册
                    NettyBaseBean nettyBaseBean = new NettyBaseBean();
                    nettyBaseBean.setHead(head);
                    nettyBaseBean.setBody(_body);
                    byteBuf = nettyBaseBean.serialize();
                    break;
                default:
                    break;
            }
            return byteBuf;
        }

        @Override
        protected void dealResponse(ByteBuf buf) {
            //接收并处理
            NettyBaseBean nettyBaseBean = new NettyBaseBean(buf);
            String msg = nettyBaseBean.getBodyString();
            switch (ServerCodeEnum.valueOf(nettyBaseBean.getHead())) {
                case Success:
                    logger.info("Success，msg：{}", msg);
                    break;
                case Fail:
                    logger.info("Fail，msg：{}", msg);
                    break;
                default:
                    break;
            }
        }
    }
}
